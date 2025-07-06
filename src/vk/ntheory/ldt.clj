(ns vk.ntheory.ldt
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

(defprotocol Table
  (table-set-number! [arr k v] "Store value `v` for natural number `k`.")
  (table-get-number [arr k] "Get value for given natural number `k`.")
  (table-in-table? [arr k] "Does given natural number `k` in table")
  (table-upper-limit [arr] "Experimental. Return max number in table."))

(defrecord FullTable [^ints arr]
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

(defrecord OddTable [^ints arr]
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
  "Check does given number `n` in table."
  [table n]
  (when-not (table-in-table? table n)
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
  "Factorize integer."
  [table ^Integer n]
  (check-in-table table n)
  (lazy-seq
   (when (> n 1)
     (let [d (table-get-number table n)]
       (cons d (ldt-int->factors table (quot n d)))))))

(defn- ldt-prime?
  "Check does given integer is `n` prime."
  [table n]
  (check-in-table table n)
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
  (in-domain? [this n] (table-in-table? (:table this) n)))

(defrecord OddLeastDivisorTable [table upper-limit]
  f/Factorization
  (int->factors [this n] (ldt-int->factors (:table this) n))
  (prime? [this n] (ldt-prime? (:table this) n))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this n] (table-in-table? (:table this) n)))

(defn make-full-factorization [upper-limit]
  (->FullLeastDivisorTable (make-full-table upper-limit)))

(defn make-odd-factorization [upper-limit]
  (let [half-upper-limit (bit-shift-right upper-limit 1)
        odd-upper-limit (if (odd? half-upper-limit) half-upper-limit (dec half-upper-limit))]
    (->FullLeastDivisorTable (make-odd-table odd-upper-limit) upper-limit)))

(defn upper-limit [ldt]
  (table-upper-limit (:table ldt)))

(defn print-table [ldt]
  (ldt-print-table (:table ldt)))


