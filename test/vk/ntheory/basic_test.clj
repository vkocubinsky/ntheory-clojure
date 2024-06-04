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
    (is (= [[]] (b/product []))))
  (testing "One of sequence is empty"
    (is (empty? (b/product [[1 2 3] []])))
    (is (empty? (b/product [[] []])))))

(deftest relatively-prime?-test
  (are [a b r] (= (b/relatively-prime? a b) r)
    1 6 true
    2 6 false
    3 6 false
    4 6 false
    5 6 true
    ))

(deftest check-at-least-one-non-zero-test
  (testing "At least one non zero pairs"
    (are [x y] (= [x y] (b/check-at-least-one-non-zero x y))
      1 1
      0 1
      1 0))
  (testing "All zero's"
    (is (thrown? IllegalArgumentException (b/check-at-least-one-non-zero 0 0)))))

(deftest check-int-test
  (testing "Small integers"
    (are [x] (= x (b/check-int x))
      -4 -3 -2 -1 0 1 2 3 4))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int x))
      1.1 "s")))

(deftest check-int-pos-test
  (testing "Small positive integers"
    (are [x] (= x (b/check-int-pos x))
      1 2 3 4))
  (testing "Small non positive integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int-pos x))
      -4 -3 -2 -1 0))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int-pos x))
      1.1 "s")))

(deftest check-int-non-neg-test
  (testing "Small non negative integers"
    (are [x] (= x (b/check-int-non-neg x))
      0 1 2 3 4))
  (testing "Small negative integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int-non-neg x))
      -4 -3 -2 -1))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int-non-neg x))
      1.1 "s")))

(deftest check-int-non-zero-test
  (testing "Small non zero integers"
    (are [x] (= x (b/check-int-non-zero x))
      -4 -3 -2 -1 1 2 3 4))
  (testing "Zero"
    (is (thrown-with-msg? IllegalArgumentException #"Expected non zero" (b/check-int-non-zero 0))))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (b/check-int-non-zero x))
      1.1 "s")))

(deftest check-relatively-prime-test
  (testing "Relatively prime pairs"
    (are [x y] (= [x y] (b/check-relatively-prime x y))
      1 6
      5 6
      1 1
      0 1
      1 0))
  (testing "Not relatively prime pairs"
    (are [x y] (thrown? IllegalArgumentException (b/check-relatively-prime x y))
      2 6
      3 6
      ))
  (testing "All zero's"
    (is (thrown-with-msg? IllegalArgumentException #"Expected at least one non zero" (b/check-relatively-prime 0 0))))
  )

(deftest check-not-divides
  (testing "a does not divide b pairs"
    (are [x y] (= [x y] (b/check-not-divides x y))
      4 6
      5 6
      )
    )
  (testing "a divides b"
    (are [x y] (thrown? IllegalArgumentException (b/check-not-divides x y))
      1 6
      2 6
      3 6
      1 0
      1 1
      ))
  (testing "division by 0"
    (is (thrown-with-msg? IllegalArgumentException #"Expected non zero" (b/check-not-divides 0 1))))
  )


(deftest congruent?-test
  (are [m a b r] (= (b/congruent? m a b) r)
    ;;modulo, number a, number b, result
    3 0 3 true
    3 1 4 true
    3 2 5 true
    3 0 1 false
    3 0 2 false
    3 0 3 true
    1 1 1 true))

(deftest mod-mul-test
  (testing "Arity"
    (is (= 1 (b/mod-mul 6)))
    (is (= 5 (b/mod-mul 6 5)))
    (is (= 1 (b/mod-mul 6 7)))
    (is (= 5 (b/mod-mul 6 7 5)))
    (is (= 5 (b/mod-mul 6 5 5 5))))
  (testing "Multiplication to modulo 6"
    (are [x y] (= x y)
      1 (b/mod-mul 6 1 1)
      5 (b/mod-mul 6 1 5)
      5 (b/mod-mul 6 5 1)
      1 (b/mod-mul 6 5 5))))

(deftest mod-add-test
  (testing "Arity"
    (is (= 0 (b/mod-add 6)))
    (is (= 5 (b/mod-add 6 5)))
    (is (= 1 (b/mod-add 6 7)))
    (is (= 0 (b/mod-add 6 7 5)))
    (is (= 3 (b/mod-add 6 5 5 5))))
  (testing "Addition to modulo 3"
    (are [x y] (= x y)
      1 (b/mod-add 3 1 0)
      2 (b/mod-add 3 1 1)
      0 (b/mod-add 3 1 2)
      2 (b/mod-add 3 2 0)
      0 (b/mod-add 3 2 1)
      1 (b/mod-add 3 2 2))))

(deftest mod-pow-test
  (are [x y] (= x y)
    1 (b/mod-pow 11 2 0)
    2 (b/mod-pow 11 2 1)
    4 (b/mod-pow 11 2 2)
    8 (b/mod-pow 11 2 3)
    5 (b/mod-pow 11 2 4)
    10 (b/mod-pow 11 2 5)
    9 (b/mod-pow 11 2 6)
    7 (b/mod-pow 11 2 7)
    3 (b/mod-pow 11 2 8)
    6 (b/mod-pow 11 2 9)
    1 (b/mod-pow 11 2 10)))

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
  (testing "Fail"
    (is (thrown? Exception (b/gcd 0 0))))
  (testing "Success"
    (are [x y] (= x y)
      6 (b/gcd 6 0)
      6 (b/gcd -6 0)
      6 (b/gcd 0 6)
      6 (b/gcd 0 -6)
      6  (b/gcd 12 18)
      6 (b/gcd -12 18)
      6 (b/gcd 12 -18)
      6 (b/gcd -12 -18))))

(deftest lcm-test
  (testing "Fail"
    (is (thrown? Exception (b/lcm 0 0)))
    (is (thrown? Exception (b/lcm 1 0)))
    (is (thrown? Exception (b/lcm 0 1))))
  (testing "Success"
    (are [x y] (= x y)
      36  (b/lcm 12 18)
      36 (b/lcm -12 18)
      36 (b/lcm 12 -18)
      36 (b/lcm -12 -18))))

(deftest gcd-lcm-property
  (doseq [a (range -13 13)
          b (range -13 13)]
    (when (not-any? zero? [a b])
      (let [d (b/gcd a b)
            l (b/lcm a b)]
        (is (= (abs (* a b)) (* d l)))))))

(deftest gcd-property
  (doseq [a (range -13 13)
          b (range -13 13)]
    (when-not (every? zero? [a b])
      (let [[d [s t]] (b/gcd-extended a b)]
        (is (= d (+ (* a s) (* b t))))
        (is (= (b/gcd a b) d))))))
