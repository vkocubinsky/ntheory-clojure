(ns vk.ntheory.ldt.table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.ldt.table :as t]
   [vk.ntheory.ldt.odd-table :as odd]
   [vk.ntheory.ldt.full-table :as full]
   [clojure.string :as str]))

(deftest full-index-table-vals-test
  (doseq [array-type [:int :short]]
    (are [upper-limit numbers] (= numbers (t/table-vals
                                           (t/make-table {:table-type :full
                                                    :init-type :index
                                                    :array-type :int
                                                    :upper-limit upper-limit})))
      1  [1]
      2  [1 2]
      3  [1 2 3]
      4  [1 2 3 4]
      5  [1 2 3 4 5])))

(deftest odd-index-table-vals-test
  (doseq [array-type [:int :short]]
    (are [upper-limit numbers] (= numbers (t/table-vals
                                           (t/make-table {:table-type :odd
                                                    :init-type :index
                                                    :array-type array-type
                                                    :upper-limit upper-limit})))
      1  [1]
      3  [1 3]
      5  [1 3 5]
      7  [1 3 5 7])))


(deftest ones-table-vals-test
  (doseq [table-type [:full :odd]
          init-type [:ones]
          array-type [:int :short]
          upper-limit [11]]
    (let [table-spec {:table-type table-type
                      :init-type init-type
                      :array-type array-type
                      :upper-limit upper-limit}
          table (t/make-table table-spec)]
      (testing (str table-spec)
        (is (every? (partial = 1) (t/table-vals table)))))))

(deftest table-contains-set-get-test
  (letfn [(table-test-helper [table]
            (doseq [n (t/table-keys table)]
              (is (t/table-contains? table n))
              (let [v (inc (rand-int (t/table-upper-limit table)))]
                (t/table-set-number! table n v)
                (is (= v (t/table-get-number table n))))))]
    (doseq [table-type [:full :odd]
            init-type [:index :ones]
            array-type [:int :short]
            upper-limit [11]]
      (let [table-spec {:table-type table-type
                        :init-type init-type
                        :array-type array-type
                        :upper-limit upper-limit}
            table (t/make-table table-spec)]
        (testing (str table-spec)
          (table-test-helper table))))))


