(ns vk.ntheory.congruences-test
  (:require
   [vk.ntheory.congruences :as c]
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


(deftest solve-remainders-test
  (testing "Trivial x ≡ 3 (mod 5)"
    (is (= [3 5] (c/solve-remainders [[3 5]])))
    )
  (testing "x ≡ 9 (mod 34); x ≡ 4 (mod 19)"
    (is (= [213 646] (c/solve-remainders [[9 34] [4 19]]))))
  (testing "x ≡ 29 (mod 63); x ≡ 9 (mod 35)"
    (is (nil? (c/solve-remainders [[29 63][9 35]]))))
  (testing "x ≡ 2 (mod 7); x ≡ 5 (mod 9); x ≡ 11 (mod 15)"
    (is (= [86 315] (c/solve-remainders [[2 7][5 9][11 15]]))))
  )

;; todo : replace to solve-coprime-reimainders
(deftest solve-coprime-remainders-test
  (testing "x ≡ 6 (mod 17); x ≡ 4 (mod 11); x ≡ -3 (mod 8)"
    (is (= [125 1496] (c/solve-coprime-remainders [[6 17] [4 11] [-3 8]])))
    ))

