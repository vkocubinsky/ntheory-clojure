(ns vk.ntheory-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory :as  nt]))


(deftest test-factorize
  (testing "Negative numbers"
    (is (thrown? Exception (nt/factorize 0)))
    (is (thrown? Exception (nt/factorize -1))))
  (testing "Unit"
    (is (= {} (nt/factorize 1))))
  (testing "Positive numbers"
    (are [x y] (= x y)
      {2 1} (nt/factorize 2)
      {3 1} (nt/factorize 3)
      {2 2} (nt/factorize 4)
      {5 1} (nt/factorize 5)
      {2 1, 3 1} (nt/factorize 6)
      {7 1} (nt/factorize 7)
      {2 3} (nt/factorize 8)
      {3 2} (nt/factorize 9)
      {2 1, 5 1} (nt/factorize 10)
      {11 1} (nt/factorize 11)
      {2 2, 3 1} (nt/factorize 12)
      {13 1} (nt/factorize 13)
      {2 1, 7 1} (nt/factorize 14)
      {3 1, 5 1} (nt/factorize 15)
      {2 4} (nt/factorize 16)
      {17 1} (nt/factorize 17)
      {2 1, 3 2} (nt/factorize 18)
      {19 1} (nt/factorize 19)
      {2 2, 5 1} (nt/factorize 20))))

(deftest test-factorize-properties
  (testing "factorize de-factorize is indentity function"
    (doseq [n (range 1 100)]
      (is (= n (nt/de-factorize (nt/factorize n))))))
  )

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

(deftest test-divisors-properties
  (testing "Divisors count comparison"
    (doseq [n (range 1 100)]
      (is (= (nt/divisors-count n) (count (nt/divisors n)))))))

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

(deftest test-id
  (f-test nt/id
          {1 1
           2 2
           3 3
           4 4
           5 5}))

(deftest test-id-a
  (f-test (nt/id-x 2)
          {1    1
           2    4
           3    9
           4   16
           5   25
           6   36
           7   49
           8   64
           9   81
           10 100}))

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
  (is (nt/f-equals nt/id (nt/dirichlet-convolution nt/totient nt/one)))
  (is (nt/f-equals nt/divisors-count (nt/dirichlet-convolution nt/one nt/one)))
  (is (nt/f-equals nt/divisors-sum (nt/dirichlet-convolution nt/id nt/one)))
  (is (nt/f-equals nt/divisors-sum (nt/dirichlet-convolution nt/totient nt/divisors-count))))

(deftest test-inverse
  (is (nt/f-equals (nt/dirichlet-inverse nt/one) nt/mobius))
  (is (nt/f-equals (nt/dirichlet-inverse nt/mobius) nt/one))
  )













