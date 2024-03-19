(ns vk.ntheory.validation-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [vk.ntheory.validation :as v]))

(deftest check-int
  (testing "Fail"
    (is (thrown? Exception (v/check-int 1.1))))
  (testing "Success"
    (is (= 1 (v/check-int 1)))))

(deftest check-int-pos-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-pos 1.1)))
    (is (thrown? Exception (v/check-int-pos -1)))
    (is (thrown? Exception (v/check-int-pos 0))))
  (testing "Success"
    (is (= 1 (v/check-int-pos 1)))
    (is (= 2 (v/check-int-pos 2)))))

(deftest check-int-non-neg-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-non-neg 1.1)))
    (is (thrown? Exception (v/check-int-non-neg -1)))
    (is (thrown? Exception (v/check-int-non-neg -2))))
  (testing "Success"
    (is (= 0 (v/check-int-non-neg 0)))
    (is (= 1 (v/check-int-non-neg 1)))))

(deftest check-int-non-zero-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-non-zero 1.1)))
    (is (thrown? Exception (v/check-int-non-zero 0)))
    (testing "Success"
      (is (= -1 (v/check-int-non-zero -1)))
      (is (= 1 (v/check-int-non-zero 1))))))

;;;;;
(deftest check-int-pos-max-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-pos-max 1.1)))
    (is (thrown? Exception (v/check-int-pos-max (inc v/max-int))))
    (is (thrown? Exception (v/check-int-pos-max -1)))
    (is (thrown? Exception (v/check-int-pos-max 0))))
  (testing "Success"
    (is (= 1 (v/check-int-pos-max 1)))
    (is (= 2 (v/check-int-pos-max 2)))
    (is (= v/max-int (v/check-int-pos-max v/max-int)))))

(deftest check-int-non-neg-max-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-non-neg-max 1.1)))
    (is (thrown? Exception (v/check-int-non-neg-max (inc v/max-int))))
    (is (thrown? Exception (v/check-int-non-neg-max -1)))
    (is (thrown? Exception (v/check-int-non-neg-max -2))))
  (testing "Success"
    (is (= 0 (v/check-int-non-neg-max 0)))
    (is (= 1 (v/check-int-non-neg-max 1)))
    (is (= v/max-int (v/check-int-non-neg-max v/max-int)))))

(deftest check-int-non-zero-max-test
  (testing "Fail"
    (is (thrown? Exception (v/check-int-non-zero-max 1.1)))
    (is (thrown? Exception (v/check-int-non-zero-max (inc v/max-int))))
    (is (thrown? Exception (v/check-int-non-zero-max 0)))
    (testing "Success"
      (is (= -1 (v/check-int-non-zero-max -1)))
      (is (= 1 (v/check-int-non-zero-max 1))))
    (is (= v/max-int (v/check-int-non-zero-max v/max-int)))))






