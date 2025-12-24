(ns vk.ntheory.test-helpers
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]))

(def factorization-data-upper-limit 31)

(def primes [2 3 5 7 11 13 17 19 23 29 31])

(def factorization-data
  "Test data.

  Columns:
  - number: number for test
  - factors: factors with their multiplicity
  - distinct-factors: discitnct factors
  - prime?: is the number is a prime
  "
  [{:number 1
    :factors []
    :factor-counts []
    :distinct-factors []
    :prime? false}
   {:number 2
    :factors [2]
    :factor-counts [[2 1]]
    :distinct-factors [2]
    :prime? true}
   {:number 3
    :factors [3]
    :factor-counts [[3 1]]
    :distinct-factors [3]
    :prime? true}
   {:number 4
    :factors [2 2]
    :factor-counts [[2 2]]
    :distinct-factors [2]
    :prime? false}
   {:number 5
    :factors [5]
    :factor-counts [[5 1]]
    :distinct-factors [5]
    :prime? true}
   {:number 6
    :factors [2 3]
    :factor-counts [[2 1] [3 1]]
    :distinct-factors [2 3]
    :prime? false}
   {:number 7
    :factors [7]
    :factor-counts [[7 1]]
    :distinct-factors [7]
    :prime? true}
   {:number 8
    :factors [2 2 2]
    :factor-counts [[2 3]]
    :distinct-factors [2]
    :prime? false}
   {:number 9
    :factors [3 3]
    :factor-counts [[3 2]]
    :distinct-factors [3]
    :prime? false}
   {:number 10
    :factors [2 5]
    :factor-counts [[2 1] [5 1]]
    :distinct-factors [2 5]
    :prime? false}
   {:number 11
    :factors [11]
    :factor-counts [[11 1]]
    :distinct-factors [11]
    :prime? true}
   {:number 12
    :factors [2 2 3]
    :factor-counts [[2 2] [3 1]]
    :distinct-factors [2 3]
    :prime? false}
   {:number 13
    :factors [13]
    :factor-counts [[13 1]]
    :distinct-factors [13]
    :prime? true}
   {:number 14
    :factors [2 7]
    :factor-counts [[2 1] [7 1]]
    :distinct-factors [2 7]
    :prime? false}
   {:number 15
    :factors [3 5]
    :factor-counts [[3 1] [5 1]]
    :distinct-factors [3 5]
    :prime? false}
   {:number 16
    :factors [2 2 2 2]
    :factor-counts [[2 4]]
    :distinct-factors [2]
    :prime? false}
   {:number 17
    :factors [17]
    :factor-counts [[17 1]]
    :distinct-factors [17]
    :prime? true}
   {:number 18
    :factors [2 3 3]
    :factor-counts [[2 1] [3 2]]
    :distinct-factors [2 3]
    :prime? false}
   {:number 19
    :factors [19]
    :factor-counts [[19 1]]
    :distinct-factors [19]
    :prime? true}
   {:number 20
    :factors [2 2 5]
    :factor-counts [[2 2] [5 1]]
    :distinct-factors [2 5]
    :prime? false}
   {:number 21
    :factors [3 7]
    :factor-counts [[3 1] [7 1]]
    :distinct-factors [3 7]
    :prime? false}
   {:number 22
    :factors [2 11]
    :factor-counts [[2 1] [11 1]]
    :distinct-factors [2 11]
    :prime? false}
   {:number 23
    :factors [23]
    :factor-counts [[23 1]]
    :distinct-factors [23]
    :prime? true}
   {:number 24
    :factors [2 2 2 3]
    :factor-counts [[2 3] [3 1]]
    :distinct-factors [2 3]
    :prime? false}
   {:number 25
    :factors [5 5]
    :factor-counts [[5 2]]
    :distinct-factors [5]
    :prime? false}
   {:number 26
    :factors [2 13]
    :factor-counts [[2 1] [13 1]]
    :distinct-factors [2 13]
    :prime? false}
   {:number 27
    :factors [3 3 3]
    :factor-counts [[3 3]]
    :distinct-factors [3]
    :prime? false}
   {:number 28
    :factors [2 2 7]
    :factor-counts [[2 2] [7 1]]
    :distinct-factors [2 7]
    :prime? false}
   {:number 29
    :factors [29]
    :factor-counts [[29 1]]
    :distinct-factors [29]
    :prime? true}
   {:number 30
    :factors [2 3 5]
    :factor-counts [[2 1] [3 1] [5 1]]
    :distinct-factors [2 3 5]
    :prime? false}
   {:number 31
    :factors [31]
    :factor-counts [[31 1]]
    :distinct-factors [31]
    :prime? true}])

(defn factorization-test-helper [factorizer]
  (doseq [{:keys [number factors factor-counts distinct-factors prime? primes]} factorization-data
          :when (odd? number)]
    (testing (str "Test number " number)
      (when (odd? number)
        (is (f/in-domain? factorizer number) "in-domain?")
        (is (= factors (f/factors factorizer number)) "factors")
        (is (= factor-counts (f/factor-counts factorizer number)) "factor-counts")
        (is (= distinct-factors (f/distinct-factors factorizer number)) "distinct-factors")
        (is (= prime? (f/prime? factorizer number)) "prime?")))))
