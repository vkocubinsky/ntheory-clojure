(ns vk.ntheory.odd-table
  "Table of positive integers indexed by odd positive integers."
  (:require [vk.ntheory.util :as util]))

(defn- table-index-keys [upper-limit]
  (range 1 (inc upper-limit) 2))

(defn table-contains-key?
  "Does the given positive integer k exist in table?"
  [table k]
  (and (pos-int? k) (odd? k) (<= k (:upper-limit table))))

(defn table-set-number!
  "Store value v for key k, where k is positive odd integer."
  [table k v]
  (assert (table-contains-key? table k))
  (let [idx (bit-shift-right k 1)]
    (aset (:array table) idx v)))

(defn table-get-number
  "Get value for given key k, where k is positive odd integer."
  [table k]
  (assert (table-contains-key? table k))
  (let [idx (bit-shift-right k 1)]
    (aget (:array table) idx)))

(defn table-keys
  "Return all key numbers."
  [table]
  (table-index-keys (:upper-limit table)))

(defn table-vals
  "Return all value numbers."
  [table] (seq (:array table)))

(defn make-table
  "Make odd table."
  [upper-limit]
  (assert (and (pos-int? upper-limit) (odd? upper-limit)))
  {:upper-limit upper-limit :array (int-array (table-index-keys upper-limit))})

(comment
  (make-table 11)
  )











