(ns vk.ntheory.ldt.odd-table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.odd-table :as t]
   [clojure.string :as str]))

(deftest table-vals-test
  (are [upper-limit numbers] (= numbers (t/table-vals
                                         (t/make-table upper-limit)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]))

(deftest table-keys-test
  (are [upper-limit numbers] (= numbers (t/table-keys
                                         (t/make-table upper-limit)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]))

(deftest table-contains?-test
  (let [table (make-table 11)]
    (doseq [k (table-keys table)]
      (is (t/table-contains? table k)))))

(deftest table-set-get-test
  (let [table (make-table 11)
        v 100]
    (doseq [k (table-keys table)]
      (t/table-set-number! table k v)
      (is (= v (t/table-get-number table k))))))







