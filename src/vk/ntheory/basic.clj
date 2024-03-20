(ns vk.ntheory.basic
  (:require [vk.ntheory.validation :as v]))

(defn divides?
  "Return true if `a != 0` divides `b`, otherwise false."
  [a b]
  (v/check-int-non-zero a)
  (v/check-int b)
  (zero? (mod b a)))

(defn pow
  "Return `a` raised to the power of `n >= 0`."
  [a n]
  (v/check-int a)
  (v/check-int-non-neg n)
  (apply * (repeat n a)))

(defn order
  "Greatest power of `p > 0` divides `n > 0`."
  [p n]
  (v/check-int-pos p)
  (v/check-int-pos n)
  (loop [n  n
         k  0]
    (let [q (quot n p)
          r (mod n p)]
      (if (= r 0)
        (recur q (inc k))
        k))))

(defn sign
  "Sign for given `n`"
  [n]
  (v/check-int n)
  (cond
    (pos? n) 1
    (neg? n) -1
    :else 0))

(defn gcd
  "Createst common divisor of `a` and `b`.
  If both `a` and `b` are equlas to zero returns zero."
  [a b]
  (v/check-int a)
  (v/check-int b)
  (loop [a (abs a) b (abs b)]
    (if (zero? b) a
        (recur b (mod a b)))))

(defn lcm
  "Least common multiple of `a` and `b`.
  If both `a` and `b` equals to zero returns zero."
  [a b]
  (v/check-int a)
  (v/check-int b)
  (let [d (gcd a b)]
    (if (= d 0)
      0
      (abs (/ (* a b) d)))))

(defn gcd-extended
  "Extended Euclid algorithm.
  For two given number `a` and `b` returns vector `[d s t]`,
  where d is the greatest common divisor `a` and `b` and
  values `s` and `d` satisfied condition
  `a * s + b * t = d`.
  "
  ([a b]
   (v/check-int a)
   (v/check-int b)
   (let [[d s t] (gcd-extended [(abs a) (abs b)] [1 0] [0 1])
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





