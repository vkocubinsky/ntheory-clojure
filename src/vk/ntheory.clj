(ns vk.ntheory
  (:require [clojure.math :as math]))

(def max-integer 10000000)

(defn pow
  "Power function."
  [a n]
  (apply * (repeat n a)))

(defn check-integer-range
  "Throw an execption if given `n` is not positive or more than `max-integer`."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (when-not (<= n max-integer) (throw (Exception. "Expected number <= ")))
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
        (reset! ldt {:least-divisor-table table :primes primes :upper n})))))

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

(defn integer->factors
  ([^Integer n]
   (check-integer-range n)
   (integer->factors (least-divisor-table n) n))
  ([^ints xs ^Integer n]
   (lazy-seq
    (when (> n 1)
      (let [d (aget xs n)]
        (cons d (integer->factors xs (quot n d))))))))

(defn integer->factors-distinct
  [n]
  (->> n integer->factors dedupe))

(defn integer->factors-partitions
  [n]
  (->> n integer->factors (partition-by identity)))

(defn integer->factors-count
  [n]
  (->> n
       integer->factors-partitions
       (map (fn [xs] [(first xs) (count xs)]))))

(defn integer->factors-map
  [n]
  (into {} (integer->factors-count n)))


(defn factors-count->integer
  "Convert factorization map back to integer."
  [cn]
  (apply * (for [[x y] cn] (pow x y))))

(defn- divisors'
  "Divisors of factorized numbers.
  Parameters:
  cn  - factorized number in format ([k1 v1] [k2 v2])
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
                         (conj d [p i]))))
    acc))

(defn divisors
  "Divisors of whole integer."
  [n]
  (check-integer-range n)
  (map factors-count->integer (-> n integer->factors-count (divisors' ,,, [[]]))))

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
    (check-integer-range n)
    (->> n
         integer->factors-count
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

(defn primes-count-distinct
  "Number of primes divides given `n` - ω."
  [n]
  (check-integer-range n)
  (-> n integer->factors-distinct count))

(defn primes-count-total
  "Number of primes and their powers divides given `n` - Ω."
  [n]
  (check-integer-range n)
  (-> n integer->factors count))


(defn liouville
  "Liouville function - λ"
  [n]
  (check-integer-range n)
  (pow (- 1) (primes-count-total n)))

(defn mangoldt
  "Mangoldt function - Λ"
  [n]
  (check-integer-range n)
  (let [[[p & _] & r] (integer->factors-partitions n)]
    (if (and p (nil? r))
      (math/log p)
      0
      )))

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
  (check-integer-range n)
  (if (= n 1)
    1
    0))

(defn one
  "Constant function returns 1."
  [n]
  (check-integer-range n)
  1)

(defn id
  "Identity function."
  [n]
  (check-integer-range n)
  n)

(defn chebyshev-first
  "The first Chebyshev function - θ."
  [n]
  (check-integer-range n)
  (->> n
       primes
       (map math/log)
       (apply +)))

(defn chebyshev-second
  "The second Chebyshev function - ψ."
  [n]
  (check-integer-range n)
  (->> n
       primes
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
  ([f g] (f-equals f g (range 1 100)))
  ([f g xs]
   (every? (fn [[a b]] (= a b))  (map (fn [n] [(f n) (g n)]) xs))))

;; todo: think about do I need dirichlet-inverse of fully multiplicative function - f^-1 = mu * f or delegate it to customer?
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
  (time (doseq [x (range 1 100000)] (divisors x)));;219
  (time (apply + (map primes-count-total (range 1 100000))));;66
  (time (apply + (map totient (range 1 100000))));;268ms
  (time (apply + (map mobius (range 1 100000))));;231ms
  (time (apply + (map mangoldt (range 1 100000))));;106ms
  (time (apply + (map chebyshev-first (range 1 5000))));;419ms
  (time (apply + (map chebyshev-second (range 1 5000))));;877ms
  (+ 1 2))


