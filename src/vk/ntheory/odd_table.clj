(ns vk.ntheory.odd-table
  "Table of positive integers indexed by odd positive integers.")

(defn- odd-keys [upper-limit]
  (range 1 (inc upper-limit) 2))

(defn tcontains-key?
  "Does the given positive integer k exist in table?"
  [table k]
  (and (pos-int? k) (odd? k) (<= k (:upper-limit table))))

(defn tset-number!
  "Store value v for key k, where k is positive odd integer."
  [table ^Integer k ^Integer v]
  (assert (tcontains-key? table k))
  (let [^ints arr (:array table)
        idx (bit-shift-right k 1)]
    (aset arr idx v)))

(defn tget-number
  "Get value for given key k, where k is positive odd integer."
  [table ^Integer k]
  (assert (tcontains-key? table k))
  (let [^ints arr (:array table)
        idx (bit-shift-right k 1)]
    (aget arr idx)))

(defn tkeys
  "Return all key numbers."
  [table]
  (odd-keys (:upper-limit table)))

(defn tvals
  "Return all value numbers."
  [table] (seq (:array table)))

(defn make
  "Make odd table filled by odd numbers 1,3,5 ... ."
  [upper-limit]
  (assert (and (pos-int? upper-limit) (odd? upper-limit)))
  {:upper-limit upper-limit :array (int-array (odd-keys upper-limit))})

(comment
  (make  11)
  )











