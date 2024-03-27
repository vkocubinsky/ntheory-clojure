(ns vk.ntheory.primitive-roots
  (:require [vk.ntheory.basic :as b]
            [vk.ntheory.ar-func :as af]
            ))

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
  (first (filter #(= 1 (b/m** m a %))(sort (af/divisors (- m 1)))))
  )


;; [p1; p2; ...]
(defn generate
  [x]
  (let [y (map cycle x)
        n (count x)]
    (loop [k (dec n)]
      )
    )
  )

(nth [1 2 3] 2)
(generate [(range 1 5) (range 1 7)])


