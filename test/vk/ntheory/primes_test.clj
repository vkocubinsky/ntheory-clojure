(ns vk.ntheory.primes-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.primes :as p])
  )


(deftest test-primes
  (testing "cache"
    (p/cache-reset!)
    (is (= 0 (:upper @p/cache))))
  (testing "small numbers"
    (is [2] (p/primes 2))
    (is [2 3] (p/primes 3))
    (is [2 3] (p/primes 4))
    (is [2 3 5] (p/primes 5))
    (is [2 3 5] (p/primes 6))
    (is [2 3 5 7] (p/primes 7))
    (is [2 3 5 7] (p/primes 9))
    (is [2 3 5 7] (p/primes 8))
    (is [2 3 5 7] (p/primes 10))
    (is [2 3 5 7 11 13 17 19 23 29] (p/primes 30))))

(deftest test-integer>factors-map
  (testing "Negative numbers"
    (is (thrown? Exception (p/integer->factors-map 0)))
    (is (thrown? Exception (p/integer->factors-map -1))))
  (testing "Unit"
    (is (= {} (p/integer->factors-map 1))))
  (testing "Positive numbers"
    (are [x y] (= x y)
      {2 1} (p/integer->factors-map 2)
      {3 1} (p/integer->factors-map 3)
      {2 2} (p/integer->factors-map 4)
      {5 1} (p/integer->factors-map 5)
      {2 1, 3 1} (p/integer->factors-map 6)
      {7 1} (p/integer->factors-map 7)
      {2 3} (p/integer->factors-map 8)
      {3 2} (p/integer->factors-map 9)
      {2 1, 5 1} (p/integer->factors-map 10)
      {11 1} (p/integer->factors-map 11)
      {2 2, 3 1} (p/integer->factors-map 12)
      {13 1} (p/integer->factors-map 13)
      {2 1, 7 1} (p/integer->factors-map 14)
      {3 1, 5 1} (p/integer->factors-map 15)
      {2 4} (p/integer->factors-map 16)
      {17 1} (p/integer->factors-map 17)
      {2 1, 3 2} (p/integer->factors-map 18)
      {19 1} (p/integer->factors-map 19)
      {2 2, 5 1} (p/integer->factors-map 20))))

(deftest test-integer>factors-count
  (testing "Negative numbers"
    (is (thrown? Exception (p/integer->factors-count 0)))
    (is (thrown? Exception (p/integer->factors-count -1))))
  (testing "Unit"
    (is (= [] (p/integer->factors-count 1))))
  (testing "Positive numbers"
    (are [x y] (= x y)
      [[2 1]] (p/integer->factors-count 2)
      [[3 1]] (p/integer->factors-count 3)
      [[2 2]] (p/integer->factors-count 4)
      [[5 1]] (p/integer->factors-count 5)
      [[2 1] [3 1]] (p/integer->factors-count 6)
      [[7 1]] (p/integer->factors-count 7)
      [[2 3]] (p/integer->factors-count 8)
      [[3 2]] (p/integer->factors-count 9)
      [[2 1] [5 1]] (p/integer->factors-count 10)
      [[11 1]] (p/integer->factors-count 11)
      [[2 2] [3 1]] (p/integer->factors-count 12)
      [[13 1]] (p/integer->factors-count 13)
      [[2 1] [7 1]] (p/integer->factors-count 14)
      [[3 1] [5 1]] (p/integer->factors-count 15)
      [[2 4]] (p/integer->factors-count 16)
      [[17 1]] (p/integer->factors-count 17)
      [[2 1] [3 2]] (p/integer->factors-count 18)
      [[19 1]] (p/integer->factors-count 19)
      [[2 2] [5 1]] (p/integer->factors-count 20))))

(deftest test-integer>factors-paritions
  (testing "Negative numbers"
    (is (thrown? Exception (p/integer->factors-partitions 0)))
    (is (thrown? Exception (p/integer->factors-partitions -1))))
  (testing "Unit"
    (is (= [] (p/integer->factors-partitions 1))))
  (testing "Positive numbers"
    (are [x y] (= x y)
      [[2]] (p/integer->factors-partitions 2)
      [[3]] (p/integer->factors-partitions 3)
      [[2 2]] (p/integer->factors-partitions 4)
      [[5]] (p/integer->factors-partitions 5)
      [[2] [3]] (p/integer->factors-partitions 6)
      [[7]] (p/integer->factors-partitions 7)
      [[2 2 2]] (p/integer->factors-partitions 8)
      [[3 3]] (p/integer->factors-partitions 9)
      [[2] [5]] (p/integer->factors-partitions 10)
      [[11]] (p/integer->factors-partitions 11)
      [[2 2] [3]] (p/integer->factors-partitions 12)
      [[13]] (p/integer->factors-partitions 13)
      [[2] [7]] (p/integer->factors-partitions 14)
      [[3] [5]] (p/integer->factors-partitions 15)
      [[2 2 2 2]] (p/integer->factors-partitions 16)
      [[17]] (p/integer->factors-partitions 17)
      [[2] [3 3]] (p/integer->factors-partitions 18)
      [[19]] (p/integer->factors-partitions 19)
      [[2 2] [5]] (p/integer->factors-partitions 20))))


(deftest test-integer>factors
  (testing "Negative numbers"
    (is (thrown? Exception (p/integer->factors 0)))
    (is (thrown? Exception (p/integer->factors -1))))
  (testing "Unit"
    (is (= [] (p/integer->factors 1))))
  (testing "Positive numbers"
    (are [x y] (= x y)
      [2] (p/integer->factors 2)
      [3] (p/integer->factors 3)
      [2 2] (p/integer->factors 4)
      [5] (p/integer->factors 5)
      [2 3] (p/integer->factors 6)
      [7] (p/integer->factors 7)
      [2 2 2] (p/integer->factors 8)
      [3 3] (p/integer->factors 9)
      [2 5] (p/integer->factors 10)
      [11] (p/integer->factors 11)
      [2 2 3] (p/integer->factors 12)
      [13] (p/integer->factors 13)
      [2 7] (p/integer->factors 14)
      [3 5] (p/integer->factors 15)
      [2 2 2 2] (p/integer->factors 16)
      [17] (p/integer->factors 17)
      [2 3 3] (p/integer->factors 18)
      [19] (p/integer->factors 19)
      [2 2 5] (p/integer->factors 20))))


(deftest test-factorization-properties
  (doseq [n (range 1 100)]
    (is (= n (p/factors-count->integer (p/integer->factors-count n))))
    (is (= n (p/factors-count->integer (p/integer->factors-map n))))
    (is (= n (p/factors-partitions->integer (p/integer->factors-partitions n))))
    (is (= n (p/factors->integer (p/integer->factors n))))))
