(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.arithmetic-functions :as af]))


(defn classify-primitive-roots
  [n]
  (let [[[p1 a1] [p2 a2] [p3 a3]] (p/int->factors-count n)
       ]
    (cond
      (= n 1) :1
      (= n 2) :2
      (= n 4) :4
      (and (nil? p2) (= 2 p1)) :even-prime-power
      (and (nil? p2) (= a1 1)) :odd-prime
      (and (nil? p2) (= a1 2)) :odd-prime-square
      (and (nil? p2) (> a1 2)) :odd-prime-power
      (and (= p1 2) (= a1 1) (not (nil? p2)) (nil? p3) ) :2-prime-power)))

(defmulti primitive-roots classify-primitive-roots)

(defmethod primitive-roots :1
  [n]
  1)

(defmethod primitive-roots :2
  [n]
  1)

(defmethod primitive-roots :4
  [n]
  3)

(defmethod primitive-roots :odd-prime
  [n]
  (println "Prititve root :odd-prime mod " n))

(defmethod primitive-roots :odd-prime-square
  [n]
  (println "Prititve root :odd-prime-square mod " n))

(defmethod primitive-roots :odd-prime-power
  [n]
  (println "Prititve root :odd-prime-power mod " n)
  )

(defmethod primitive-roots :2-prime-power
  [n]
  (println "Prititve root :2-prime-power mod " n)
  )

(defmethod primitive-roots :default
  [n]
  (println "No primitive root"))

(defn order-brute-force
  "Find multiplicative order of given integer `a` in Z/Zn - {0}"
  [a m]
  (b/check-int-non-neg a)
  (b/check-int-pos m)
  (loop [k 1
         an (mod a m)]
    (condp = an
      0 (throw (Exception. "Expected a and m relatively prime."))
      1 k
      (recur (inc k) (b/m* m an a)))))

(defn order
  [a m]
  (first (filter #(= 1 (b/m** m a %)) (sort (af/divisors (af/totient m))))))

(defn primitive-root?
  [a m]
  (= (order a m) (af/totient m)))

(defn primitive-root-prime?
  [a p]
  (p/check-odd-prime p)
  (->> (dec p)
      (p/int->factors-distinct)
      (map #(/ (dec p) %))
      (map #(b/m** p a %))
      (every? #(not(= 1 %)))
      )
  )

(primitive-root-prime? 2 997)


;; [p1; p2; ...]
(defn generate
  [xss]
  (let [yss (map cycle xss)
        n (count xss)]
    (loop [k (dec n)
           yss yss
           overflow false]
      (let [ks (nth yss k)
            e (first ks)]))))

(nth [1 2 3] 2)
(generate [(range 1 5) (range 1 7)])

;; 997, 9973

(primitive-root? 2 11)
(time (count (map #(primitive-root? % 997) (range 1 997))))
(time (count (map #(primitive-root-prime? % 997) (range 1 997))))

