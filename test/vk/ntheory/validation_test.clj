(ns vk.ntheory.validation-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.validation :as v]))

(deftest integer-positive-test
  (testing "Fail"
    (is (thrown? Exception (v/check-integer-pos -1)))
    (is (thrown? Exception (v/check-integer-pos 0))))
  (testing "Success"
    (is (= 1 (v/check-integer-pos 1)))
    (is (= 2 (v/check-integer-pos 2)))))

(deftest integer-non-negative-test
  (testing "Fail"
    (is (thrown? Exception (v/check-integer-non-negative -1)))
    (is (thrown? Exception (v/check-integer-non-negative -2))))
  (testing "Success"
    (is (= 0 (v/check-integer-non-negative 0)))
    (is (= 1 (v/check-integer-non-negative 1)))))

(deftest integer-max-test
  (testing "Fail"
    (is (thrown? Exception (v/check-integer-max (inc v/max-integer)))))
  (testing "Success"
    (is (= 0 (v/check-integer-non-negative 0)))
    (is (= 1 (v/check-integer-non-negative 1)))))

(deftest integer-range-test
  (testing "Fail"
    (is (thrown? Exception (v/check-integer-range -1)))
    (is (thrown? Exception (v/check-integer-range 0))))
  (is (thrown? Exception (v/check-integer-range (inc v/max-integer))))
  (testing "Success"
    (is (= 1 (v/check-integer-range 1)))
    (is (= 2 (v/check-integer-range 2)))))


