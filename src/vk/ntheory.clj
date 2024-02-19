(ns vk.ntheory
  (:require [clojure.math :as math]))

(defn pow
  "Power function."
  [a n]
  (apply * (repeat n a)))

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

(defn- ldt-update
  "Update least divisor table to new value if it was not set."
  [^ints xs ^Integer k ^Integer v]
  (let [e (aget xs k)]
    (when-not (< e k)
      (aset xs k v))))

(defn- ldt-build
  "Build least divisors table."
  [n]
  (println "Rebuild least divisor table for n = " n)
  (loop [xs (int-array (range (inc n)))
         p (ldt-find-prime xs 2)]
    (if (or (nil? p) (> (* p p) n))
      xs
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (ldt-update xs k p))
        (recur xs (ldt-find-prime xs (inc p)))))))





(def ldt (atom {:table nil :upper 0}))

(defn- ldt-auto-extend
  "Return auto extend number for given `n`."
  [n]
  (->> n
       math/log10
       math/ceil
       (math/pow 10)
       int))

(defn ldt-get
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (let [{:keys [table upper]} @ldt]
    (if (<= n upper)
      table
      (let [n (ldt-auto-extend n)
            table (ldt-build n)]
        (reset! ldt {:table table :upper n})
        table))))

(defn ldt-reset!
  []
  (reset! ldt {:table nil :upper 0}))

(defn- ldt-primes
  [xs]
  (->> (map vector xs (range))
         (drop-while #(< (second %) 2))
         (filter #(= (first %) (second %)))
         (map first)))


(defn primes
  "Get primes less or equal to given `n`."
  [n]
  (->> (ldt-get n)
      ldt-primes
      (take-while #(<= % n)))
  )


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
           (reduce *)))))

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
  (ldt-reset!)
  (ldt-get 300)
  (time (/ (reduce + (map divisors-count (range 1 100000))) 100000.0))
  (f-equals
   (dirichlet-convolution nt/mobius nt/one)
   nt/unit))


