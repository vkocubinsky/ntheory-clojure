(ns vk.ntheory.ldt.table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.ldt.table :as t]
   [clojure.string :as str]))

(deftest full-table-make-test
  (are [x y] (= y (t/table-content (t/full-table-make x)))
    1  [[1 1]]
    2  [[1 1] [2 2]]
    3  [[1 1] [2 2] [3 3]]
    4  [[1 1] [2 2] [3 3] [4 4]]
    5  [[1 1] [2 2] [3 3] [4 4] [5 5]]))

(deftest odd-table-make-test
  (are [x y] (= y (t/table-content (t/odd-table-make x)))
    1  [[1 1]]
    3  [[1 1] [3 3]]
    5  [[1 1] [3 3] [5 5]]
    7  [[1 1] [3 3] [5 5] [7 7]]))

(defn table-test-helper [upper-limit table numbers]
  (is (= (t/table-upper-limit table) upper-limit))
  (doseq [n numbers]
    (is (t/table-contains? table n))
    (let [v (inc (rand-int upper-limit))]
      (t/table-set-number! table n v)
      (is (= v (t/table-get-number table n)))))
  (is (not (t/table-contains? table (inc upper-limit)))))

(deftest full-table-test
  (table-test-helper 10 (t/full-table-make 10) (range 1 11 1)))

(deftest odd-table-test
  (table-test-helper 11 (t/odd-table-make 11) (range 1 12 2)))




