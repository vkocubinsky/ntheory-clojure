(ns vk.ntheory.basic-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.basic :as b :refer [divides? pow gcd gcd-extended sign order]]))

(deftest sign-test
  (is (= 1 (sign 2)))
  (is (= (- 1) (sign (- 2))))
  (is (= 0 (sign 0)))
  )

(deftest divides?-test
  (testing "Divide by zero"
    (is (thrown? Exception (divides? 0 8)))
    (is (thrown? Exception (divides? 0 -8)))
  (testing "Everything divides 0, but 0"
    (is (divides? 2 0))
    (is (thrown? Exception (divides? 0 0))))
    ) 
  (testing "simple cases"
    (are [x y] (= x y)
      true (divides? 2 8)
      true (divides? -2 8)
      true (divides? 2 -8)
      true (divides? -2 -8)
      false (divides? 2 9)
      false (divides? 2 -9)
      false (divides? -2 9)
      false (divides? -2 -9))))

(deftest pow-test
  (testing "negative numbers"
    (is (thrown? Exception (pow 2 -1))))
  (testing "power of 2"
    (are [x y] (= x y)
      1 (pow 2 0)
      2 (pow 2 1)
      4 (pow 2 2)
      8 (pow 2 3)
      16 (pow 2 4)
      32 (pow 2 5)
      64 (pow 2 6)
      128 (pow 2 7)
      256 (pow 2 8)
      512 (pow 2 9)
      1024 (pow 2 10))))

(deftest order-test
  (are [x y] (= x y)
    0 (order 2 1)
    1 (order 2 2)
    0 (order 2 3)
    2 (order 2 4)
    0 (order 2 5)
    1 (order 2 6)
    0 (order 2 7)
    3 (order 2 8)
    0 (order 2 9)
    1 (order 2 10)))


(deftest gcd-test
  (are [x y] (= x y)
    0 (gcd 0 0)
    6 (gcd 6 0)
    6 (gcd -6 0)
    6 (gcd 0 6)
    6 (gcd 0 -6)
    6  (gcd 12 18)
    6 (gcd -12 18)
    6 (gcd 12 -18)
    6 (gcd -12 -18)))

(deftest gcd-extended-test
  (doseq [a (range -12 12)
          b (range -12 12)]
    (let [[d s t] (gcd-extended a b)]
      (is (= d (+ (* a s) (* b t))))
      (is (= (gcd a b) d)))))
