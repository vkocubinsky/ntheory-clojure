(ns vk.ntheory.odd-table
  "Table of positive integers indexed by odd positive integer.
  The table designed to keep least prime divisor of an odd number.
  Internally array is used for store numbers. The size of array is
  $(upper-limit + 1)/2$ which allow save around half of space.
  ")

(set! *warn-on-reflection* true)

(defn odd-keys
  "Returns sequence `1,3,5,...,upper-limit`."
  ([^Integer upper-limit]
   (odd-keys 1 upper-limit))
  ([^Integer start ^Integer upper-limit]
   (range start (inc upper-limit) 2)))

(defn tcontains?
  "Does the given positive odd integer `k` exist in the table?"
  [table ^Integer k]
  (and (pos-int? k) (odd? k) (<= k (:upper-limit table))))

(defn- array-index
  "Convert given number `k` to array index"
  [k]
  (bit-shift-right k 1))

(defn tset!
  "Set value `v` for key `k`, where k is positive odd integer."
  [table ^Integer k ^Integer v]
  {:pre [(tcontains? table k) (pos-int? v)]}
  (let [^ints arr (:array table)
        idx (array-index k)]
    (aset arr idx v)))

(defn tget
  "Get value for given key `k`, where `k` is positive odd integer."
  [table ^Integer k]
  {:pre [(tcontains? table k)]
   :post [#(pos-int? %)]}
  (let [^ints arr (:array table)
        idx (array-index k)]
    (aget arr idx)))

(defn tkeys
  "Return all key numbers."
  [table]
  (odd-keys (:upper-limit table)))

(defn tvals
  "Return all value numbers."
  [table] (seq (:array table)))

(defn make
  "Make odd table filled by odd numbers 1,3,5, ..., upper-limit ."
  [upper-limit]
  {:pre [(pos-int? upper-limit) (odd? upper-limit)]}
  {:upper-limit upper-limit :array (int-array (odd-keys upper-limit))})

(comment
  (let [t (make 11)]
    (tget t 11))
  (:upper-limit (make  11))
  (:array (make  11)))











