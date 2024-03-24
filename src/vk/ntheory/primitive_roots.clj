(ns vk.ntheory.primitive-roots
  (:require [vk.ntheory.basic :as b]
            )
  )

(defn order
  "Find multiplicative order of given integer `a` in Z/Zn - {0}"
  [a n]
  (b/check-int-non-neg a)
  (b/check-int-pos n)
  (loop [k 1
         an (mod a n)]
    (condp = an
      0 (throw (Exception. "Expected a and n relatively prime."))
      1 k
      (recur (inc k) (mod (* an a) n)))
    )
  )


