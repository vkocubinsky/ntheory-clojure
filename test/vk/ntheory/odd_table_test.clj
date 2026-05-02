(ns vk.ntheory.odd-table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
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
      (is (tbl/tcontains? table k))
      (is (not (tbl/tcontains? table (inc k)))))))

(deftest table-set-get-test
  (let [table (tbl/make 11)
        v 100]
    (doseq [k (tbl/tkeys table)]
      (tbl/tset! table k v)
      (is (= v (tbl/tget table k))))))

(deftest corner-cases-test
  (let [table (tbl/make 11)]
    (testing "tcontains?"
      (is (not (tbl/tcontains? table -1))
          "Index is not positive integer")
      (is (not (tbl/tcontains? table 2))
          "Index is not odd integer")
      (is (not (tbl/tcontains? table 13))
          "Index is more than upper-limit"))
    (testing "tget"
      (is (thrown? AssertionError (tbl/tget table -1))
          "Index is not positive integer")
      (is (thrown? AssertionError (tbl/tget table 2))
          "Index is not odd integer")
      (is (thrown? AssertionError (tbl/tget table 13))
          "Index is more than upper-limit"))
    (testing "tset"
      (is (thrown? AssertionError (tbl/tset! table -1 1))
          "Index is not positive integer")
      (is (thrown? AssertionError (tbl/tset! table 1 -1))
          "Value is not positive integer")
      (is (thrown? AssertionError (tbl/tset! table 2 1))
          "Index is not odd integer")
      (is (thrown? AssertionError (tbl/tset! table 13 1))
          "Index is more than upper-limit"))))











