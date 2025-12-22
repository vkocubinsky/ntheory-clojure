(ns vk.ntheory.odd-sieve-test
  (:require
   [clojure.test :refer [deftest is are]]
   [vk.ntheory.factorization :as factor]
   [vk.ntheory.odd-table :as table]
   [vk.ntheory.odd-sieve :as sieve]
   ))

(deftest sieve-test
  (are [upper-limit numbers] (= numbers (let [table (table/make-table upper-limit)
                                              table' (sieve/sieve table)]
                                          (table/table-vals table')))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 3]
    11 [1 3 5 7 3 11]
    13 [1 3 5 7 3 11 13]
    15 [1 3 5 7 3 11 13 3]))

(def test-prop-upper-limit 31)


(deftest factors-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]
    (are [x y] (= y (factor/factors factorizer x))
      1  []
      3  [3]
      5  [5]
      7  [7]
      9  [3 3]
      11 [11]
      13 [13]
      15 [3 5]
      17 [17]
      19 [19]
      21 [3 7]
      23 [23]
      25 [5 5]
      27 [3 3 3]
      29 [29]
      31 [31])))


(deftest factor-counts-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]
    (are [x y] (= y (factor/factor-counts factorizer x))
      1  []
      3  [[3 1]]
      5  [[5 1]]
      7  [[7 1]]
      9  [[3 2]]
      11 [[11 1]]
      13 [[13 1]]
      15 [[3 1] [5 1]]
      17 [[17 1]]
      19 [[19 1]]
      21 [[3 1] [7 1]]
      23 [[23 1]]
      25 [[5 2]]
      27 [[3 3]]
      29 [[29 1]]
      31 [[31 1]]))
  )


(deftest distinct-factors-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]
    (are [x y] (= y (factor/distinct-factors factorizer x))
      1  []
      3  [3]
      5  [5]
      7  [7]
      9  [3]
      11 [11]
      13 [13]
      15 [3 5]
      17 [17]
      19 [19]
      21 [3 7]
      23 [23]
      25 [5]
      27 [3]
      29 [29]
      31 [31])))


(deftest prime?-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]

   (are [x y] (= (factor/prime? factorizer x) y)
                1  false
                3  true
                5  true
                7  true
                9  false
                11 true
                13 true
                15 false
                17 true
                19 true
                21 false
                23 true
                25 false
                27 false
                29 true
                31 true
                )))

(deftest primes-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]
    (is (= (factor/primes factorizer) [3 5 7 11 13 17 19 23 29 31]))
   )
  )


(deftest in-domain?-test
  (let [factorizer (factor/make-factorization {:type :odd-table :upper-limit 31})]
    (doseq [n (range 1 32 2)]
      (is (factor/in-domain? factorizer n))
      (is (not (factor/in-domain? factorizer (inc n))))
      )
   )
  )








