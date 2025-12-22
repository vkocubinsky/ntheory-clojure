(ns vk.ntheory.odd-table-test
  (:require
   [clojure.test :refer [deftest is are]]
   [vk.ntheory.odd-table :as t]))

(deftest table-vals-test
  (are [upper-limit numbers] (= numbers (t/table-vals
                                         (t/make-table upper-limit)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 9]
    11 [1 3 5 7 9 11]))

(deftest table-keys-test
  (doseq [upper-limit (range 1 12 2)]
    (let [table (t/make-table upper-limit)]
      (is (= (t/table-vals table) (t/table-keys table))))))

(deftest table-contains?-test
  (let [table (t/make-table 11)]
    (doseq [k (t/table-keys table)]
      (is (t/table-contains-key? table k))
      (is (not (t/table-contains-key? table (inc k)))))))

(deftest table-set-get-test
  (let [table (t/make-table 11)
        v 100]
    (doseq [k (t/table-keys table)]
      (t/table-set-number! table k v)
      (is (= v (t/table-get-number table k))))))







