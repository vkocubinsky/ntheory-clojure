(ns vk.ntheory.basic
  "Some basic function of number theory.")

(defn check
  "Returns `x` if value of predicat `(pred x)` is true,
  otherwise throws an exception."
  [pred x msg]
  (if (pred x)
    x
    (throw (IllegalArgumentException. msg))))

(defn check-not
  "Returns `x` if value of predicat `(pred x)` is false,
  otherwise throws an exception."
  [pred x msg]
  (check (complement pred) x msg))

(defn check-int
  "Returns `n` if `n` is an integer, otherwise throws an exception."
  [n]
  (check int? n (format "%s is not an integer." n)))

(defn check-int-pos
  "Returns `n` if `n` is a positive integer,
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check pos? n (format "%s is not positive integer." n))))

(defn check-int-non-neg
  "Returns `n` if `n` is non negative integer(positive or zero),
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check-not neg? n (format "%s is not positive integer or is not zero." n))))

(defn check-int-non-zero
  "Return `n` if `n` is non zero integer,
  otherwise throw an exception."
  [n]
  (let [n (check-int n)]
    (check-not zero? n (format "%s is zero" n))))

(defn divides?
  "Return true if `a != 0` divides `b`, otherwise false."
  [a b]
  (check-int-non-zero a)
  (check-int b)
  (zero? (mod b a)))

(defn m*
  "Multiplication modulo `m`, `(m*)` returns 1, `(m* a)` returns `a`."
  ([m] 1)
  ([m a] (mod a m))
  ([m a b] (mod (* a b) m))
  ([m a b & more] (reduce (partial m* m) (m* m a b) more)))

(defn m+
  "Addition modulo `m`. `(m+) returns 0, `(m+ a)` returns `a`."
  ([m] 0)
  ([m a] (mod a m))
  ([m a b] (mod (+ a b) m))
  ([m a b & more] (reduce (partial m+ m) (m+ m a b) more)))

(defn- bit-count
  [n]
  (count (Integer/toBinaryString n)))

(defn m**
  "Raise integer `a` to the power of `n >= 0` modulo `m`.
  See D.Knuth, The Art of Computer Programming, Volume II."
  [m a n]
  (check-int-non-neg m)
  (check-int a)
  (check-int-non-neg n)
  (let [c (bit-count n)
        m*' (partial m* m)]
    (reduce
     (fn [acc bit] (let [s (m*' acc acc)]
                     (if bit
                       (m*' s a)
                       s)))
     1
     (for [b1 (range c 0 -1)
           :let [b0 (dec b1)
                 bit (bit-test n b0)]]
       bit))))

(defn pow
  "Raise `a` to the power of `n >= 0`."
  [a n]
  (check-int a)
  (check-int-non-neg n)
  (apply * (repeat n a)))

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
  "Createst common divisor of integers `a` and `b`.
  (gcd 0 0) returns 0."
  [a b]
  (check-int a)
  (check-int b)
  (loop [a (abs a) b (abs b)]
    (if (zero? b) a
        (recur b (mod a b)))))

(defn lcm
  "Least common multiple of `a` and `b`.
  (lcm 0 0) returns 0."
  [a b]
  (check-int a)
  (check-int b)
  (let [d (gcd a b)]
    (if (= d 0)
      0
      (abs (/ (* a b) d)))))

(defn gcd-extended
  "Extended Euclid algorithm.
  For two integers `a` and `b` returns vector `[d s t]`, where `d` is
  the greatest common divisor of integers `a` and `b` and values
  `s`,`t` and `d` satisfied condition `a * s + b * t = d`.
  "
  ([a b]
   (check-int a)
   (check-int b)
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

(defn check-relatively-prime
  "Throw an exception if integers `a` and `b` are not relatively prime."
  [a b]
  (let [d (gcd a b)]
    (check #(= 1 %) d (format "Integers %s and %s are not relatively prime." a b))))

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
  [xss] (product' (mapv first xss) (mapv cycle xss)))

