(ns vk.ntheory.ldt.full-table
  "Table for store positive integers."
  (:require
   [vk.ntheory.ldt.table :as t]))

(defn init-index-keys
  [upper-limit]
  (range 1 (inc upper-limit)))

(defrecord FullTable [upper-limit arr setfn]
  t/Table
  (table-set-number! [this k v]
    (assert (t/table-contains? this k))
    (setfn arr (dec k) v))
  (table-get-number [this k]
    (assert (t/table-contains? this k))
    (aget arr (dec k)))
  (table-contains? [this k] (and (pos-int? k) (<= k upper-limit)))
  (table-upper-limit [this] upper-limit)
  (table-keys [this] (init-index-keys upper-limit))
  (table-vals [this] (seq arr)))

(defmethod t/make-table [:full :index :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (init-index-keys upper-limit)) aset-int))

(defmethod t/make-table [:full :index :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (short-array (init-index-keys upper-limit)) aset-short))

(defmethod t/make-table [:full :ones :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (repeat upper-limit 1)) aset-int))

(defmethod t/make-table [:full :ones :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (short-array (repeat upper-limit 1)) aset-short))













