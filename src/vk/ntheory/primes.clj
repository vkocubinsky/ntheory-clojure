(ns vk.ntheory.primes
  "Primes and Integer Factorization."
  (:require [vk.ntheory.basic :as b]
            [clojure.math :as math]))

(def max-int
  "Max integer which can be factorized."
  10000000)

(defn- less-equal-max-int?
  "Less or equal to `max-int`"
  [n]
  (<= n max-int))

(defn check-int-pos-max
  "Check `0 < n <= max-int`."
  [n]
  (let [n (b/check-int-pos n)]
    (b/check-predicate less-equal-max-int? n (format "%s is more than %s" n max-int))))

(defn check-int-non-neg-max
  "Check `0 <= n <= max-int`"
  [n]
  (let [n (b/check-int-non-neg n)]
    (b/check-predicate less-equal-max-int? n (format "%s is more than %s" n max-int))))

(defn check-int-non-zero-max
  "Check is given integer `n` is not zero and less or equal to max-int "
  [n]
  (let [n (b/check-int-non-zero n)]
    (b/check-predicate less-equal-max-int? n (format "%s is more than %s" n max-int))))

(defn- table-find-prime
  "Find next prime in least divisor table.
  Parameters: 
    xs    - least divisor table
    start - start index
    end   - end index
  "
  ([xs start] (table-find-prime xs start (count xs)))
  ([^ints xs start end]
   (when (< start end)
     (let [e (aget xs start)]
       (if (and (> start 1) (= e start))
         start
         (recur xs (inc start) end))))))

(defn- table-update
  "Update least divisor table to new value if it was not set."
  [^ints xs ^Integer k ^Integer v]
  (let [e (aget xs k)]
    (when-not (< e k)
      (aset xs k v))))

(defn- table-build
  "Build least divisor table.
  Use slightly modified Eratosthenes algorithm for build least divisor table."
  [n]
  (loop [xs (int-array (range (inc n)))
         p (table-find-prime xs 2)]
    (if (or (nil? p) (> (* p p) n))
      xs
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (table-update xs k p))
        (recur xs (table-find-prime xs (inc p)))))))

(defn- primes-build
  "Build prime table from least divisor table"
  [xs]
  (->> (map vector xs (range))
       (drop-while #(< (second %) 2))
       (filter #(= (first %) (second %)))
       (map first)))

(defn- table-extend-size
  "Return auto extend upper number for given `n`."
  [n]
  (if (< n 10) 10
      (->> n
           math/log10
           math/ceil
           (math/pow 10)
           int)))

(def cache (atom {:least-divisor-table nil :primes nil :upper 0}))

(defn- table-auto-extend
  [n]
  (check-int-pos-max n)
  (let [{:keys [_ _ upper] :as all} @cache]
    (if (<= n upper)
      all
      (let [n (table-extend-size n)
            table (table-build n)
            primes (primes-build table)]
        (reset! cache {:least-divisor-table table :primes primes :upper n})))))

(defn- least-divisor-table
  "Convenience function for return least divisor table not less than given `n`."
  [n]
  (:least-divisor-table (table-auto-extend n)))

(defn cache-reset!
  []
  (reset! cache {:least-divisor-table nil :primes nil :upper 0}))

(defn primes
  [n]
  (check-int-pos-max n)
  (take-while #(<= % n) (:primes (table-auto-extend n))))

(defn unit?
  "Is given `n` a unit?"
  [n]
  (check-int-pos-max n)
  (= n 1))

(defn prime?
  "Is given `0 < n <= max-int` a prime number?"
  [n]
  (check-int-pos-max n)
  (if (unit? n)
    false
    (let [^ints table (least-divisor-table n)
          e (aget table n)]
      (= e n))))

(defn composite?
  "Is given `0 < n <= max-int` a composite number?"
  [n]
  (check-int-pos-max n)
  (if (unit? n)
    false
    ((complement prime?) n)))


(defn- int->factors'
  [^ints xs ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (aget xs n)]
       (cons d (int->factors' xs (quot n d))))))
  )

(defn int->factors
  [^Integer n]
   (check-int-pos-max n)
   (int->factors' (least-divisor-table n) n)
  )

(defn int->factors-distinct
  [n]
  (check-int-pos-max n)
  (->> n int->factors dedupe))

(defn int->factors-partitions
  [n]
  (check-int-pos-max n)
  (->> n int->factors (partition-by identity)))

(defn int->factors-count
  [n]
  (check-int-pos-max n)
  (->> n
       int->factors-partitions
       (map (fn [xs] [(first xs) (count xs)]))))

(defn int->factors-map
  [n]
  (check-int-pos-max n)
  (into {} (int->factors-count n)))

(defn int->coprime-factors
  [n]
  (check-int-pos-max n)
  (for [[p e] (int->factors-count n)] (b/pow p e)))

(defn factors->int
  [xs]
  (apply * xs))

(defn factors-count->int
  "Convert factors map or factors counts back to integer."
  [cn]
  (apply * (for [[x y] cn] (b/pow x y))))

(defn factors-partitions->int
  [xss]
  (->> xss
       (map #(apply * %))
       (apply *)))

(defn check-prime
  [n]
  (let [n (check-int-pos-max n)]
    (b/check-predicate prime? n (format "%s is not a prime" n))))

(defn check-odd-prime
  [n]
  (let [n (check-prime n)]
    (b/check-predicate odd? n (format "%s is not odd" n))))

