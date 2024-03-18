(ns vk.ntheory.congruence-test
  (:require
   [vk.ntheory.congruence :as c]
   [clojure.test :refer [deftest is testing]]))

(deftest solve-test
  (testing "x^2 - 1 ≡ 0")
  (is (= [1 3 5 7] (c/solve #(dec (* % %)) 8))))

(deftest solve-linear-test
  (testing "Example 6x ≡ 7 (mod 15)"
    (is (= #{} (c/solve-linear 6 1 15))))
  (testing "Example 6x ≡ 3 (mod 15)"
    (is (= #{3 8 13} (c/solve-linear 6 3 15))))
  (testing "Example 20x ≡ 44 (mod 108)"
    (is (= #{13 40 67 94} (c/solve-linear 20 44 108))))
  (testing "Example 9x ≡ 8 (mod 34)"
    (is (= #{16} (c/solve-linear 9 8 34))))
  (testing "Example 55x ≡ 7 (mod 87)"
    (is (= #{46} (c/solve-linear 55 7 87)))))
