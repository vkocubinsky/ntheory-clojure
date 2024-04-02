(ns vk.ntheory.basic-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.basic :as b]))

(deftest product-test
  (testing "Two sequences"
    (is (= [[0 0] [0 1] [0 2] [1 0] [1 1] [1 2]] (b/product [(range 2) (range 3)]))))
  (testing "One sequence"
    (is (= [[0] [1]] (b/product [(range 2)]))))
  (testing "Zero sequence"
    (is (= [[]] (b/product [])))))

(deftest check-relatively-prime-test
  (testing "Fail"
    (is (thrown? Exception (b/check-relatively-prime 2 6)))
    (is (thrown? Exception (b/check-relatively-prime 3 6))))
  (testing "Success"
    (is (nil? (b/check-relatively-prime 1 6)))
    (is (nil? (b/check-relatively-prime 5 6)))))

(deftest check-not-divides
  (testing "Fail"
    (is (thrown? Exception (b/check-not-divides 2 6)))
    (is (thrown? Exception (b/check-not-divides 3 6))))
  (testing "Success"
    (is (nil? (b/check-not-divides 5 6)))
    (is (nil? (b/check-not-divides 11 6))))
  )

(deftest check-int-test
  (testing "Fail"
    (is (thrown? Exception (b/check-int 1.1))))
  (testing "Success"
    (is (= 1 (b/check-int 1)))))

(deftest check-int-pos-test
  (testing "Fail"
    (is (thrown? Exception (b/check-int-pos 1.1)))
    (is (thrown? Exception (b/check-int-pos -1)))
    (is (thrown? Exception (b/check-int-pos 0))))
  (testing "Success"
    (is (= 1 (b/check-int-pos 1)))
    (is (= 2 (b/check-int-pos 2)))))

(deftest check-int-non-neg-test
  (testing "Fail"
    (is (thrown? Exception (b/check-int-non-neg 1.1)))
    (is (thrown? Exception (b/check-int-non-neg -1)))
    (is (thrown? Exception (b/check-int-non-neg -2))))
  (testing "Success"
    (is (= 0 (b/check-int-non-neg 0)))
    (is (= 1 (b/check-int-non-neg 1)))))

(deftest check-int-non-zero-test
  (testing "Fail"
    (is (thrown? Exception (b/check-int-non-zero 1.1)))
    (is (thrown? Exception (b/check-int-non-zero 0)))
    (testing "Success"
      (is (= -1 (b/check-int-non-zero -1)))
      (is (= 1 (b/check-int-non-zero 1))))))

(deftest m*-test
  (testing "Arity"
    (is (= 1 (b/m* 6)))
    (is (= 5 (b/m* 6 5)))
    (is (= 1 (b/m* 6 7)))
    (is (= 5 (b/m* 6 7 5)))
    (is (= 5 (b/m* 6 5 5 5))))
  (testing "Multiplication to modulo 6"
    (are [x y] (= x y)
      1 (b/m* 6 1 1)
      5 (b/m* 6 1 5)
      5 (b/m* 6 5 1)
      1 (b/m* 6 5 5))))

(deftest m+-test
  (testing "Arity"
    (is (= 0 (b/m+ 6)))
    (is (= 5 (b/m+ 6 5)))
    (is (= 1 (b/m+ 6 7)))
    (is (= 0 (b/m+ 6 7 5)))
    (is (= 3 (b/m+ 6 5 5 5))))
  (testing "Addition to modulo 3"
    (are [x y] (= x y)
      1 (b/m+ 3 1 0)
      2 (b/m+ 3 1 1)
      0 (b/m+ 3 1 2)
      2 (b/m+ 3 2 0)
      0 (b/m+ 3 2 1)
      1 (b/m+ 3 2 2))))

(deftest m**-test
  (are [x y] (= x y)
    1 (b/m** 11 2 0)
    2 (b/m** 11 2 1)
    4 (b/m** 11 2 2)
    8 (b/m** 11 2 3)
    5 (b/m** 11 2 4)
    10 (b/m** 11 2 5)
    9 (b/m** 11 2 6)
    7 (b/m** 11 2 7)
    3 (b/m** 11 2 8)
    6 (b/m** 11 2 9)
    1 (b/m** 11 2 10)))

(deftest divides?-test
  (testing "Divide by zero"
    (is (thrown? Exception (b/divides? 0 8)))
    (is (thrown? Exception (b/divides? 0 -8))))
  (testing "Everything divides 0, but 0"
    (is (b/divides? 1 0))
    (is (b/divides? 2 0))
    (is (thrown? Exception (b/divides? 0 0))))
  (testing "Simple cases"
    (are [x y] (= x y)
      true (b/divides? 1 1)
      true (b/divides? 1 1)
      true (b/divides? 2 8)
      true (b/divides? -2 8)
      true (b/divides? 2 -8)
      true (b/divides? -2 -8)
      false (b/divides? 2 9)
      false (b/divides? 2 -9)
      false (b/divides? -2 9)
      false (b/divides? -2 -9))))

(deftest pow-test
  (testing "Negative powers."
    (is (thrown? Exception (b/pow 2 -1))))
  (testing "Power of -2"
    (are [x y] (= x y)
      1 (b/pow -2 0)
      -2 (b/pow -2 1)
      4 (b/pow -2 2)
      -8 (b/pow -2 3)
      16 (b/pow -2 4)
      -32 (b/pow -2 5)
      64 (b/pow -2 6)
      -128 (b/pow -2 7)
      256 (b/pow -2 8)
      -512 (b/pow -2 9)
      1024 (b/pow -2 10)))
  (testing "Power of 2"
    (are [x y] (= x y)
      1 (b/pow 2 0)
      2 (b/pow 2 1)
      4 (b/pow 2 2)
      8 (b/pow 2 3)
      16 (b/pow 2 4)
      32 (b/pow 2 5)
      64 (b/pow 2 6)
      128 (b/pow 2 7)
      256 (b/pow 2 8)
      512 (b/pow 2 9)
      1024 (b/pow 2 10))))

(deftest sign-test
  (is (= 1 (b/sign 2)))
  (is (= (- 1) (b/sign (- 2))))
  (is (= 0 (b/sign 0))))

(deftest order-test
  (are [x y] (= x y)
    0 (b/order 2 1)
    1 (b/order 2 2)
    0 (b/order 2 3)
    2 (b/order 2 4)
    0 (b/order 2 5)
    1 (b/order 2 6)
    0 (b/order 2 7)
    3 (b/order 2 8)
    0 (b/order 2 9)
    1 (b/order 2 10)))

(deftest gcd-test
  (are [x y] (= x y)
    0 (b/gcd 0 0)
    6 (b/gcd 6 0)
    6 (b/gcd -6 0)
    6 (b/gcd 0 6)
    6 (b/gcd 0 -6)
    6  (b/gcd 12 18)
    6 (b/gcd -12 18)
    6 (b/gcd 12 -18)
    6 (b/gcd -12 -18)))

(deftest lcm-test
  (are [x y] (= x y)
    0 (b/lcm 0 0)
    0 (b/lcm 6 0)
    0 (b/lcm -6 0)
    0 (b/lcm 0 6)
    0 (b/lcm 0 -6)
    36  (b/lcm 12 18)
    36 (b/lcm -12 18)
    36 (b/lcm 12 -18)
    36 (b/lcm -12 -18)))

(deftest gcd-extended-test
  (doseq [a (range -12 12)
          b (range -12 12)]
    (let [[d s t] (b/gcd-extended a b)]
      (is (= d (+ (* a s) (* b t))))
      (is (= (b/gcd a b) d)))))
