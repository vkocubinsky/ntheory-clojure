(ns vk.ntheory.primes-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.primes :as p]))

(deftest check-int-pos-max-test
  (testing "Positive integers which not exceeds max"
    (are [x] (= x (p/check-int-pos-max x))
      1 2 3 4 p/max-int))
  (testing "Non positive or exceeds max"
    (are [x] (thrown? IllegalArgumentException (p/check-int-pos-max x))
      -4 -3 -2 -1 0 (inc p/max-int)))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (p/check-int-pos-max x))
      1.1 "s")))

(deftest check-int-non-neg-max-test
  (testing "Positive integers which not exceeds max"
    (are [x] (= x (p/check-int-non-neg-max x))
      0 1 2 3 4 p/max-int))
  (testing "Negative or exceeds max"
    (are [x] (thrown? IllegalArgumentException (p/check-int-non-neg-max x))
      -4 -3 -2 -1 (inc p/max-int)))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (p/check-int-non-neg-max x))
      1.1 "s")))

(deftest check-int-non-zero-max-test
  (testing "Positive integers which not exceeds max"
    (are [x] (= x (p/check-int-non-zero-max x))
      (- p/max-int) -4 -3 -2 -1 1 2 3 4 p/max-int))
  (testing "Zero"
    (is (thrown? IllegalArgumentException (p/check-int-non-zero-max 0))))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (p/check-int-non-zero-max x))
      1.1 "s")))


(deftest check-prime-test
  (testing "Primes"
    (are [x] (= x (p/check-prime x))
      3 5 7 11 13 19
      ))
  (testing "Not primes"
    (are [x] (thrown? IllegalArgumentException (p/check-prime x))
      -3 -2 -1 0 1))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (p/check-prime x))
      1.1 "s")))

(deftest check-odd-prime-test
  (testing "Odd primes"
    (are [x] (= x (p/check-odd-prime x))
      3 5 7 11 13 19
      ))
  (testing "Not odd primes"
    (are [x] (thrown? IllegalArgumentException (p/check-odd-prime x))
      -3 -2 -1 0 1 2))
  (testing "Not integers"
    (are [x] (thrown? IllegalArgumentException (p/check-odd-prime x))
      1.1 "s")))


(deftest primes-test
  (testing "cache"
    (p/cache-reset!)
    (is (= 0 (:upper @p/cache))))
  (testing "small numbers"
    (are [x y] (= (p/primes x) y)
      1 []
      2 [2]
      3 [2 3]
      4 [2 3]
      5 [2 3 5]
      6 [2 3 5]
      7 [2 3 5 7]
      8 [2 3 5 7]
      9 [2 3 5 7]
      10 [2 3 5 7]
      11 [2 3 5 7 11]
      12 [2 3 5 7 11]
      13 [2 3 5 7 11 13]
      30 [2 3 5 7 11 13 17 19 23 29])))

(deftest categorize-test
  (testing "small numbers"
    (are [x y] (= ((juxt p/unit? p/prime? p/composite?) x) y)
      ;; number [unit? prime? composite?] 
      1 [true false false]
      2 [false true false]
      3 [false true false]
      4 [false false true]
      5 [false true false]
      6 [false false true]
      7 [false true false]
      8 [false false true]
      9 [false false true]
      10 [false false true])))

(deftest int->factors-map-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/int->factors-map 0)))
    (is (thrown? Exception (p/int->factors-map -1))))
  (testing "Positive numbers"
    (are [x y] (= (p/int->factors-map x) y)
      1  {}
      2  {2 1}
      3  {3 1}
      4  {2 2}
      5  {5 1}
      6  {2 1, 3 1}
      7  {7 1}
      8  {2 3}
      9  {3 2}
      10 {2 1, 5 1}
      11 {11 1}
      12 {2 2, 3 1}
      13 {13 1}
      14 {2 1, 7 1}
      15 {3 1, 5 1}
      16 {2 4}
      17 {17 1}
      18 {2 1, 3 2}
      19 {19 1}
      20 {2 2, 5 1})))

(deftest int->factors-count-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/int->factors-count 0)))
    (is (thrown? Exception (p/int->factors-count -1))))
  (testing "Positive numbers"
    (are [x y] (= (p/int->factors-count x) y)
      1  []
      2  [[2 1]]
      3  [[3 1]]
      4  [[2 2]]
      5  [[5 1]]
      6  [[2 1] [3 1]]
      7  [[7 1]]
      8  [[2 3]]
      9  [[3 2]]
      10 [[2 1] [5 1]]
      11 [[11 1]]
      12 [[2 2] [3 1]]
      13 [[13 1]]
      14 [[2 1] [7 1]]
      15 [[3 1] [5 1]]
      16 [[2 4]]
      17 [[17 1]]
      18 [[2 1] [3 2]]
      19 [[19 1]]
      20 [[2 2] [5 1]])))

(deftest int->coprime-factors-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/int->coprime-factors 0)))
    (is (thrown? Exception (p/int->coprime-factors -1))))
  (testing "Positive numbers"
    (are [x y] (= (p/int->coprime-factors x) y)
      1  []
      2  [2]
      3  [3]
      4  [4]
      5  [5]
      6  [2 3]
      7  [7]
      8  [8]
      9  [9]
      10 [2 5]
      11 [11]
      12 [4 3]
      13 [13]
      14 [2 7]
      15 [3 5]
      16 [16]
      17 [17]
      18 [2 9]
      19 [19]
      20 [4 5])))

(deftest int->factors-paritions-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/int->factors-partitions 0)))
    (is (thrown? Exception (p/int->factors-partitions -1))))
  (testing "Positive numbers"
    (are [x y] (= (p/int->factors-partitions x) y)
      1  []
      2  [[2]]
      3  [[3]]
      4  [[2 2]]
      5  [[5]]
      6  [[2] [3]]
      7  [[7]]
      8  [[2 2 2]]
      9  [[3 3]]
      10 [[2] [5]]
      11 [[11]]
      12 [[2 2] [3]]
      13 [[13]]
      14 [[2] [7]]
      15 [[3] [5]]
      16 [[2 2 2 2]]
      17 [[17]]
      18 [[2] [3 3]]
      19 [[19]]
      20 [[2 2] [5]])))
(deftest int->factors-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/int->factors 0)))
    (is (thrown? Exception (p/int->factors -1))))
  (testing "Positive numbers"
    (are [x y] (= (p/int->factors x) y)
      1  []
      2  [2]
      3  [3]
      4  [2 2]
      5  [5]
      6  [2 3]
      7  [7]
      8  [2 2 2]
      9  [3 3]
      10 [2 5]
      11 [11]
      12 [2 2 3]
      13 [13]
      14 [2 7]
      15 [3 5]
      16 [2 2 2 2]
      17 [17]
      18 [2 3 3]
      19 [19]
      20 [2 2 5])))

(deftest factorization-properties-test
  (doseq [n (range 1 100)]
    (is (= n (p/factors-count->int (p/int->factors-count n))))
    (is (= n (p/factors-count->int (p/int->factors-map n))))
    (is (= n (p/factors-partitions->int (p/int->factors-partitions n))))
    (is (= n (p/factors->int (p/int->factors n))))
    (is (= n (p/factors->int (p/int->coprime-factors n))))))
