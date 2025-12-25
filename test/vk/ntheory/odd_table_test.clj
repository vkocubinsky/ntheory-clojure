(ns vk.ntheory.odd-table-test
  (:require
   [clojure.test :refer [deftest is are]]
   [vk.ntheory.odd-table :as tbl]))

(deftest tvals-test
  (are [upper-limit numbers] (= numbers (tbl/tvals
                                         (tbl/make upper-limit)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 9]
    11 [1 3 5 7 9 11]))

(deftest tkeys-test
  (doseq [upper-limit (range 1 12 2)]
    (let [table (tbl/make upper-limit)]
      (is (= (tbl/tvals table) (tbl/tkeys table))))))

(deftest tcontains?-test
  (let [table (tbl/make 11)]
    (doseq [k (tbl/tkeys table)]
      (is (tbl/tcontains-key? table k))
      (is (not (tbl/tcontains-key? table (inc k)))))))

(deftest table-set-get-test
  (let [table (tbl/make 11)
        v 100]
    (doseq [k (tbl/tkeys table)]
      (tbl/tset-number! table k v)
      (is (= v (tbl/tget-number table k))))))







