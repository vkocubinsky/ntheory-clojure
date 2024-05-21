(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.congruences :as c]
            [vk.ntheory.arithmetic-functions :as af]))

(defn check-prime-to-mod
  [m a]
  (b/check-int-pos m)
  (b/check-int-non-neg a)
  (b/check-relatively-prime m a)
  (mod a m))

(defn order
  "Find multiplicative order of given integer `a`."
  [m a]
  (check-prime-to-mod m a)
  (->> m
       af/totient
       af/divisors
       sort
       (filter #(= 1 (b/m** m a %)))
       first))

;; Reduced residues
(defn classify-residues
  [m]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count m)]
    (cond
      (= m 1) ::mod-1
      (and (nil? p2) (= a1 1)) ::mod-p 
      (and (nil? p2) (> a1 1)) ::mod-p**e 
      )))

(defmulti reduced-residues classify-residues :default ::composite)

(defmethod reduced-residues ::mod-1
  [m]
  '(0))

(defmethod reduced-residues ::mod-p
  [m]
  (range 1 m))

;; Optimized version with concat and lazy-seq doesn't have performance imporovement
(defmethod reduced-residues ::mod-p**e
  [m]
  (let [[[p a]] (p/int->factors-count m)]
    (->> (range 1 m)
         (remove #(b/divides? p %)))))

(defmethod reduced-residues ::composite
  [m]
  (let [cn (p/int->factors-count m)
        xss (mapv (fn [[p k]] (reduced-residues (b/pow p k))) cn)
        A (mapv (fn [[p k]] (/ m (b/pow p k))) cn)]
    (for [x (b/product xss)]
      (apply (partial b/m+ m) (map #(b/m* m %1 %2) x A)))))


(defn classify-modulo
  [m & _]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count m)]
    (cond
      (= m 1) ::mod-1
      (= m 2) ::mod-2
      (= m 4) ::mod-4
      (and (nil? p2) (> p1 2) (= a1 1)) ::mod-p 
      (and (nil? p2) (> p1 2) (> a1 1)) ::mod-p**e
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3)) ::mod-2p**e
      (and (= p1 2) (>= a1 3) (nil? p2)) ::mod-2**e))
  )

(derive ::mod-1 ::has-primitive-root)
(derive ::mod-2 ::has-primitive-root)
(derive ::mod-4 ::has-primitive-root)
(derive ::mod-p ::has-primitive-root)
(derive ::mod-p**e ::has-primitive-root)
(derive ::mod-2p**e ::has-primitive-root)

(defn has-primitive-root?
  [m]
  (isa? (classify-modulo m) ::has-primitive-root))

(defn check-has-primitive-root
  [m]
  (b/check-predicate has-primitive-root? m (format "Modulo %s has not primitive root" m)))

(defn primitive-root?
  "Check does a is primitive root modulo m"
  [m a]
  (check-has-primitive-root m)
  (check-prime-to-mod m a)
  (let [phi (af/totient m)]
    (->> phi
         (p/int->factors-distinct)
         (map #(/ phi %))
         (map #(b/m** m a %))
         (every? #(not (= 1 %))))))

(defn check-primitive-root
  [m g]
  (b/check-predicate (partial primitive-root? m)
                     g
                     (format "Value %s is not primitive root modulo %s" g m)))

(defmulti find-primitive-root classify-modulo :default ::composite)

(defmethod find-primitive-root ::mod-1
  [m]
  0)

(defmethod find-primitive-root ::mod-2
  [m]
  1)

(defmethod find-primitive-root ::mod-4
  [m]
  3)

(defmethod find-primitive-root ::mod-p
  [m]
  (->> (range 1 m)
       (filter #(primitive-root? m %))
       first))

(defmethod find-primitive-root ::mod-p**e
  [m]
  (let [[[p _]] (p/int->factors-count m)
        g (find-primitive-root p)
        c (b/m** (* p p) g (dec p))]
    (if (= 1 c)
      (+ g p)
      g)))

(defmethod find-primitive-root ::mod-2p**e
  [m]
  (let [[[_ _] [p a]] (p/int->factors-count m)
        g (find-primitive-root (b/pow p a))]
    (if (odd? g) g
        (+ g (b/pow p a)))))

(defmethod find-primitive-root ::composite
  [m]
  nil)

(defn get-primitive-root
  [m]
  (if-let [g (find-primitive-root m)]
    g
    (throw (Exception. "Modulo doesn't have primitive root"))))

(defn primitive-roots
  [m]
  (if-let [g (find-primitive-root m)]
    (map #(b/m** m g %) (reduced-residues (af/totient m)))
    []))

;; Brute force implementation
(defn reduced-residues'
  [m]
  (->> (range 1 m)
       (filter #(= 1 (b/gcd m %)))))

(defn primitive-roots'
  "Brute force version of search primitive roots."
  [m]
  (b/check-int-pos m)
  (->> (reduced-residues' m)
       (filter #(primitive-root? m %))))

;; Power residues
;; (defmulti solve-power-residue)
;; (defmulti power-residues)

(defmulti power-residue? classify-modulo)

(defmethod power-residue? ::has-primitive-root
  ;; Case modulo m has a primitive root.
  [m n a]
  (check-prime-to-mod m a)
  (let [phi (af/totient m)
        d (b/gcd n phi)
        t (b/m** m a (/ phi d))]
    (= 1 t)))

(defmethod power-residue? ::mod-2**e
  ;; Case modulo m for modulo 2^e, where e >= 3."
  [m n a]
  (check-prime-to-mod m a)
  (if (odd? n)
    true ;;solution always exists and unique
    (if (= (mod a 4) 1)
      (let [[[_ e]] (p/int->factors-count m)
            m' (b/pow 2 (- e 2))
            d (b/gcd n m')
            t (b/m** m a
                     (/ m' d))]
        (= 1 t))
      false)))

(defmethod power-residue? ::composite
  [m n a]
  ;; 
  "not implemented")

(defn index
  ([m a] (index m (get-primitive-root m) a))
  ([m g a]
   (check-prime-to-mod m a)
   (check-primitive-root m g)
   (loop [acc g
          ind 1]
     (if (= acc a)
       ind
       (recur (b/m* m acc g) (inc ind))))))

(defmulti solve-power-residue classify-modulo :default ::composite)

(defmethod solve-power-residue ::has-primitive-root
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (let [g (get-primitive-root m)
        b (index m a)
        phi (af/totient m)
        xs (c/solve-linear n b phi)]
    (->> xs (map (partial b/m** m g)) (apply sorted-set))))

(defn power-residues
  [m n]
  (let [g (get-primitive-root m)
        phi (af/totient m)
        d (b/gcd n phi)]
    (map (partial b/m** m g) (range 0 phi d))))

(defn m2n-check-modulo
  "Check than m is 2^n, where n >= 3. Returns n."
  [m]
  (b/check-int-pos m)
  (let [[[p1 a1] [p2 a2]] (p/int->factors-count m)]
    (if (and (= p1 2) (>= a1 3) (nil? p2))
      a1
      (throw (IllegalArgumentException. "Expected module 2^n where n >= 3")))))

(defn m2n-index->residue
  [m [u v]]
  (mod (* (b/pow -1 u)
          (b/m** m 5 v)) m))

(defn m2n-indices
  "Returns residues modulo 2^n, where n >= 3."
  [m]
  (let [e (m2n-check-modulo m)]
    (for [u [0 1]
          v (range 0 (b/pow 2 (- e 2)))]
      [u v])))

(defn m2n-index
  [m a]
  (let [e (m2n-check-modulo m)
        a' (check-prime-to-mod m a)]
    (first (filter #(= a' (m2n-index->residue m %))  (m2n-indices m)))))

(defmethod solve-power-residue ::mod-2**e
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (letfn [(solve-odd-n [m n a]
            (let [[s t] (m2n-index m a)
                  [[_ e]] (p/int->factors-count m)
                  m' (b/pow 2 (- e 2))
                  d (b/gcd n m')
                  z (first (c/solve-linear n t m'))
                  x (b/m* m (b/m** m (- 1) s) (b/m** m 5 z))]
              (sorted-set x)))
          (solve-even-n [m n a]
            (let [[s t] (m2n-index m a)]
              (if (= s 0)
                (let [[[_ e]] (p/int->factors-count m)
                      m' (b/pow 2 (- e 2))
                      d (b/gcd n m')
                      zs (c/solve-linear n t m')]
                  (for [y [0 1]
                        z zs]
                    (b/m* m (b/m** m (- 1) y) (b/m** m 5 z))
                    )
                  )
                (sorted-set))))]
    (if (odd? n)
      (solve-odd-n m n a)
      (solve-even-n m n a))))

(defmethod solve-power-residue ::composite
  [m n a]
  "not implemented")
