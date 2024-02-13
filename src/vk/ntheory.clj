(ns vk.ntheory
  (:require [clojure.set :as set]
            [clojure.math :as math]))

(set! *warn-on-reflection* true)

(defn pow
  "Power function."
  [a n]
  (apply * (repeat n a)))

(defn sieve'
  "Sieve of Eratosthenes.
  Return primes less or equal to `n`."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (loop [v (apply sorted-set (range 2 (inc n)))
         k 2]
    (if (> (* k k) n)
      v
      (recur (set/difference v (set (range (* k k) (inc n) k))) (inc k)))))

(def sieve-table (atom {:table (sorted-set) :upper 1}))

(defn reset-sieve-table!
  []
  (reset! sieve-table {:table (sorted-set) :upper 1}))

(defn sieve
  "Cached version of Sieve of Eratosthenes."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (let [{:keys [table upper]} @sieve-table]
    (if (<= n upper)
      (apply sorted-set (subseq table <= n))
      (let [table (sieve' n)]
        (reset! sieve-table {:table (sieve' n) :upper n})
        table))))

(defn- div-p-adic-valuation
  "Divide given number `n` on highest order of prime number `p` divides `n`.
   Return pair [v n'] where `v` - highest order of p divides n and
   n' -  quotient of n and p^v i.e. n/p^v."
  [p n]
  (loop [v 0
         n' n]
    (if (= (mod n' p) 0)
      (recur (inc v) (quot n' p))
      [v n'])))

(defn-  factorize'
  [n cn [p & primes']]
  (cond
    (= n 1) cn
    (or (nil? p) (> (* p p) n)) (assoc cn n 1)
    :else (let [[v n'] (div-p-adic-valuation p n)]
            (if (zero? v)
              (recur n' cn primes')
              (recur n' (assoc cn p v) primes')))))

(defn sieve-factorize
  "Factorize given positive integer `n`."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive `n`")))
  (factorize' n (sorted-map) (sieve (int (math/sqrt n)))))

(def factorize sieve-factorize)


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
  (f-equals
   (dirichlet-convolution nt/mobius nt/one)
   nt/unit))


