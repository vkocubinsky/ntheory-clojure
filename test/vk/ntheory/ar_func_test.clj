(ns vk.ntheory.ar-func-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [clojure.math :as math]
   [vk.ntheory.ar-func :as nt]))

(deftest test-divisors
  (testing "Non postive numbers"
    (is (thrown? Exception (nt/divisors 0)))
    (is (thrown? Exception (nt/divisors -1))))
  (testing "Small Positive Numbers"
    (are [x y] (= x y)
      [1] (sort (nt/divisors 1))
      [1 2] (sort (nt/divisors 2))
      [1 3] (sort (nt/divisors 3))
      [1 2 4] (sort (nt/divisors 4))
      [1 5] (sort (nt/divisors 5))
      [1 2 3 6] (sort (nt/divisors 6))
      [1 7] (sort (nt/divisors 7))
      [1 2 4 8] (sort (nt/divisors 8))
      [1 3 9] (sort (nt/divisors 9))
      [1 2 5 10] (sort (nt/divisors 10))
      [1 11] (sort (nt/divisors 11))
      [1 2 3 4 6 12] (sort (nt/divisors 12)))))


(defn f-test
  "Test function `f` according to table `m`."
  [f m]
  (testing "Non positive numbers"
    (is (thrown? Exception (f 0)))
    (is (thrown? Exception (f -1))))
  (testing "Positive numbers"
    (doseq [[x fe] m]
      (let [fa (f x)]
        (is (= fe fa) (format "Expected f(%s) == %s, but got %s" x fe fa))))))

(deftest test-primes-count-distinct
  (f-test nt/primes-count-distinct
          {1 0
           2 1
           3 1
           4 1
           5 1
           6 2
           7 1
           8 1
           9 1
           10 2}))

(deftest test-primes-count-total
  (f-test nt/primes-count-total
          {1 0
           2 1
           3 1
           4 2
           5 1
           6 2
           7 1
           8 3
           9 2
           10 2}))

(deftest test-chebyshev-first
  (f-test nt/chebyshev-first
          {1 0
           2 (math/log 2)
           3 (+ (math/log 2) (math/log 3))
           4 (+ (math/log 2) (math/log 3))
           5 (+ (math/log 2) (math/log 3) (math/log 5))}))

(deftest test-chebyshev-second
  (f-test nt/chebyshev-second
          {1 0
           2 (+ (nt/mangoldt 2))
           3 (+ (nt/mangoldt 2) (nt/mangoldt 3))
           4 (+ (nt/mangoldt 2) (nt/mangoldt 4) (nt/mangoldt 3))
           5 (+ (nt/mangoldt 2) (nt/mangoldt 4) (nt/mangoldt 3) (nt/mangoldt 5))}))

(deftest test-mangoldt
  (f-test nt/mangoldt
          {1 0
           2 (math/log 2)
           3 (math/log 3)
           4 (math/log 2)
           5 (math/log 5)
           6 0
           7 (math/log 7)
           8 (math/log 2)
           9 (math/log 3)
           10 0}))

(deftest test-liouville
  (f-test nt/liouville
          {1 1
           2 -1
           3 -1
           4 1
           5 -1
           6 1
           7 -1
           8 -1
           9 1
           10 1}))

(deftest test-mobius
  (f-test nt/mobius
          {1  1
           2 -1
           3 -1
           4  0
           5 -1
           6  1
           7 -1
           8  0
           9  0
           10 1}))

(deftest test-totient
  (f-test nt/totient
          {1  1
           2  1
           3  2
           4  2
           5  4
           6  2
           7  6
           8  4
           9  6
           10 4}))

(deftest test-unit
  (f-test nt/unit
          {1 1
           2 0
           3 0
           4 0
           5 0}))

(deftest test-one
  (f-test nt/one
          {1 1
           2 1
           3 1
           4 1
           5 1}))

(deftest test-divisors-count
  (f-test nt/divisors-count
          {1  1
           2  2
           3  2
           4  3
           5  2
           6  4
           7  2
           8  4
           9  3
           10 4}))

(deftest test-divisors-sum
  (f-test nt/divisors-sum
          {1   1
           2   3
           3   4
           4   7
           5   6
           6  12
           7   8
           8  15
           9  13
           10 18}))

(deftest test-divisors-square-sum
  (f-test nt/divisors-square-sum
          {1    1
           2    5
           3   10
           4   21
           5   26
           6   50
           7   50
           8   85
           9   91
           10 130}))

(deftest test-divisors-sum-a
  (is (nt/f-equals nt/divisors-count (nt/divisors-sum-x 0)))
  (is (nt/f-equals nt/divisors-sum (nt/divisors-sum-x 1)))
  (is (nt/f-equals nt/divisors-square-sum (nt/divisors-sum-x 2))))

(deftest test-relations
  (is (nt/f-equals nt/unit (nt/dirichlet-convolution nt/mobius nt/one)))
  (is (nt/f-equals identity (nt/dirichlet-convolution nt/totient nt/one)))
  (is (nt/f-equals nt/divisors-count (nt/dirichlet-convolution nt/one nt/one)))
  (is (nt/f-equals nt/divisors-sum (nt/dirichlet-convolution identity nt/one)))
  (is (nt/f-equals nt/divisors-sum (nt/dirichlet-convolution nt/totient nt/divisors-count))))

(deftest test-inverse
  (is (nt/f-equals (nt/dirichlet-inverse nt/one) nt/mobius))
  (is (nt/f-equals (nt/dirichlet-inverse nt/mobius) nt/one)))













