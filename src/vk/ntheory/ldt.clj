(ns vk.ntheory.ldt
  "Least divisor table(full) namespace.

   Least divisor table(full) is an java array where element at index idx
   equal to least(prime) divisor of idx. Elements with index 0 is not used,
   element with index 1 contains 1."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defprotocol Table
  (table-set-number! [arr k v] "Store value v for natural number k.")
  (table-get-number [arr k] "Get value for given natural number k.")
  (table-in-table? [arr k] "Does given natural number k in table")
  (table-upper-limit [arr] "Experimental. Return max number in table."))

(defrecord FullTable [arr]
  Table
  (table-set-number! [this k v]
    (assert (table-in-table? this k) "Number must be in table")
    (aset-int arr k v))
  (table-get-number [this k]
    (assert (table-in-table? this k) "Number must be in table")
    (aget arr k))
  (table-in-table? [this k] (and (int? k) (> k 0) (<= k (table-upper-limit this))))
  (table-upper-limit [this] (let [len (alength arr)]
                              (if (pos? len)
                                (dec (alength arr))
                                0))))

(defrecord OddTable [arr]
  Table
  (table-set-number! [this k v]
    (assert (table-in-table? this k) "Number must be in table")
    (let [idx (bit-shift-right k 1)]
      (aset-int arr idx v)))
  (table-get-number [this k]
    (assert (table-in-table? this k) "Number must be in table")
    (let [idx (bit-shift-right k 1)]
      (aget arr k)))
  (table-in-table? [this k] (and (int? k) (odd? k) (> k 0) (<= k (table-upper-limit this))))
  (table-upper-limit [this]
    (let [len (alength arr)]
      (if (pos? len)
        (inc (bit-shift-left (dec len) 1))
        0))))

(defn- check-in-table
  "Check does given number in table.

  Parameters:
  - table: least divisor table.
  - a: number.  
  "
  [table a]
  (when-not (table-in-table? table a)
    (throw (ex-info "Out of range" {:upper-limit (table-upper-limit table) :value a}))))

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
  "Mark a as multiple of p in least divisor table if it is not already marked."
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
      (let [step (if (= p 2) p (bit-shift-left p 1))]
        (doseq [k (range (* p p) (inc (table-upper-limit table)) step)]
          (mark-multiple table k p))
        (recur (find-prime table (inc p)))))))

(defn- make-odd-table
  [upper-limit]
  (assert (odd? upper-limit) "Upper limit must be odd")
  (sieve (->OddTable (int-array (range 1 (inc upper-limit) 2)))))

(defn- make-full-table
  [upper-limit]
  (sieve (->FullTable (int-array (range (inc upper-limit))))))

(defn- ldt-int->factors
  "Factorize integer.

  Returns: ordered factors of given integer
  "
  [^ints table ^Integer a]
  (check-in-table table a)
  (lazy-seq
   (when (> a 1)
     (let [d (aget table a)]
       (cons d (ldt-int->factors table (quot a d)))))))

(defn- ldt-prime?
  "Check does given integer is a prime."
  [table a]
  (check-in-table table a)
  (let [val (aget table a)]
    (and (> val 1)
         (= val a))))

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
  (int->factors [this a] (ldt-int->factors (:table this) a))
  (prime? [this a] (ldt-prime? (:table this) a))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this a] (table-in-table? (:table this) a)))

(defrecord OddLeastDivisorTable [table]
  f/Factorization
  (int->factors [this a] (ldt-int->factors (:table this) a))
  (prime? [this a] (ldt-prime? (:table this) a))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this a] (table-in-table? (:table this) a)))



(defn make-full-factorization [n]
  (->FullLeastDivisorTable (make-table n)))

(defn make-odd-factorization [n]
  (->FullLeastDivisorTable (make-table n)))



(defn upper-limit [ldt]
  (ldt-upper-limit (:table ldt)))

(defn print-table [ldt]
  (ldt-print-table (:table ldt)))


