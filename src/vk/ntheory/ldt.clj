(ns vk.ntheory.ldt
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defprotocol Table
  (table-set-number! [arr k v] "Store value `v` for natural number `k`.")
  (table-get-number [arr k] "Get value for given natural number `k`.")
  (table-contains? [arr k] "Does given natural number `k` in table")
  (table-upper-limit [arr] "Return max number in table."))

(defrecord EmptyTable []
  Table
  (table-set-number! [this k v]
    (throw (ex-info "Empty table." {:table :empty})))
  (table-get-number [this k]
    (throw (ex-info "Empty table." {:table :empty})))
  (table-contains? [this k]
    (throw (ex-info "Empty table." {:table :empty})))
  (table-upper-limit [this] 0))

(defrecord FullTable [^long upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (when-not (table-contains? this k)
      (throw (ex-info "Number must be in table"
                      {:operation :table-set-number! :k k :v v :upper-limit upper-limit})))
    (aset-int arr k v))
  (table-get-number [this k]
    (when-not (table-contains? this k)
      (throw (ex-info "Number must be in table"
                      {:operation :table-get-number :k k :upper-limit upper-limit})))
    (aget arr k))
  (table-contains? [this k] (and (int? k) (pos? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit))

(defn make-full-table
  "Initialize full table for sieve."
  [upper-limit]
  (when-not (and (int? upper-limit) (pos? upper-limit))
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (->FullTable upper-limit (int-array (range (inc upper-limit)))))

(defrecord OddTable [upper-limit ^ints arr]
  Table
  (table-set-number! [this k v]
    (when-not (table-contains? this k)
      (throw (ex-info "Number must be in table"
                      {:operation :table-set-number! :k k :v v :upper-limit upper-limit})))
    (let [idx (bit-shift-right k 1)]
      (aset-int arr idx v)))
  (table-get-number [this k]
    (when-not (table-contains? this k)
      (throw (ex-info "Number must be in table"
                      {:operation :table-get-number :k k :upper-limit upper-limit})))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)
      ))
  (table-contains? [this k] (and (int? k) (odd? k) (pos? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit))

(defn make-odd-table
  [upper-limit]
  (when-not (and (int? upper-limit) (pos? upper-limit) (odd? upper-limit))
    (throw (ex-info "Upper limit must be positive odd integer" {:upper-limit upper-limit})))
  (->OddTable upper-limit (int-array (range 1 (inc upper-limit) 2))))

(defn- check-table-contains
  "Check does given number `n` in table."
  [table n]
  (when-not (table-contains? table n)
    (throw (ex-info "Out of range" {:upper-limit (table-upper-limit table) :value n}))))

(defn- find-prime
  "Find prime in least divisor table."
  [table start]
  (let [end (table-upper-limit table)]
    (loop [k start]
      (when (<= k end)
        (let [k' (table-get-number table k)]
          (if (= k' k)
            k
            (recur (inc k))))))))

(defn- mark-multiple
  "Mark `k` as multiple of `p` in least divisor table if it is not already marked."
  [^ints table ^Integer k ^Integer p]
  (let [k' (table-get-number table k)]
    (when (= k' k)
      (table-set-number! table k p))))

(defn- sieve
  "Sieve of Erathosphene."
  [table]
  (loop [p  (find-prime table 2)]
    (if (or (nil? p) (> (* p p) (table-upper-limit table)))
      table
      ;; if p != 2 skip even numbers 
      (let [step (if (= p 2) p (* p 2))]
        (doseq [k (range (* p p) (inc (table-upper-limit table)) step)]
          (mark-multiple table k p))
        (recur (find-prime table (inc p)))))))

(defn- ldt-int->factors
  "Factorize integer."
  [table ^Integer n]
  (check-table-contains table n)
  (lazy-seq
   (when (> n 1)
     (let [d (table-get-number table n)]
       (cons d (ldt-int->factors table (quot n d)))))))

(defn- ldt-prime?
  "Check does given integer is `n` prime."
  [table n]
  (check-table-contains table n)
  (let [n' (table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- ldt-primes
  "Make primes sequence from least divisor table"
  [table]
  (->> table
       (keep-indexed #(when (= %1 %2) %1))
       (drop-while #(< % 2))))

(defn- ldt-print-table [table]
  (->> table
       (map-indexed (fn [idx val] {:index idx :value val}))
       (pp/print-table)))

(defrecord FullLeastDivisorTable [table]
  f/Factorization
  (int->factors [this n] (ldt-int->factors (:table this) n))
  (prime? [this n] (ldt-prime? (:table this) n))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn power-of-two-parts
  "Returns power of two and rest for given number"
  [n]
  (let [k (Long/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))

(defn- odd-ldt-int->factors [table n]
  (let [[power rest] n]
    (concat (repeat power 2) (ldt-int->factors table n))))

(defn- odd-ldt-prime? [table n]
  (let [[power rest] n]
    (and (zero? power) (ldt-prime? table n))))

(defrecord OddLeastDivisorTable [table upper-limit]
  f/Factorization
  (int->factors [this n] (odd-ldt-int->factors (:table this) n))
  (prime? [this n] (odd-ldt-prime? (:table this) n))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn make-full-factorization [upper-limit]
  (let [table (make-full-table upper-limit)]
    (sieve table)
    (->FullLeastDivisorTable table)))

(defn make-odd-factorization [upper-limit]
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        table (make-odd-table odd-upper-limit)]
    (sieve table)
    (->OddLeastDivisorTable upper-limit table)))

(defn print-table [ldt]
  (ldt-print-table (:table ldt)))


