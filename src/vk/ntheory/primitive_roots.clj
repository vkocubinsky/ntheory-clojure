(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.arithmetic-functions :as af]))

(defn order'
  "Brute force version of find multiplicative order."
  [a m]
  (b/check-int-non-neg a)
  (b/check-int-pos m)
  (b/check-relatively-prime a m)
  (loop [k 1
         an (mod a m)]
    (if (= 1 an)
      k
      (recur (inc k) (b/m* m an a)))))

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

(defmulti reduced-residues classify-residues :default :composite)

(defmethod reduced-residues :1
  [m]
  '(1))

(defmethod reduced-residues :prime
  [m]
  (range 1 m))

;; For now brute force implementation
(defmethod reduced-residues :prime-power
  [m]
  (let [[[p a]] (p/int->factors-count m)]
    (->> (range 1 m)
         (remove #(b/divides? p %)))))

;; For now brute force implementation
(defmethod reduced-residues :composite
  [m]
  (->> (range 1 m)
       (filter #(= 1 (b/gcd m %)))))

(defmulti primitive-root? (fn [a m] (p/prime? m)))

(defmethod primitive-root? false
  [a m]
  (= (order a m) (af/totient m)))

;; Does it works for non prime p if we replace (p-1) to phi(p)? 
(defmethod primitive-root? true
  [a p]
  (p/check-odd-prime p)
  (->> (dec p)
       (p/int->factors-distinct)
       (map #(/ (dec p) %))
       (map #(b/m** p a %))
       (every? #(not (= 1 %)))))

(defn classify-modulo
  [n]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count n)]
    (cond
      (= n 1) :1
      (= n 2) :2
      (= n 4) :4
      (and (nil? p2) (= a1 1)) :odd-prime ;; p
      (and (nil? p2) (> a1 1)) :odd-prime-power ;; p^a
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3)) :2-odd-prime-power)))

(defn primitive-roots'
  "Brute force version of search primitive roots."
  [m]
  (b/check-int-pos m)
  (->> (reduced-residues m)
       (filter #(primitive-root? % m))))

(defmulti find-primitive-root classify-modulo)

(defmethod find-primitive-root :1
  [m]
  1)

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

;; 997, 9973

(defn combinations
  [xs]
  (let [n (count xs)
        yss (mapv #(cycle (range %)) xs)]
    (println "out: " (map first yss))
    (loop [k (dec n)
           yss yss]
      (when-not (neg? k)
        (let [ys (get yss k) ;; get sequence
              ys (rest ys)   ;; shift sequence
              e (first ys)   ;; to detect overflow
              yss (assoc yss k ys) ;; assoc shifted sequence 
              ]
          (if (= e 0)
            (recur (dec k) yss) ;; overflow
            (do
              (println "out: " (map first yss))
              (recur (dec n) yss))))))))

(combinations [5 3 7])
