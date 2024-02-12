(ns vk.ntheory-alpha
  (:require [clojure.math :as math]))

(defn gcd
  "Createst common divisor."
  [a b]
  (cond
    (neg? b) (gcd a (- b))
    (zero? b) (abs a)
    :else (recur b (mod a b))))

(defn gcd-extra
  "Extended Euclid algorithm.
  Returns greatest common divisor `d` for two given
  numbers `a` and `b`. And also return a pair [s t] such that
  a * s + b * t = d.
  "
  ([a b]
   (if (neg? b)
     (let [[d s t] (gcd-extra [a (- b)] [1 0] [0 1])] [d s (- t)])
     (gcd-extra [a b] [1 0] [0 1])))
  ([[a b] [s'' t''] [s' t']]
   (if
    (zero? b)
     (if (neg? a)
       [(- a) (- s'') (- t'')]
       [a s'' t''])
     (let [q (quot a b)
           s (- s'' (* s' q))
           t (- t'' (* t' q))]
       (recur [b (mod a b)] [s' t'] [s t])))))


