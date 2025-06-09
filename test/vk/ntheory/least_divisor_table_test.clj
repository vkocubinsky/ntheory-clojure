(ns vk.ntheory.least-divisor-table-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.least-divisor-table :as tbl]
   [clojure.string :as str]))

(deftest make-table-test
  (are [x y] (= (vec (tbl/make-table x)) y)
    0  [0]
    1  [0 1]
    2  [0 1 2]
    3  [0 1 2 3]
    4  [0 1 2 3 2]
    5  [0 1 2 3 2 5]
    6  [0 1 2 3 2 5 2]
    7  [0 1 2 3 2 5 2 7]
    8  [0 1 2 3 2 5 2 7 2]
    9  [0 1 2 3 2 5 2 7 2 3]
    10 [0 1 2 3 2 5 2 7 2 3 2]
    ))

(deftest upper-limit-test
  (is (= (tbl/upper-limit (tbl/make-table 0)) 0))
  (is (= (tbl/upper-limit (tbl/make-table 1)) 1))
  (is (= (tbl/upper-limit (tbl/make-table 2)) 2)))

(deftest int->factors-test
  (let [table (tbl/make-table 20)]
    (testing "Out of range"
      (is (thrown-with-msg? Exception #"Out of range" (tbl/int->factors table 0)))
      (is (thrown-with-msg? Exception #"Out of range" (tbl/int->factors table -1)))
      (is (thrown-with-msg? Exception #"Out of range" (tbl/int->factors table 31))))
    (testing "Positive numbers"
      (are [x y] (= (tbl/int->factors table x) y)
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
        20 [2 2 5]))))

(deftest prime?-test
  (let [table (tbl/make-table 20)]
    (testing "Out of range"
      (is (thrown-with-msg? Exception #"Out of range" (tbl/prime? table 0)))
      (is (thrown-with-msg? Exception #"Out of range" (tbl/prime? table -1)))
      (is (thrown-with-msg? Exception #"Out of range" (tbl/prime? table 31))))
    (testing "Positive numbers"
      (are [x y] (= (tbl/prime? table x) y)
        1  false
        2  true
        3  true
        4  false
        5  true
        6  false
        7  true
        8  false
        9  false
        10 false
        11 true
        12 false
        13 true
        14 false
        15 false
        16 false
        17 true
        18 false
        19 true
        20 false))))

(deftest primes-test
  (are [x y] (= (tbl/primes (tbl/make-table x)) y)
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
    14 [2 3 5 7 11 13]
    15 [2 3 5 7 11 13]
    16 [2 3 5 7 11 13]
    17 [2 3 5 7 11 13 17]
    18 [2 3 5 7 11 13 17]
    19 [2 3 5 7 11 13 17 19]
    20 [2 3 5 7 11 13 17 19]))

(deftest factorization-properties-test
  (doseq [n (range 1 100)]
    (is (= n (p/factors-count->int (p/int->factors-count n))))
    (is (= n (p/factors-count->int (p/int->factors-map n))))
    (is (= n (p/factors-partitions->int (p/int->factors-partitions n))))
    (is (= n (p/factors->int (p/int->factors n))))
    (is (= n (p/factors->int (p/int->coprime-factors n))))))



(deftest table-print-test
  (let [table (tbl/make-table 10)
        output (with-out-str (tbl/print-table table))
        expected-lines 
          ["| :index | :value |"
           "|--------+--------|"
           "|      0 |      0 |"
           "|      1 |      1 |"
           "|      2 |      2 |"
           "|      3 |      3 |"
           "|      4 |      2 |"
           "|      5 |      5 |"
           "|      6 |      2 |"
           "|      7 |      7 |"
           "|      8 |      2 |"
           "|      9 |      3 |"
           "|     10 |      2 |"]
        expected-output (str/join \newline expected-lines)]
    (is (= (str/trim output) expected-output))))
