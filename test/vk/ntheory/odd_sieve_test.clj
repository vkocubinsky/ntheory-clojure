(ns vk.ntheory.odd-sieve-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.odd-table :as t]
   [vk.ntheory.odd-sieve :as s]
   [vk.ntheory.test-data :as d]))

(deftest sieve-test
  (are [upper-limit numbers] (= numbers (let [table (t/make-table upper-limit)
                                              table' (s/sieve table)]
                                          (t/table-vals table')))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 3]
    11 [1 3 5 7 3 11]
    13 [1 3 5 7 3 11 13]
    15 [1 3 5 7 3 11 13 3]))

(deftest factorization-test
  (let [factorizer (f/make-factorization {:type :odd-table :upper-limit d/factorization-data-upper-limit})]
    (doseq [{:keys [number factors factor-counts distinct-factors prime? primes]} d/factorization-data
            :when (odd? number)
            ]
      (testing (str "Test number " number)
        (when (odd? number)
          (is (f/in-domain? factorizer number) "in-domain?")
            (is (= factors (f/factors factorizer number)) "factors")
            (is (= factor-counts (f/factor-counts factorizer number)) "factor-counts")
            (is (= distinct-factors (f/distinct-factors factorizer number)) "distinct-factors")
            (is (= prime? (f/prime? factorizer number)) "prime?")
          )))))

(deftest primes-test
  (doseq [{:keys [number primes]} d/factorization-data
          :when (odd? number)
          :let [factorizer (f/make-factorization {:type :odd-table :upper-limit number})]
          ] 
         (testing (str "Test number " number)
           (is (= (rest primes) (f/primes factorizer)) "primes"))))









