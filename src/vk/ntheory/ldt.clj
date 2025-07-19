(ns vk.ntheory.ldt
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for natural number `k`.")
  (table-get-number [this k] "Get value for given natural number `k`.")
  (table-contains? [this k] "Does given natural number `k` in table")
  (table-upper-limit [this] "Return max number in table."))

(defrecord EmptyTable []
  Table
  (table-set-number! [this k v]
    (throw (ex-info "Empty table." {:table :empty})))
  (table-get-number [this k]
    (throw (ex-info "Empty table." {:table :empty})))
  (table-contains? [this k] false)
  (table-upper-limit [this] 0))

(defn make-empty-table []
  (->EmptyTable))

(defrecord FullTable [^int upper-limit ^ints arr]
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
      (aget arr idx)))
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

(defn- sieve
  "Sieve of Erathosphene."
  [table start]
  (loop [p  start]
    (if (> (* p p) (table-upper-limit table))
      table
      (let [p' (table-get-number table p)
            step (if (= p 2) p (* p 2))]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (table-upper-limit table)) step)]
            (let [k' (table-get-number table k)]
              (when (= k' k)
                (table-set-number! table k p)))))

        (recur (+ p step))))))

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
  "Primes for give table. Caller knows sequence of numbers corresponded to the table."
  [table seq]
  (->> table
       :arr
       (map #(vector %1 %2) seq ,,,)
       (filter (fn [[k v]] (= k v)) ,,,)
       (map first ,,,)
       (drop-while #(< % 2) ,,,)))

(defn- full-ldt-primes
  [table]
  (ldt-primes table (range 0 (inc (table-upper-limit table)))))

(defrecord FullLeastDivisorTable [table]
  f/Factorization
  (int->factors [this n] (ldt-int->factors (:table this) n))
  (prime? [this n] (ldt-prime? (:table this) n))
  (primes [this] (full-ldt-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn- odd-ldt-primes
  [table]
  (let [seq (range 1 (inc (table-upper-limit table)) 2)
        upper-limit (table-upper-limit table)]
    (if (> upper-limit 2)
      (cons 2 seq)
      seq)))

(defn power-of-two-parts
  "Returns power of two and rest for given number"
  [n]
  (let [k (Integer/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))

(defn- odd-ldt-int->factors [table n]
  (let [[power rest] (power-of-two-parts n)]
    (concat (repeat power 2) (ldt-int->factors table rest))))

(defn- odd-ldt-prime? [table n]
  (cond
    (< (table-upper-limit table) 2) false
    (= n 2) true
    :else (let [[power rest] (power-of-two-parts n)]
            (and (zero? power) (ldt-prime? table rest)))))

(defrecord OddLeastDivisorTable [table upper-limit]
  f/Factorization
  (int->factors [this n] (odd-ldt-int->factors (:table this) n))
  (prime? [this n] (odd-ldt-prime? (:table this) n))
  (primes [this] (odd-ldt-primes (:table this)))
  (in-domain? [this n] (table-contains? (:table this) n)))

(defn make-full-factorization [upper-limit]
  (if (< upper-limit 2)
    (->EmptyTable)
    (let [table (make-full-table upper-limit)]
      (sieve table 2)
      (->FullLeastDivisorTable table))))

(defn make-odd-factorization [upper-limit]
  (if (< upper-limit 2)
    (->EmptyTable)
    (let [odd-upper-limit (if (odd? upper-limit)
                            upper-limit
                            (dec upper-limit))
          table (make-odd-table odd-upper-limit)]
      (sieve table 3)
      (->OddLeastDivisorTable table upper-limit))))




