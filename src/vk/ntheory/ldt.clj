(ns vk.ntheory.ldt
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defn- check-true
  "Throws exception when x is not true."
  [x err-msg err-map]
  (when-not x
    (throw (ex-info err-msg err-map))))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for positive integer `k`.")
  (table-get-number [this k] "Get value for given positive integer `k`.")
  (table-contains? [this k] "Does given positive integer `k` in table")
  (table-upper-limit [this] "Return max number in table.")
  (table-content [this] "Returns sequence of pair [k,v] from the table."))

(defn- full-table-init-seq [upper-limit]
  (range (inc upper-limit)))

(defrecord FullTable [^int upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (assert (table-contains? this k))
    (aset-int arr k v))
  (table-get-number [this k]
    (assert (table-contains? this k))
    (aget arr k))
  (table-contains? [this k] (and (pos-int? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-content [this]
    (rest ;; exclude 0
     (map #(vector %1 %2)
          (full-table-init-seq upper-limit)
          arr))))

(defn full-table-make
  "Initialize full table for sieve."
  [upper-limit]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (full-table-init-seq upper-limit))))

(defn- odd-table-init-seq [upper-limit]
  (range 1 (inc upper-limit) 2))

(defrecord OddTable [^int upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (assert (table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aset-int arr idx v)))
  (table-get-number [this k]
    (assert (table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)))
  (table-contains? [this k] (and (pos-int? k) (odd? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-content [this]
    (map #(vector %1 %2)
         (odd-table-init-seq upper-limit)
         arr)))

(defn odd-table-make
  [upper-limit]
  (assert (odd? upper-limit))
  (->OddTable upper-limit (int-array (odd-table-init-seq upper-limit))))

(defn- table-check-contains
  "Check does given number `n` in table."
  [table n]
  (check-true (table-contains? table n)
              "Out of range."
              {:upper-limit (table-upper-limit table) :value n}))

(defn- check-pos-int [n]
  (check-true (pos-int? n) "Expected positive integer."
              {:n n}))

(defn- sieve
  "Sieve of Erathosphene."
  [table start]
  (loop [p  start]
    (if (> (* p p) (table-upper-limit table))
      table
      (let [p' (table-get-number table p)
            mark-step (if (= p 2) p (* p 2))
            iter-step (if (= p 2) 1 2)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (table-upper-limit table)) mark-step)]
            (let [k' (table-get-number table k)]
              (when (= k' k)
                (table-set-number! table k p)))))
        (recur (+ p iter-step))))))

(defn- table-factors
  "Factorize integer."
  [table ^Integer n]
  (table-check-contains table n)
  (lazy-seq
   (when (> n 1)
     (let [d (table-get-number table n)]
       (cons d (table-factors table (quot n d)))))))

(defn- table-prime?
  "Check does given integer is `n` prime."
  [table n]
  (table-check-contains table n)
  (let [n' (table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- table-primes
  [table]
  (->> (table-content table)
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defrecord FullTableFactorization [table]
  f/Factorization
  (factors [this n] (table-factors (:table this) n))
  (prime? [this n] (table-prime? (:table this) n))
  (primes [this] (table-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

;; Valery, may be names are wrong
(defn- odd-table-primes
  [table]
  (let [upper-limit (table-upper-limit table)
        seq (table-primes table)]
    (if (>= upper-limit 2)
      (cons 2 seq)
      seq)))

(defn power-of-two-parts
  "Returns power of two and rest for given number."
  [n]
  (assert (pos-int? n))
  (let [k (Integer/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))

(defn- odd-table-factors [table n]
  (check-pos-int n)
  (let [[power-of-two rest] (power-of-two-parts n)]
    (concat (repeat power-of-two 2) (table-factors table rest))))

(defn- odd-table-prime? [table n]
  (check-pos-int n)
  (cond
    (< (table-upper-limit table) 2) false
    (= n 2) true
    :else (let [[power-of-two rest] (power-of-two-parts n)]
            (and (zero? power-of-two) (table-prime? table rest)))))

(defn- odd-table-contains [table n]
  (check-pos-int n)
  (let [[power-of-two rest] (power-of-two-parts n)]
    (table-contains? table rest)))

(defrecord OddTableFactorization [table upper-limit]
  f/Factorization
  (factors [this n] (odd-table-factors (:table this) n))
  (prime? [this n] (odd-table-prime? (:table this) n))
  (primes [this] (odd-table-primes (:table this)))
  (in-domain? [this n] (odd-table-contains? (:table this) n)))

(defmethod f/make :full-ldt [_ upper-limit]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [table (full-table-make upper-limit)]
    (sieve table 2)
    (->FullTableFactorization table)))

(defmethod f/make :odd-ldt [_ upper-limit]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        table (odd-table-make odd-upper-limit)]
    (sieve table 3)
    (->OddTableFactorization table upper-limit)))




