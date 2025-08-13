(ns vk.ntheory.ldt.factorization-impl
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.util :as u]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.factorization :as f]))


(defn- sieve
  "Sieve of Erathosphene."
  [table start]
  (loop [p  start]
    (if (> (* p p) (t/table-upper-limit table))
      table
      (let [p' (t/table-get-number table p)
            mark-step (if (= p 2) p (* p 2))
            iter-step (if (= p 2) 1 2)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (t/table-upper-limit table)) mark-step)]
            (let [k' (t/table-get-number table k)]
              (when (= k' k)
                (t/table-set-number! table k p)))))
        (recur (+ p iter-step))))))

(defn- table-factors
  "Factorize integer."
  [table ^Integer n]
  (t/table-check-contains table n)
  (lazy-seq
   (when (> n 1)
     (let [d (t/table-get-number table n)]
       (cons d (table-factors table (quot n d)))))))

(defn- table-prime?
  "Check does given integer is `n` prime."
  [table n]
  (t/table-check-contains table n)
  (let [n' (t/table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- table-primes
  [table]
  (->> (map vector (t/table-keys table) (t/table-values table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defrecord FullTableFactorization [table]
  f/Factorization
  (factors [this n] (table-factors table n))
  (prime? [this n] (table-prime? table n))
  (primes [this] (table-primes table))
  (in-domain? [this n] (t/table-contains? table n)))



(defrecord OddTableFactorization [table upper-limit]
  f/Factorization
  (factors [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (table-factors table rest))))
  (prime? [this n] (u/check-pos-int n)
    (cond
      (< upper-limit 2) false
      (= n 2) true
      :else (let [[power-of-two rest] (u/power-of-two-parts n)]
              (and (zero? power-of-two) (table-prime? table rest)))))
  (primes [this] (let [seq (table-primes table)]
                   (if (>= upper-limit 2)
                     (cons 2 seq)
                     seq)))
  (in-domain? [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (t/table-contains? table rest))))

(defmulti make-factorization (fn [name upper-limit] name))

(defmethod make-factorization :full [_ upper-limit]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [table (t/make-for-sieve :full upper-limit)]
    (sieve table 2)
    (->FullTableFactorization table)))

(defmethod make-factorization :odd [_ upper-limit]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        table (t/make-for-sieve :odd odd-upper-limit)]
    (sieve table 3)
    (->OddTableFactorization table upper-limit)))




