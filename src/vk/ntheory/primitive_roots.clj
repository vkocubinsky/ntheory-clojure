(ns vk.ntheory.primitive-roots
  "Primitive roots."
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.arithmetic-functions :as af]))

(defn order
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

(defn order'
  [a m]
  (first (filter #(= 1 (b/m** m a %)) (sort (af/divisors (- m 1))))))

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


