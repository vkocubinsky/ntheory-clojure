(ns vk.ntheory.ldt.full-table
  "Table for store positive integers."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.util :as u]))

(defn init-index-keys
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
  (table-keys [this] (init-index-keys upper-limit))
  (table-values [this] (seq arr)))

(defmethod t/make [:full :index :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (init-index-keys upper-limit))))

(defmethod t/make [:full :index :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (short-array (init-index-keys upper-limit))))

(defmethod t/make [:full :ones :int]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (int-array (repeat upper-limit 1))))

(defmethod t/make [:full :ones :short]
  [{:keys [upper-limit]}]
  (assert (pos-int? upper-limit))
  (->FullTable upper-limit (short-array (repeat upper-limit 1))))













