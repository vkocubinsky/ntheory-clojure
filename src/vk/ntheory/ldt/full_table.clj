(ns vk.ntheory.ldt.full-table
  "Table for store positive integers."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.util :as u]))

(defn init-keys
  [upper-limit]
  (range 1 (inc upper-limit)))

(defrecord FullTable [^int upper-limit ^ints arr]
  t/Table
  (table-set-number! [this k v]
    (assert (t/table-contains? this k))
    (aset-int arr (dec k) v))
  (table-get-number [this k]
    (assert (t/table-contains? this k))
    (aget arr (dec k)))
  (table-contains? [this k] (and (pos-int? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-keys [this] (init-keys upper-limit))
)

(defmethod t/make-for-sieve :full  [_ upper-limit]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (init-keys upper-limit))))














