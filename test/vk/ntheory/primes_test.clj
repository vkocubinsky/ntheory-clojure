(ns vk.ntheory.primes-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.primes :as p]))

(deftest primes-test
  (testing "cache"
    (p/cache-reset!)
    (is (= 0 (:upper @p/cache))))
  (testing "small numbers"
    (are [x y] (= x (p/primes y))
      [2] 2
      [2 3] 3
      [2 3] 4
      [2 3 5] 5
      [2 3 5] 6
      [2 3 5 7] 7
      [2 3 5 7] 9
      [2 3 5 7] 8
      [2 3 5 7] 10
      [2 3 5 7 11 13 17 19 23 29] 30)))


(deftest categorize-test
  (testing "small numbers"
    (are [x y] (= x ((juxt p/unit? p/prime? p/composite?) y))
      ;; unit? prime? composite? 
      [true false false] 1
      [false true false] 2
      [false true false] 3
      [false false true] 4
      [false true false] 5
      [false false true] 6
      [false true false] 7
      [false false true] 8
      [false false true] 9
      [false false true] 10)))

(deftest integer>factors-map-test
  (testing "Negative numbers"
    (is (thrown? Exception (p/integer->factors-map 0)))
    (is (thrown? Exception (p/integer->factors-map -1))))
  (testing "Unit"
    (is (= {} (p/integer->factors-map 1))))
  (testing "Positive numbers"
    (are [x y] (= x (p/integer->factors-map y))
      {} 1 
      {2 1} 2
      {3 1} 3
      {2 2} 4
      {5 1} 5
      {2 1, 3 1} 6
      {7 1} 7
      {2 3} 8
      {3 2} 9
      {2 1, 5 1} 10
      {11 1} 11
      {2 2, 3 1} 12
      {13 1} 13
      {2 1, 7 1} 14
      {3 1, 5 1} 15
      {2 4} 16
      {17 1} 17
      {2 1, 3 2} 18
      {19 1} 19
      {2 2, 5 1} 20)))

(deftest integer>factors-count-test
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

(deftest integer>factors-paritions-test
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

(deftest integer>factors-test
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

(deftest factorization-properties-test
  (doseq [n (range 1 100)]
    (is (= n (p/factors-count->integer (p/integer->factors-count n))))
    (is (= n (p/factors-count->integer (p/integer->factors-map n))))
    (is (= n (p/factors-partitions->integer (p/integer->factors-partitions n))))
    (is (= n (p/factors->integer (p/integer->factors n))))))
