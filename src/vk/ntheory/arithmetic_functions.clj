 (ns vk.ntheory.arithmetic-functions
  (:require [clojure.math :as math]
            [vk.ntheory.primes :as p]
            [vk.ntheory.basic :as b]))

(def default-natural-sample (range 1 100))

(defn divisors
  "Divisors of a positive integer."
  [n]
  (p/check-int-pos-max n)
  (let [cn (p/int->factors-count n)
        bases (map first cn)
        powers (map second cn)
        ranges (map #(range 0 (inc %)) powers)
        ]
    (for [x (b/product ranges)]
      (p/factors-count->int (map #(vector %1 %2) bases x)))
    )
  )


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
    (p/check-int-pos-max n)
    (->> n
         p/int->factors-count
         (map (fn [[p k]] (f p k)))
         (reduce rf))))

(defn primes-count-distinct
  "Number of primes divides given `n` - ω."
  [n]
  (p/check-int-pos-max n)
  (-> n p/int->factors-distinct count))

(defn primes-count-total
  "Number of primes and their powers divides given `n` - Ω."
  [n]
  (p/check-int-pos-max n)
  (-> n p/int->factors count))

(defn liouville
  "Liouville function - λ"
  [n]
  (p/check-int-pos-max n)
  (b/pow (- 1) (primes-count-total n)))

(defn mangoldt
  "Mangoldt function - Λ"
  [n]
  (p/check-int-pos-max n)
  (let [[[p & _] & r] (p/int->factors-partitions n)]
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
                                         (dec (b/pow p
                                                     (* (inc k)
                                                        a)))
                                         (dec (b/pow p a)))))))

(def divisors-sum
  "Divisors sum - σ₁."
  (divisors-sum-x 1))

(def divisors-square-sum
  "Divisors square sum - σ₂."
  (divisors-sum-x 2))

(defn mobius
  "Mobius function - μ."
  [n]
  (p/check-int-pos-max n)
  (->> n
       p/int->factors-count
       (reduce (fn [a [_ k]] (if (= k 1) (* a -1) (reduced 0))) 1)))

(def totient
  "Euler's totient function - ϕ."
  (reduce-on-prime-count * (fn [p k] (- (b/pow p k) (b/pow p (dec k))))))

(defn unit
  "Unit function - ϵ."
  [n]
  (p/check-int-pos-max n)
  (if (= n 1)
    1
    0))

(defn one
  "Constant function returns 1."
  [n]
  (p/check-int-pos-max n)
  1)

(defn chebyshev-theta
  "Chebyshev θ function."
  [n]
  (p/check-int-pos-max n)
  (->> n
       p/primes
       (map math/log)
       (apply +)))

(defn chebyshev-psi
  "Chebyshev ψ function"
  [n]
  (p/check-int-pos-max n)
  (->> n
       p/primes
       (map #(* (math/log %)
                (math/floor (/ (math/log n)
                               (math/log %)))))
       (apply +)))

(defn dirichlet-mul
  "Dirichlet product(or convolution)."
  ([f g]
   (fn [n] (apply + (for [d (divisors n)] (* (f d) (g (/ n d)))))))
  ([f g & more] (reduce dirichlet-mul f (cons g more))))

(defn equal
  ([f g] (equal f g default-natural-sample))
  ([f g xs]
   (every? (fn [[a b]] (= a b))  (map (fn [n] [(f n) (g n)]) xs))))

(defn dirichlet-inverse
  "Dirichlet inverse."
  [f]
  (letfn [(inv [f n]
            (if (= n 1)
              (/ 1 (f 1))
              (*
               (/ (- 1) (one 1))
               (reduce + (for [d (divisors n) :when (< d n)] (* (f (/ n d)) (inv f d)))))))]
    (partial inv f)))

(defn pointwise-mul
  "Pointwise multiplication of two functions `f` and `g`."
  [f g]
  (fn [n]
    (* (f n) (g n))))

(defn pointwise-add
  "Pointwise addition of two functions `f` and `g`."
  [f g]
  (fn [n]
    (+ (f n) (g n))))


