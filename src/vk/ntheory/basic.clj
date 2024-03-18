(ns vk.ntheory.basic
  (:require [vk.ntheory.validation :as v])
  )

(defn divides?
  "Return true if `a` divides `b` otherwise false"
 [a b]
 (zero? (mod b a)))


(defn pow
  "Power function."
  [a n]
  (v/check-integer-non-negative n)
  (apply * (repeat n a)))

(defn order
  "Greatest power of p divides n."
  [p n]
  (loop [n  n
         k  0]
    (let [q (quot n p)
          r (mod n p)]
      (if (= r 0)
        (recur q (inc k))
        k))))

(defn sign
  [n]
  (cond
    (pos? n) 1
    (neg? n) -1
    :else 0))

(defn gcd
  "Createst common divisor."
  [a b]
  (loop [a (abs a) b (abs b)]
    (if (zero? b) a
        (recur b (mod a b)))))

(defn lcm
  "Least common multiple"
  [a b]
  (abs (/ (* a b) (gcd a b))))


(defn gcd-extended
  "Extended Euclid algorithm.
  For two given number `a` and `b` returns vector `[d s t]`,
  where d is the greatest common divisor `a` and `b` and
  values `s` and `d` satisfied condition
  `a * s + b * t = d`.
  "
  ([a b] (let [[d s t] (gcd-extended [(abs a) (abs b)] [1 0] [0 1])
               s' (* (sign a) s)
               t' (* (sign b) t)]
           (assert (= d (+ (* a s') (* b t'))))
           [d s' t']))
  ([[a b] [s'' t''] [s' t']]
   (if (zero? b)
     [a s'' t'']
     (let [q (quot a b)
           s (- s'' (* s' q))
           t (- t'' (* t' q))]
       (recur [b (mod a b)] [s' t'] [s t])))))





