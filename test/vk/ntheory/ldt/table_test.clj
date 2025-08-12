(ns vk.ntheory.ldt.table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.ldt.table :as t]
   [vk.ntheory.ldt.odd-table :as odd]
   [vk.ntheory.ldt.full-table :as full]
   [clojure.string :as str]))

(deftest full-table-keys-test
  (are [upper-limit numbers] (= numbers (t/table-keys (t/make-for-sieve :full upper-limit)))
    1  [1]
    2  [1 2]
    3  [1 2 3]
    4  [1 2 3 4]
    5  [1 2 3 4 5]))

(deftest odd-table-keys-test
  (are [upper-limit numbers] (= numbers (t/table-keys (t/make-for-sieve :odd upper-limit)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]))

(deftest table-test
  (letfn [(table-test-helper [table]
            (doseq [n (t/table-keys table)]
              (is (t/table-contains? table n))
              (let [v (inc (rand-int (t/table-upper-limit table)))]
                (t/table-set-number! table n v)
                (is (= v (t/table-get-number table n))))))]
    (doseq [[name upper-limit] {:full 10 :odd 11}]
      (testing (format "%s %s " name upper-limit)
        (table-test-helper (t/make-for-sieve name upper-limit))))))


