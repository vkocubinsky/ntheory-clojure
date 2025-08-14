(ns vk.ntheory.ldt.odd-table
  "Odd table for store positive integers."
  (:require [vk.ntheory.ldt.table :as t]))

(defn- init-index-keys [upper-limit]
  (range 1 (inc upper-limit) 2))

(defrecord OddTable [upper-limit arr setfn]
  t/Table
  (table-set-number! [this k v]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (setfn arr idx v)))
  (table-get-number [this k]
    (assert (t/table-contains? this k))
    (let [idx (bit-shift-right k 1)]
      (aget arr idx)))
  (table-contains? [this k] (and (pos-int? k) (odd? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-keys [this] (init-index-keys upper-limit))
  (table-vals [this] (seq arr)))

(defmethod t/make-table [:odd :index :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->OddTable upper-limit (int-array (init-index-keys upper-limit)) aset-int))

(defmethod t/make-table [:odd :index :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->OddTable upper-limit (short-array (init-index-keys upper-limit)) aset-short))

(defmethod t/make-table [:odd :ones :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->OddTable upper-limit (int-array (repeat upper-limit 1)) aset-int))

(defmethod t/make-table [:odd :ones :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->OddTable upper-limit (short-array (repeat upper-limit 1)) aset-short))











