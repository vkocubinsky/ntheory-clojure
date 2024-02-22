(ns vk.ntheory
  (:require [clojure.math :as math]))

(defn pow
  "Power function."
  [a n]
  (reduce * (repeat n a)))

(defn check-positive
  "Throw an execption if given `n` is not positive."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  n)

(defn- ldt-find-prime
  "Find next prime in least divisor table.
  Parameters: 
    xs     - table
    start - start index
    end   - end index
  "
  ([xs start] (ldt-find-prime xs start (count xs)))
  ([^ints xs start end]
   (when (< start end)
     (let [e (aget xs start)]
       (if (and (> start 1) (= e start)) 
         start
         (recur xs (inc start) end))))))

(defn- ldt-update!
  "Update least divisor table to new value if it was not set."
  [^ints xs ^Integer k ^Integer v]
  (let [e (aget xs k)]
    (when-not (< e k)
      (aset xs k v))))

(defn- ldt-build
  "Build least divisors table."
  [n]
  (loop [xs (int-array (range (inc n)))
         p (ldt-find-prime xs 2)]
    (if (or (nil? p) (> (* p p) n))
      xs
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (ldt-update! xs k p))
        (recur xs (ldt-find-prime xs (inc p)))))))

(defn- ldt-primes
  [xs]
  (->> (map vector xs (range))
         (drop-while #(< (second %) 2))
         (filter #(= (first %) (second %)))
         (map first)))


(defn- ldt-auto-upper
  "Return auto extend upper number for given `n`."
  [n]
  (if (< n 10) 10
  (->> n
       math/log10
       math/ceil
       (math/pow 10)
       int)))


(def ldt (atom {:least-divisor-table nil :primes nil :upper 0}))


(defn- ldt-auto-extend!
  [n]
  (let [{:keys [_ _ upper] :as all} @ldt]
    (if (<= n upper)
      all
      (let [n (ldt-auto-upper n)
            table (ldt-build n)
            primes (ldt-primes table)]
        (reset! ldt {:least-divisor-table table :primes primes :upper n})
        ))))

(defn ldt-reset!
  []
  (reset! ldt {:least-divisor-table nil :primes nil :upper 0}))

(defn primes
  [n]
  (take-while #(<= % n) (:primes (ldt-auto-extend! n))))

(defn- least-divisor-table
  "Convenience function for return least divisor table not less than given `n`."
  [n]
  (:least-divisor-table (ldt-auto-extend! n)))

(defn ldt-factorize
  ([^Integer n]
   (loop [^ints T (least-divisor-table n)
          n  n
          ds []]
     (if (= n 1)
       ds
       (let [d (aget T n)]
         (recur T (quot n d) (conj ds d)))))))


(defn factorize
  "Factorize given `n`."
  [n]
  (check-positive n)
  (-> n ldt-factorize frequencies))

(defn de-factorize
  "Convert factorization map back to integer."
  [cn]
  (reduce * (for [[x y] cn] (pow x y))))

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
  (check-positive n)
  (map de-factorize (-> n factorize (divisors' ,,, [{}]))))


(defn reduce-on-prime
  "Higher order which return arithmetical function based on
  function defined on power of a prime.
  Parameters:
  rf - reduce functions such as `*` or `+`
  f - function defined on power of a prime, which accept [p k],
      where p is a prime and k is an order of prime,
      it returns value on order of prime."
  [rf f]
  (fn [n]
    (check-positive n)
    (->> n
         factorize
         (map (fn [[p k]] (f p k)))
         (reduce rf))))

(defn multiplicative-function
  "Higher order which return multiplicative function based on
  function defined on power of a prime.
  Parameters:
  f - function defined on power of a prime, which accept [p k],
      where p is a prime and k is an order of prime,
      it returns value on order of prime."
  [f]
  (reduce-on-prime * f))

(defn additive-function
  "Higher order which return additive function based on
  function defined on power of a prime.
  Parameters:
  f - function defined on power of a prime, which accept [p k],
      where p is a prime and k is an order of prime,
      it returns value on order of prime."
  [f]
  (reduce-on-prime + f))


(def primes-count-distinct
  "Number of primes divides given `n` - ω."
  (additive-function (fn [_ _] 1)))

(def primes-count-total
  "Number of primes and their powers divides given `n` - Ω."
  (additive-function (fn [_ k] k))
  )

(defn liouville
  "Liouville function - λ"
  [n]
  (check-positive n)
  (pow (- 1) (primes-count-total n))
  )

(defn mangoldt
  "Mangoldt function - Λ"
  [n]
  (let [cn (factorize n)]
    (if (= 1 (count cn) )
    (math/log (-> cn keys first))
    0))
  )

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
  (check-positive n)
  (if (= n 1)
    1
    0))

(defn one
  "Constant function returns 1."
  [n]
  (check-positive n)
  1)

(defn id
  "Identity function."
  [n]
  (check-positive n)
  n)

(defn chebyshev-first
  "The first Chebyshev function - θ."
  [n]
  (check-positive n)
  (->> n
      primes
      (map math/log)
      (reduce +)))

(defn chebyshev-second
  "The second Chebyshev function - ψ."
  [n]
  (check-positive n)
  (->> (range 1 (inc n))
      (map mangoldt)
      (reduce +)
      )
  )


(defn dirichlet-convolution
  "Dirichlet convolution."
  ([f g]
   (fn [n] (reduce + (for [d (divisors n)] (* (f d) (g (/ n d)))))))
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
               (reduce + (for [d (divisors n) :when (< d n)] (* (f (/ n d)) (f-inverse f d)))))))]
    (partial f-inverse f)))



(comment
  (time (/ (reduce + (map divisors-count (range 1 100000))) 100000.0))
  )


