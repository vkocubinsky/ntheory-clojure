(ns vk.ntheory.ldt
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defn natural? [n]
  (and (int? n) (pos? n)))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for natural number `k`.")
  (table-get-number [this k] "Get value for given natural number `k`.")
  (table-contains? [this k] "Does given natural number `k` in table")
  (table-upper-limit [this] "Return max number in table."))

(defrecord FullTable [^int upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (assert (table-contains? this k))
    (aset-int arr k v))
  (table-get-number [this k]
    (assert (table-contains? this k))
    (aget arr k))
  (table-contains? [this k] (and (natural? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit))

(defn full-init-seq [upper-limit]
  (range (inc upper-limit)))

(defn full-make-table
  "Initialize full table for sieve."
  [upper-limit]
  (assert (natural? upper-limit))
  (->FullTable upper-limit (int-array (full-init-seq upper-limit))))

(defrecord OddTable [upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (assert (table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aset-int arr idx v)))
  (table-get-number [this k]
    (assert (table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)))
  (table-contains? [this k] (and (natural? k) (odd? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit))

(defn odd-init-seq [upper-limit]
  (range 1 (inc upper-limit) 2))

(defn odd-make-table
  [upper-limit]
  (assert (odd? upper-limit))
  (->OddTable upper-limit (int-array (odd-init-seq upper-limit))))

(defn table-check-contains
  "Check does given number `n` in table."
  [table n]
  (when-not (table-contains? table n)
    (throw (ex-info "Out of range" {:upper-limit (table-upper-limit table) :value n}))))

(defn sieve
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

(defn table-int->factors
  "Factorize integer."
  [table ^Integer n]
  (table-check-contains table n)
  (lazy-seq
   (when (> n 1)
     (let [d (table-get-number table n)]
       (cons d (table-int->factors table (quot n d)))))))

(defn table-prime?
  "Check does given integer is `n` prime."
  [table n]
  (table-check-contains table n)
  (let [n' (table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn table-primes
  [table seq]
  (->> table
       :arr
       (map #(vector %1 %2) seq)
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defn full-table-primes
  [table]
  (table-primes table (full-init-seq (table-upper-limit table))))

(defrecord FullLeastDivisorTable [table]
  f/Factorization
  (int->factors [this n] (table-int->factors (:table this) n))
  (prime? [this n] (table-prime? (:table this) n))
  (primes [this] (full-table-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn odd-table-primes
  [table]
  (let [upper-limit (table-upper-limit table)
        seq (table-primes table (odd-init-seq upper-limit))
        ]
    (if (> upper-limit 2)
      (cons 2 seq)
      seq)))

(defn power-of-two-parts
  "Returns power of two and rest for given number"
  [n]
  (let [k (Integer/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))

(defn odd-table-int->factors [table n]
  (let [[power rest] (power-of-two-parts n)]
    (concat (repeat power 2) (table-int->factors table rest))))

(defn odd-table-prime? [table n]
  (cond
    (< (table-upper-limit table) 2) false
    (= n 2) true
    :else (let [[power rest] (power-of-two-parts n)]
            (and (zero? power) (table-prime? table rest)))))

(defrecord OddLeastDivisorTable [table upper-limit]
  f/Factorization
  (int->factors [this n] (odd-table-int->factors (:table this) n))
  (prime? [this n] (odd-table-prime? (:table this) n))
  (primes [this] (odd-table-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn make-full-factorization [upper-limit]
  (when-not (natural? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [table (full-make-table upper-limit)]
    (sieve table 2)
    (->FullLeastDivisorTable table)))

(defn make-odd-factorization [upper-limit]
  (when-not (natural? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        table (odd-make-table odd-upper-limit)]
    (sieve table 3)
    (->OddLeastDivisorTable table upper-limit)))




