(ns vk.ntheory.ldt.table
  "Table for store positive integers."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.util :as u]))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for positive integer `k`.")
  (table-get-number [this k] "Get value for given positive integer `k`.")
  (table-contains? [this k] "Does given positive integer `k` in table")
  (table-upper-limit [this] "Return max number in table.")
  (table-content [this] "Returns sequence of pair [k,v] from the table."))

;; Full table implementation
(defn full-table-init-seq
  [upper-limit]
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

;; Odd table imp-lementation
(defn odd-table-init-seq [upper-limit]
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

(defn table-check-contains
  "Check does given number `n` in table."
  [table n]
  (u/check-true (table-contains? table n)
                "Out of range."
                {:upper-limit (table-upper-limit table) :value n}))












