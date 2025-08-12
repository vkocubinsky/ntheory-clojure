(ns vk.ntheory.ldt.odd-table
  "Odd table for store positive integers."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.util :as u]))


(defn- init-keys [upper-limit]
  (range 1 (inc upper-limit) 2))

(defrecord OddTable [^int upper-limit ^ints arr]
  t/Table
  (table-set-number! [this k v]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aset-int arr idx v)))
  (table-get-number [this k]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)))
  (table-contains? [this k] (and (pos-int? k) (odd? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-keys [this] (init-keys upper-limit)))

(defmethod t/make-for-sieve :odd [_ upper-limit]
  (assert (odd? upper-limit))
  (->OddTable upper-limit (int-array (init-keys upper-limit))))














