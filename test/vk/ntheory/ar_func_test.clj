(ns vk.ntheory.ar-func-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [clojure.math :as math]
   [vk.ntheory.ar-func :as af]))

(deftest divisors-test
  (testing "Non postive numbers"
    (is (thrown? Exception (af/divisors 0)))
    (is (thrown? Exception (af/divisors -1))))
  (testing "Small Positive Numbers"
    (are [x y] (= (sort (af/divisors x)) y)
      1  [1]
      2  [1 2]
      3  [1 3]
      4  [1 2 4]
      5  [1 5]
      6  [1 2 3 6]
      7  [1 7]
      8  [1 2 4 8]
      9  [1 3 9]
      10 [1 2 5 10]
      11 [1 11]
      12 [1 2 3 4 6 12])))

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

(deftest primes-count-distinct-test
  (f-test af/primes-count-distinct
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

(deftest primes-count-total-test
  (f-test af/primes-count-total
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

(deftest chebyshev-first-test
  (f-test af/chebyshev-first
          {1 0
           2 (math/log 2)
           3 (+ (math/log 2) (math/log 3))
           4 (+ (math/log 2) (math/log 3))
           5 (+ (math/log 2) (math/log 3) (math/log 5))}))

(deftest chebyshev-second-test
  (f-test af/chebyshev-second
          {1 0
           2 (+ (af/mangoldt 2))
           3 (+ (af/mangoldt 2) (af/mangoldt 3))
           4 (+ (af/mangoldt 2) (af/mangoldt 4) (af/mangoldt 3))
           5 (+ (af/mangoldt 2) (af/mangoldt 4) (af/mangoldt 3) (af/mangoldt 5))}))

(deftest mangoldt-test
  (f-test af/mangoldt
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

(deftest liouville-test
  (f-test af/liouville
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

(deftest mobius-test
  (f-test af/mobius
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

(deftest totient-test
  (f-test af/totient
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

(deftest unit-test
  (f-test af/unit
          {1 1
           2 0
           3 0
           4 0
           5 0}))

(deftest one-test
  (f-test af/one
          {1 1
           2 1
           3 1
           4 1
           5 1}))

(deftest divisors-count-test
  (f-test af/divisors-count
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

(deftest divisors-sum-test
  (f-test af/divisors-sum
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

(deftest divisors-square-sum-test
  (f-test af/divisors-square-sum
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

(deftest divisors-sum-a-test
  (is (af/f= af/divisors-count (af/divisors-sum-x 0)))
  (is (af/f= af/divisors-sum (af/divisors-sum-x 1)))
  (is (af/f= af/divisors-square-sum (af/divisors-sum-x 2))))

(deftest relations-test
  (is (af/f= af/unit (af/d* af/mobius af/one)))
  (is (af/f= identity (af/d* af/totient af/one)))
  (is (af/f= af/divisors-count (af/d* af/one af/one)))
  (is (af/f= af/divisors-sum (af/d* identity af/one)))
  (is (af/f= af/divisors-sum (af/d* af/totient af/divisors-count))))

(deftest inverse-test
  (is (af/f= (af/inverse af/one) af/mobius))
  (is (af/f= (af/inverse af/mobius) af/one)))













