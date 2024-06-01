(ns vk.ntheory.basic
  "Some basic functions of number theory.")

(defn check-true
  "Throws an exception if x is not true."
  [x ^String msg]
  (when-not x (throw (IllegalArgumentException. msg))))

(defn check-predicate
  "Returns `x` if value of predicat `(pred x)` is true,
  otherwise throws an exception."
  [pred x msg]
  (check-true (pred x) msg)
  x)

(defn check-int
  "Returns `n` if `n` is an integer, otherwise throws an exception."
  [n]
  (check-predicate int? n (format "%s is not an integer." n)))

(defn check-int-pos
  "Returns `n` if `n` is a positive integer,
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check-predicate pos? n (format "%s is not positive integer." n))))

(defn check-int-non-neg
  "Returns `n` if `n` is non negative integer(positive or zero),
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check-predicate (complement neg?) n (format "%s is not positive integer or is not zero." n))))

(defn check-int-non-zero
  "Return `n` if `n` is non zero integer,
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check-predicate (complement zero?) n (format "%s is zero" n))))

(defn check-at-least-one-non-zero
  "Throw exception if all arguments are zero, otherwise returns nil."
  [a b]
  (check-true (not-every? zero? [a b])
              (format "Expected at least one non zero integer"))
  [a b])

(defn divides?
  "Return true if `a != 0` divides `b`, otherwise false."
  [a b]
  (check-int-non-zero a)
  (check-int b)
  (zero? (mod b a)))

(defn check-not-divides
  "Throw an exception if `a` not divies `b`."
  [a b]
  (let [a (check-int-non-zero a)
        b (check-int b)]
    (check-true (not (divides? a b)) (format "Expected %s does not divides %s" a b))
    [a b]))

(defn congruent?
  "Check does `a` is congruent to `b` modulo m"
  [m a b]
  (check-int-pos m)
  (check-int a)
  (check-int b)
  (= (mod a m) (mod b m)))

(defn mod-mul
  "Multiplication modulo `m`, `(mod-mul)` returns 1, `(mod-mul a)` returns `a`."
  ([m]
   (check-int-pos m)
   1)
  ([m a]
   (check-int-pos m)
   (check-int a)
   (mod a m))
  ([m a b]
   (check-int-pos m)
   (check-int a)
   (check-int b)
   (mod (* a b) m))
  ([m a b & more] (reduce (partial mod-mul m) (mod-mul m a b) more)))

(defn mod-add
  "Addition modulo `m`. `(mod-add) returns 0, `(mod-add a)` returns `a`."
  ([m]
   (check-int-pos m)
   0)
  ([m a]
   (check-int-pos m)
   (check-int a)
   (mod a m))
  ([m a b]
   (check-int-pos m)
   (check-int a)
   (check-int b)
   (mod (+ a b) m))
  ([m a b & more] (reduce (partial mod-add m) (mod-add m a b) more)))

(defn- fast-power-iter
  ([fmult a n]
   (if
    (= n 0) (fmult 1) ;; special handler for modulo 1, return 0 instead of 1
    (fast-power-iter fmult 1 a n)))
  ([fmult odd even n]
   (if (= n 1)
     (fmult odd even)
     (if (odd? n)
       (recur fmult (fmult odd even) even (dec n))
       (recur fmult odd (fmult even even) (/ n 2))))))

(defn mod-pow
  "Raise integer `a` to the power of `n >= 0` modulo `m`."
  [m a n]
  (check-int-pos m)
  (check-int-non-neg n)
  (check-int a)
  (fast-power-iter (partial mod-mul m) a n))

(defn pow
  "Raise `a` to the power of `n >= 0`."
  [a n]
  (check-int a)
  (check-int-non-neg n)
  (fast-power-iter * a n))

(defn order
  "Greatest power of `p > 0` divides `n > 0`."
  [p n]
  (check-int-pos p)
  (check-int-pos n)
  (loop [n  n
         k  0]
    (let [q (quot n p)
          r (mod n p)]
      (if (= r 0)
        (recur q (inc k))
        k))))

(defn sign
  "Sign for given `n`."
  [n]
  (check-int n)
  (cond
    (pos? n) 1
    (neg? n) -1
    :else 0))

(defn gcd
  "The greatest common divisor of integers `a` and `b`, not both
  zero."
  [a b]
  (check-int a)
  (check-int b)
  (check-at-least-one-non-zero a b)
  (loop [a (abs a) b (abs b)]
    (if (zero? b) a
        (recur b (mod a b)))))

(defn- gcd-extended'
  "Helper function for `gcd-extended`."
  [[a b] [s'' t''] [s' t']]
  (if (zero? b)
    [a s'' t'']
    (let [q (quot a b)
          s (- s'' (* s' q))
          t (- t'' (* t' q))]
      (recur [b (mod a b)] [s' t'] [s t]))))

(defn gcd-extended
  "Extended Euclid algorithm.
  For two integers `a` and `b`,not both zero, returns vector
  `[d [s t]]`, where `d` is the greatest common divisor of integers `a` and
  `b` and values `s`,`t` and `d` satisfied condition:
  `a * s + b * t = d`.
  "
  [a b]
  (check-int a)
  (check-int b)
  (check-at-least-one-non-zero a b)
  (let [[d s t] (gcd-extended' [(abs a) (abs b)] [1 0] [0 1])
        s' (* (sign a) s)
        t' (* (sign b) t)]
    (assert (= d (+ (* a s') (* b t'))))
    [d [s' t']]))

(defn gcd-inverse
  "Experimental."
  [m a]
  (let [[d [s t]] (gcd-extended a m)]
    (if (= 1 d)
      s
      (throw (IllegalArgumentException. (format "Integers %s and %s are not relatively prime." m a))))))

(defn lcm
  "The least common multiple of two non zero integers `a` and `b`."
  [a b]
  (check-int-non-zero a)
  (check-int-non-zero b)
  (let [d (gcd a b)]
    (abs (/ (* a b) d))))

(defn relatively-prime?
  "Is given `a` and `b` are relatively prime"
  [a b]
  (= (gcd a b) 1))

(defn check-relatively-prime
  "Throw an exception if integers `a` and `b` are not relatively prime."
  [a b]
  (check-true (relatively-prime? a b) (format "Integers %s and %s are not relatively prime." a b))
  [a b])

(defn- product'
  "Helper function for function `product`.
   Parameters:
  `starts` - vector of first values of original input sequences
  `css` - vector of cycled sequences."
  [starts css]
  (lazy-seq
   (cons (map first css)
         (loop [css css
                k (dec (count css))]
           (when-not (neg? k)
             (let [start (get starts k)
                   cs (rest (get css k))
                   e (first cs)
                   css (assoc css k cs)]
               (if (= e start)
                 (recur css (dec k))
                 (product' starts css))))))))

(defn product
  "Return all n-sequences combined from given n sequences."
  [xss] (if (not-any? empty? xss)
          (product' (mapv first xss) (mapv cycle xss))
          []))

