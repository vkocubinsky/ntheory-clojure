(ns vk.ntheory
  (:require [clojure.set :as set]
            [clojure.math :as math])
  (:import [java.util Arrays])
  (:gen-class))

(defn pow
  "Power function."
  [a n]
  (apply * (repeat n a)))


(defn- ldt-find-prime
  "Find next prime in least divisor table.
  Parameters: 
    T     - table
    start - start index
    end   - end index
  "
  ([T start] (ldt-find-prime T start (count T)))
  ([^ints T start end]
   (when (< start end)
     (let [e (aget T start)]
       (if (and (> start 1) (= e start)) ;; todo: >=
         start
         (recur T (inc start) end))))))

(defn- ldt-update
  [^ints T ^Integer k ^Integer v]
  (let [e (aget T k)]
    (when-not (< e k)
      (aset T k v))))

(defn- ldt-build
  "Build least divisors table"
  [n]
  (loop [T (int-array (range (inc n)))
         p (ldt-find-prime T 2)]
    (if (or (nil? p) (> (* p p) n))
      T
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (ldt-update T k p))
        (recur T (ldt-find-prime T (inc p)))))))


(defn- ldt-range-array
  "Create or extend an array as range from zero to `n`"
  ([n] (ldt-range-array (int-array 0) n))
  ([^ints a ^Integer n]
   (let [l (alength a)]
     (if (< l n)
       (let [b (int-array (range l n))
             c (Arrays/copyOf a n)]
         (System/arraycopy b 0 c l (- n l))
         c)
       a))))

;; todo: calculation of (n'/p + 1) * p is not nesesarry if
;; we double size, only if n > n'*n' we have some optimization.
;; but it is minimal optimization. 
(defn ldt-extend
  [^ints a n]
  (let [n' (dec (alength a))]
    (if (< n' n)
      (loop [b (ldt-range-array a (inc n))
             p (ldt-find-prime b 2)]
        (if (or (nil? p) (> (* p p) n))
          b
          (let [s (max (* (inc (quot n' p)) p)
                       (* p p))]
            (doseq [k (range (* p p) (inc n) p)]
              (ldt-update b k p))
            (recur b (ldt-find-prime b (inc p))))))
      a)))




(def ldt (atom {:table nil :upper 0}))

(defn ldt-get
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (let [{:keys [table upper]} @ldt]
    (if (<= n upper)
      table
      (let [table (ldt-build n)]
        (reset! ldt {:table table :upper n})
        table
      )
    )
  ))

(defn ldt-reset!
  []
  (reset! ldt {:table nil :upper 0}))


(defn ldt-factorize
  ([^Integer n]
   (loop [^ints T (ldt-get n)
          n  n
          ds []]
     (if (= n 1)
       ds
       (let [d (aget T n)]
         (recur T (quot n d) (conj ds d)))))))


(defn factorize
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (-> n ldt-factorize frequencies))

  
(defn de-factorize
  "Convert factorization map back to integer."
  [cn]
  (apply * (for [[x y] cn] (pow x y))))

(defn- divisors'
  "Divisors of factorized numbers.
  Parameters:
  cn  - factorized number
  acc - sequence of factorized numbers which
        divides original value

  The implementation based on fact that if we have number `a = m * n`
  and `m` and `n` are relative prime then any divisor of `a` can be
  written in form `d = dm * dn` where `dm`,`dn` are divisors of `m`
  and `n` respectively. And this exactly one such representation."
  [cn acc]
  (if-let [[p k] (first cn)]
    (recur (rest cn) (for [i (range 0 (inc k))
                           d acc]
                       (if (zero? i)
                         d
                         (assoc d p i))))
    acc))

(defn divisors
  "Divisors of whole integer."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (map de-factorize (-> n factorize (divisors' ,,, [{}]))))

(defn multiplicative-function
  "Higher order which return multiplicative function based on values on primes.
  Parameters:
  f - function which accept [p k], where p is a prime and k is an order of prime, it returns value on order of prime.
  "
  [f]
  (fn [n]
    (when-not (pos? n) (throw (Exception. "Expected positive number")))
    (if (= n 1)
      1
      (->> n
           factorize
           (map (fn [[p k]] (f p k)))
           (apply *)))))

(def divisors-count
  "Divisors count - σ₀"
  (multiplicative-function (fn [_ k] (inc k))))

(defn  divisors-sum-x
  "Higher order function which return divisors sum of `a` powers function - σₐ."
  [a]
  (if (= a 0)
    divisors-count
    (multiplicative-function (fn [p k] (/
                                        (dec (pow p
                                                  (* (inc k)
                                                     a)))
                                        (dec (pow p a)))))))

(def divisors-sum
  "Divisors sum - σ₁."
  (divisors-sum-x 1))

(def divisors-square-sum
  "Divisors square sum - σ₂."
  (divisors-sum-x 2))

(def mobius
  "Mobius function - μ."
  (multiplicative-function (fn [_ k] (if (> k 1) 0 -1))))

(def totient
  "Euler's totient function - ϕ."
  (multiplicative-function (fn [p k] (- (pow p k) (pow p (dec k))))))

(defn unit
  "Unit function - ϵ."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (if (= n 1)
    1
    0))

(defn one
  "Constant function returns 1."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  1)

(defn id
  "Identity function."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  n)

(defn id-x
  "Higher order function which return power function for given `a`."
  [a]
  (fn [n]
    (when-not (pos? n) (throw (Exception. "Expected positive number")))
    (pow n a)))

(defn dirichlet-convolution
  "Dirichlet convolution."
  ([f g]
   (fn [n] (apply + (for [d (divisors n)] (* (f d) (g (/ n d)))))))
  ([f g & more] (reduce dirichlet-convolution f (cons g more))))

(defn f-equals
  ([f g] (f-equals f g (range 1 100)))
  ([f g xs]
   (every? (fn [[a b]] (= a b))  (map (fn [n] [(f n) (g n)]) xs))))


(defn dirichlet-inverse
  "Dirichlet inverse."
  [f]
  (letfn [(f-inverse [f n]
            (if (= n 1)
              (/ 1 (f 1))
              (*
               (/ (- 1) (one 1))
               (apply + (for [d (divisors n) :when (< d n)] (* (f (/ n d)) (f-inverse f d)))))))]
    (partial f-inverse f)))


(comment
  (reset-ldt!)
  (ldt-get 100000)
  (time (/ (reduce + (map divisors-count (range 1 100000))) 100000.0))
    (f-equals
   (dirichlet-convolution nt/mobius nt/one)
   nt/unit))


