(ns vk.ntheory-alpha
  (:require [clojure.math :as math]))

(defn gcd
  "Createst common divisor."
  [a b]
  (cond
    (zero? a) b
    (zero? b) a
    :else (recur b (mod a b))
    )
  )

;; a = bq + r 



