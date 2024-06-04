(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.congruences :as c]
            [vk.ntheory.arithmetic-functions :as f]))

(defn check-prime-to-mod
  [m a]
  (b/check-int-pos m)
  (b/check-int-non-neg a)
  (b/check-relatively-prime m a)
  (mod a m))

;; Reduced residues
(defn classify-residues
  "Dispatch function for reduced residues"
  [m]
  (let [[[_ a1] [p2 _]] (p/int->factors-count m)]
    (cond
      (= m 1) ::mod-1
      (and (nil? p2) (= a1 1)) ::mod-any-p
      (and (nil? p2) (> a1 1)) ::mod-any-p**e
      :else ::composite)))

(defmulti reduced-residues
  "Returns reduced residues modulo m"
  classify-residues)

(defn reduced-residues'
  "Brute force implemetation of reduced-residues'"
  [m]
  (->> (range 0 m)
       (filter #(= 1 (b/gcd m %)))))

(defmethod reduced-residues ::mod-1
  [m]
  [0])

(defmethod reduced-residues ::mod-any-p
  [m]
  (range 1 m))

;; Optimized version with concat and lazy-seq doesn't have performance imporovement
(defmethod reduced-residues ::mod-any-p**e
  [m]
  (let [[[p _]] (p/int->factors-count m)]
    (->> (range 1 m)
         (remove #(b/divides? p %)))))

(defmethod reduced-residues ::composite
  [m]
  (let [cn (p/int->factors-count m)
        xss (mapv (fn [[p k]] (reduced-residues (b/pow p k))) cn)
        A (mapv (fn [[p k]] (/ m (b/pow p k))) cn)]
    (for [x (b/product xss)]
      (apply (partial b/mod-add m) (map #(b/mod-mul m %1 %2) x A)))))

(defn order
  "Find multiplicative order of given integer `a`."
  [m a]
  (check-prime-to-mod m a)
  (->> m
       f/totient
       f/divisors
       sort
       (filter #(b/congruent? m 1 (b/mod-pow m a %)))
       first))

(defn print-table-residue-order
  "Print order table modulo `m`"
  [m]
  (pp/print-table [:residue :order]
                  (map (fn [r] {:residue r :order (order m r)})
                       (sort (reduced-residues m)))))

;; Todo: consider optiomization for prime moduli, for prime order
;; count is phi(d) where d|p-1
(defn order-count
  "Return map where key is order and value is
  count of classes/residues with this order."
  [m]
  (frequencies (map (partial order m) (reduced-residues m))))

(defn print-table-order-count
  [m]
  (pp/print-table [:order :count]
   (map (fn [[k v]] {:order k :count v})(sort-by first (order-count m))))
  )

(defn classify-modulo
  [m & _]
  (let [[[p1 a1] [p2 _] [p3 _]] (p/int->factors-count m)]
    (cond
      (= m 1) ::mod-1
      (= m 2) ::mod-2
      (= m 4) ::mod-4
      (and (nil? p2) (> p1 2) (= a1 1)) ::mod-p
      (and (nil? p2) (> p1 2) (> a1 1)) ::mod-p**e
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3)) ::mod-2p**e
      (and (= p1 2) (>= a1 3) (nil? p2)) ::mod-2**e
      :else ::composite)
    ))

(derive ::mod-1 ::has-primitive-root)
(derive ::mod-2 ::has-primitive-root)
(derive ::mod-4 ::has-primitive-root)
(derive ::mod-p ::has-primitive-root)
(derive ::mod-p**e ::has-primitive-root)
(derive ::mod-2p**e ::has-primitive-root)
(derive ::mod-2**e ::no-primitive-root)
(derive ::composite ::no-primitive-root)

(defn has-primitive-root?
  [m]
  (isa? (classify-modulo m) ::has-primitive-root))

(defn check-has-primitive-root
  [m]
  (b/check-predicate has-primitive-root? m (format "Modulo %s has not primitive root" m)))

(defn primitive-root?
  "Check does a is primitive root modulo m"
  [m a]
  (check-prime-to-mod m a)
  (if (has-primitive-root? m) ;; optimization for modulo without primitive root
   (let [phi (f/totient m)]
    (->> phi
         (p/int->factors-distinct)
         (map #(/ phi %))
         (map #(b/mod-pow m a %))
         (every? #(not (b/congruent? m 1 %)))))
   false))

(defn check-primitive-root
  [m g]
  (b/check-predicate (partial primitive-root? m)
                     g
                     (format "Value %s is not primitive root modulo %s" g m)))

(defmulti find-primitive-root classify-modulo)

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
        c (b/mod-pow (* p p) g (dec p))]
    (if (b/congruent? m 1 c)
      (+ g p)
      g)))

(defmethod find-primitive-root ::mod-2p**e
  [m]
  (let [[[_ _] [p a]] (p/int->factors-count m)
        g (find-primitive-root (b/pow p a))]
    (if (odd? g) g
        (+ g (b/pow p a)))))

(defmethod find-primitive-root ::no-primitive-root
  [m]
  nil)

(defn primitive-roots
  [m]
  (if-let [g (find-primitive-root m)]
    (map #(b/mod-pow m g %) (reduced-residues (f/totient m)))
    []))

;; HERE
;; Power residues
(defn power-residue?'
  "Brute force implemntation of power-residue?"
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (letfn [(f [x] (b/mod-pow m x n))]
    (true? (some #(b/congruent? m a (f %)) (reduced-residues' m)))))

(defn solve-power-residue'
  "Brute force implementation of solve-power-residue"
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (into (sorted-set) (for [x (reduced-residues' m)
                           :let [xn (b/mod-pow m x n)]
                           :when (b/congruent? m xn a)] x)))

(defn power-residues'
  "Brute force implementation of power-residues"
  [m n]
  (b/check-int-pos m)
  (b/check-int-pos n)
  (distinct (map #(b/mod-pow m % n) (reduced-residues' m))))

(defmulti power-residue? classify-modulo :default ::composite)

(defmethod power-residue? ::has-primitive-root
  ;; Case modulo m has a primitive root.
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (let [phi (f/totient m)
        d (b/gcd n phi)
        t (b/mod-pow m a (/ phi d))]
    (b/congruent? m 1 t)))

(defmethod power-residue? ::mod-2**e
  ;; Case modulo m for modulo 2^e, where e >= 3."
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (if (odd? n)
    true ;;solution always exists and unique
    (if (= (mod a 4) 1)
      (let [[[_ e]] (p/int->factors-count m)
            m' (b/pow 2 (- e 2))
            d (b/gcd n m')
            t (b/mod-pow m a
                     (/ m' d))]
        (b/congruent? m 1 t))
      false)))

(defmethod power-residue? ::composite
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (every? #(power-residue? % n a)
          (for [[p e] (p/int->factors-count m)] (b/pow p e))))

(defn index
  ([m a] (index m (find-primitive-root m) a))
  ([m g a]
   (check-has-primitive-root m)
   (check-prime-to-mod m a)
   (check-primitive-root m g)
   (loop [acc g
          ind 1]
     (if (b/congruent? m acc a)
       ind
       (recur (b/mod-mul m acc g) (inc ind))))))

(defmulti solve-power-residue classify-modulo :default ::composite)

(defmethod solve-power-residue ::has-primitive-root
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (let [g (find-primitive-root m)
        b (index m a)
        phi (f/totient m)
        xs (c/solve-linear n b phi)]
    (->> xs (map (partial b/mod-pow m g)) (apply sorted-set))))

(defmulti power-residues classify-modulo :default ::composite)

(defmethod power-residues ::has-primitive-root
  [m n]
  (let [g (find-primitive-root m)
        phi (f/totient m)
        d (b/gcd n phi)]
    (map (partial b/mod-pow m g) (range 0 phi d))))

(defn mod-2**e-check-modulo
  "Check than m is 2^n, where n >= 3. Returns n."
  [m]
  (b/check-int-pos m)
  (let [[[p1 a1] [p2 _]] (p/int->factors-count m)]
    (if (and (= p1 2) (>= a1 3) (nil? p2))
      a1
      (throw (IllegalArgumentException. "Expected module 2^n where n >= 3")))))

(defn mod-2**e-index->residue
  [m [u v]]
  (mod (* (b/pow -1 u)
          (b/mod-pow m 5 v)) m))

(defn mod-2**e-indices
  "Returns residues modulo 2^n, where n >= 3."
  [m]
  (let [e (mod-2**e-check-modulo m)]
    (for [u [0 1]
          v (range 0 (b/pow 2 (- e 2)))]
      [u v])))

(defn mod-2**e-index
  [m a]
  (let [e (mod-2**e-check-modulo m)
        a' (check-prime-to-mod m a)]
    (first (filter #(b/congruent? m a' (mod-2**e-index->residue m %))  (mod-2**e-indices m)))))

(defmethod power-residues ::mod-2**e
  [m n]
  (b/check-int-pos n)
  (letfn [(odd-n [m n]
            (reduced-residues m))
          (even-n [m n]
            (let [[[_ e]] (p/int->factors-count m)
                  m' (b/pow 2 (- e 2))
                  d (b/gcd n m')]
              (for [z (range 0 m' d)]
                (b/mod-pow m 5 z))))]
    (if (odd? n)
      (odd-n m n)
      (even-n m n))))

(defmethod power-residues ::composite
  [m n]
  (filter (partial power-residue? m n) (reduced-residues m)))

(defmethod solve-power-residue ::mod-2**e
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (letfn [(solve-odd-n [m n a]
            (let [[s t] (mod-2**e-index m a)
                  [[_ e]] (p/int->factors-count m)
                  m' (b/pow 2 (- e 2))
                  d (b/gcd n m')
                  z (first (c/solve-linear n t m'))
                  x (b/mod-mul m (b/mod-pow m (- 1) s) (b/mod-pow m 5 z))]
              (sorted-set x)))
          (solve-even-n [m n a]
            (let [[s t] (mod-2**e-index m a)]
              (if (= s 0)
                (let [[[_ e]] (p/int->factors-count m)
                      m' (b/pow 2 (- e 2))
                      d (b/gcd n m')
                      zs (c/solve-linear n t m')]
                  (for [y [0 1]
                        z zs]
                    (b/mod-mul m (b/mod-pow m (- 1) y) (b/mod-pow m 5 z))))
                (sorted-set))))]
    (if (odd? n)
      (into (sorted-set) (solve-odd-n m n a))
      (into (sorted-set) (solve-even-n m n a)))))

(defmethod solve-power-residue ::composite
  [m n a]
  (->> (for [[p e] (p/int->factors-count m)
             :let [m' (b/pow p e)]]
         (map #(vector % m') (solve-power-residue m' n a)))
       b/product
       (map c/solve-coprime-remainders)
       (map first)
       (apply sorted-set)))
