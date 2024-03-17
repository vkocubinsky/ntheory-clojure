(ns vk.ntheory.ar-func
  (:require [clojure.math :as math]
            [vk.ntheory.primes :as p]
            [vk.ntheory.validation :as v]
            [vk.ntheory.basic :refer [pow]]))

(def default-natural-sample (range 1 100))

(defn- divisors'
  "Divisors of factorized numbers.
  Parameters:
  cn  - factorized number in format ([k1 v1] [k2 v2])
  acc - sequence of factorized numbers which
        divides original value."
  [cn acc]
  (if-let [[p k] (first cn)]
    (recur (rest cn) (for [i (range 0 (inc k))
                           d acc]
                       (if (zero? i)
                         d
                         (conj d [p i]))))
    acc))

(defn divisors
  "Divisors of whole integer."
  [n]
  (v/check-integer-range n)
  (as-> n v
    (p/integer->factors-count v)
    (divisors' v [[]])
    (map p/factors-count->integer v)))

(defn reduce-on-prime-count
  "Higher order which return arithmetical function based on
  function defined on power of a prime.
  Parameters:
  rf - reduce functions such as `*` or `+`
  f - function defined on power of a prime, which accept [p k],
      where p is a prime and k is an order of prime,
      it returns value on order of prime."
  [rf f]
  (fn [n]
    (v/check-integer-range n)
    (->> n
         p/integer->factors-count
         (map (fn [[p k]] (f p k)))
         (reduce rf))))

(defn primes-count-distinct
  "Number of primes divides given `n` - ω."
  [n]
  (v/check-integer-range n)
  (-> n p/integer->factors-distinct count))

(defn primes-count-total
  "Number of primes and their powers divides given `n` - Ω."
  [n]
  (v/check-integer-range n)
  (-> n p/integer->factors count))

(defn liouville
  "Liouville function - λ"
  [n]
  (v/check-integer-range n)
  (pow (- 1) (primes-count-total n)))

(defn mangoldt
  "Mangoldt function - Λ"
  [n]
  (v/check-integer-range n)
  (let [[[p & _] & r] (p/integer->factors-partitions n)]
    (if (and p (nil? r))
      (math/log p)
      0)))

(def divisors-count
  "Divisors count - σ₀"
  (reduce-on-prime-count * (fn [_ k] (inc k))))

(defn  divisors-sum-x
  "Higher order function which return divisors sum of `a` powers function - σₐ."
  [a]
  (if (= a 0)
    divisors-count
    (reduce-on-prime-count *' (fn [p k] (/
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

(defn mobius
  "Mobius function - μ."
  [n]
  (v/check-integer-range n)
  (->> n
       p/integer->factors-count
       (reduce (fn [a [_ k]] (if (= k 1) (* a -1) (reduced 0))) 1)))

(def totient
  "Euler's totient function - ϕ."
  (reduce-on-prime-count * (fn [p k] (- (pow p k) (pow p (dec k))))))

(defn unit
  "Unit function - ϵ."
  [n]
  (v/check-integer-range n)
  (if (= n 1)
    1
    0))

(defn one
  "Constant function returns 1."
  [n]
  (v/check-integer-range n)
  1)

(defn chebyshev-first
  "The first Chebyshev function - θ."
  [n]
  (v/check-integer-range n)
  (->> n
       p/primes
       (map math/log)
       (apply +)))

(defn chebyshev-second
  "The second Chebyshev function - ψ."
  [n]
  (v/check-integer-range n)
  (->> n
       p/primes
       (map #(* (math/log %)
                (math/floor (/ (math/log n)
                               (math/log %)))))
       (apply +)))

(defn dirichlet-convolution
  "Dirichlet convolution."
  ([f g]
   (fn [n] (apply + (for [d (divisors n)] (* (f d) (g (/ n d)))))))
  ([f g & more] (reduce dirichlet-convolution f (cons g more))))

(defn f-equals
  ([f g] (f-equals f g default-natural-sample))
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
  (time (doseq [x (range 1 100000)] (divisors x)));;400-500ms
  (time (apply + (map primes-count-distinct (range 1 100000))));;225
  (time (apply + (map primes-count-total (range 1 100000))));;124
  (time (apply + (map liouville (range 1 100000))));;172
  (time (apply + (map mangoldt (range 1 100000))));;190ms

  (time (apply + (map divisors-count (range 1 100000))));;500ms
  (time (apply + (map divisors-sum (range 1 100000))));;640ms
  (time (apply + (map divisors-square-sum (range 1 100000))));;500ms

  (time (apply + (map mobius (range 1 100000))));;327ms, 171ms
  (time (apply + (map totient (range 1 100000))));;585ms, 281

  (time (apply + (map chebyshev-first (range 1 5000))));;733ms
  (time (apply + (map chebyshev-second (range 1 5000))));;877ms
  )


