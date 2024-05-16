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
  (b/check-relatively-prime a m))

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

(defn classify-residues
  [m]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count m)]
    (cond
      (= m 1) :1
      (and (nil? p2) (= a1 1)) :prime ;; p
      (and (nil? p2) (> a1 1)) :prime-power ;; p^a
      )))

;; Move to primes?
(defmulti reduced-residues classify-residues :default :composite)

(defmethod reduced-residues :1
  [m]
  '(0))

(defmethod reduced-residues :prime
  [m]
  (range 1 m))

;; Optimized version with concat and lazy-seq doesn't have performance imporivement
(defmethod reduced-residues :prime-power
  [m]
  (let [[[p a]] (p/int->factors-count m)]
    (->> (range 1 m)
         (remove #(b/divides? p %)))))

(defmethod reduced-residues :composite
  [m]
  (let [cn (p/int->factors-count m)
        xss (mapv (fn [[p k]] (reduced-residues (b/pow p k))) cn)
        A (mapv (fn [[p k]] (/ m (b/pow p k))) cn)]
    (for [x (b/product xss)]
      (apply (partial b/m+ m) (map #(b/m* m %1 %2) x A)))))


(defn primitive-root?
  [m a]
  (check-prime-to-mod m a)
  (let [phi-p (af/totient m)]
    (->> phi-p
         (p/int->factors-distinct)
         (map #(/ phi-p %))
         (map #(b/m** m a %))
         (every? #(not (= 1 %))))))

(defn classify-modulo
  [m]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count m)]
    (cond
      (= m 1) :1
      (= m 2) :2
      (= m 4) :4
      (and (nil? p2) (> p1 2) (= a1 1)) :odd-prime ;; p
      (and (nil? p2) (> p1 2) (> a1 1)) :odd-prime-power ;; p^a
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3)) :2-odd-prime-power)))

(defmulti find-primitive-root classify-modulo)

(defmethod find-primitive-root :1
  [m]
  0)

(defmethod find-primitive-root :2
  [m]
  1)

(defmethod find-primitive-root :4
  [m]
  3)

(defmethod find-primitive-root :odd-prime
  [m]
  (->> (range 1 m)
       (filter #(primitive-root? m %))
       first))

(defmethod find-primitive-root :odd-prime-power
  [m]
  (let [[[p a]] (p/int->factors-count m)
        g (find-primitive-root p)
        c (b/m** (* p p) g (dec p))]
    (if (= 1 c)
      (+ g p)
      g)))

(defmethod find-primitive-root :2-odd-prime-power
  [m]
  (let [[[_ _] [p a]] (p/int->factors-count m)
        g (find-primitive-root (b/pow p a))]
    (if (odd? g) g
        (+ g (b/pow p a)))))

(defmethod find-primitive-root :default
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


(defn power-residue?
  [m n a]
  (b/check-relatively-prime a m)
  (let [g (get-primitive-root m)
          phi (af/totient m)
          d (b/gcd n phi)
        t (mod (b/m** m a (/ phi d)) n)]
      (= 1 t))
  )

(defn index
  [m a]
  (check-prime-to-mod m a)
  (let [g (get-primitive-root m)]
    (loop [acc g
           ind 1]
      (if (= acc a)
        ind
        (recur (b/m* m acc g) (inc ind))))))

(defn solve-power-residue
  [m n a]
  (check-prime-to-mod m a)
  (b/check-int-pos n)
  (let [g (get-primitive-root m)
        b (index m a)
        phi (af/totient m)
        xs (c/solve-linear n b phi)]
    (->> xs (map (partial b/m** m g)) (apply sorted-set))))

(defn power-residues
  [m n])

