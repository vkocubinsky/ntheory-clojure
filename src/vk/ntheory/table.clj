(ns vk.ntheory.table
  "Table for store positive integers."
  (:require [vk.ntheory.util :as util]))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for positive integer `k`.")
  (table-get-number [this k] "Get value for given positive integer `k`.")
  (table-contains? [this k] "Does given positive integer `k` in table")
  (table-upper-limit [this] "Return max number in table.")
  (table-keys [this] "Return all key numbers.")
  (table-vals [this] "Return all numbers."))

(defn table-check-contains
  "Check does given number `n` in table."
  [table n]
  (u/check-true (table-contains? table n)
                "Out of range."
                {:upper-limit (table-upper-limit table) :value n}))




(defn- init-index-keys [upper-limit]
  (range 1 (inc upper-limit) 2))

(defrecord OddTable [upper-limit arr]
  t/Table
  (table-set-number! [this k v]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aset arr idx v)))
  (table-get-number [this k]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)))
  (table-contains? [this k] (and (pos-int? k) (odd? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-keys [this] (init-index-keys upper-limit))
  (table-vals [this] (seq arr)))


(defn make-table [upper-limit]
  (assert (pos-int? upper-limit))
  (->OddTable upper-limit (int-array (init-index-keys upper-limit)))
  )













