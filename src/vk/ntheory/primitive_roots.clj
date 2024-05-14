(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.arithmetic-functions :as af]))

(defn order
  "Find multiplicative order of given integer `a`."
  [a m]
  (b/check-int-non-neg a)
  (b/check-int-pos m)
  (b/check-relatively-prime a m)
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
  [a m]
  (b/check-relatively-prime a m)
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
       (filter #(primitive-root? % m))
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
       (filter #(primitive-root? % m))))

;; 997, 9973

(defn power-residue?
  [m n a]
  (b/check-relatively-prime a m)
  (if-let [g (find-primitive-root m)]
    (let [phi (af/totient m)
          d (b/gcd n phi)
          t (mod (b/pow a (/ phi d)) n)]
      ;;(prn "phi=" phi " d=" d " test=" t) 
      (= 1 t))))




