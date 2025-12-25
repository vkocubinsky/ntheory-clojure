(ns vk.ntheory.factorization-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.odd-trial-division :as trial-division]
   [vk.ntheory.odd-sieve :as odd-sieve]
   [vk.ntheory.odd-table :as odd-table]
   [vk.ntheory.test-helpers :as helpers]))

(deftest odd-table-content-test
  (are [upper-limit numbers] (= numbers (let [factorization (f/make-factorization {:type :odd-sieve :upper-limit upper-limit})
                                              table (:table factorization)]
                                          (odd-table/table-vals table)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 3]
    11 [1 3 5 7 3 11]
    13 [1 3 5 7 3 11 13]
    15 [1 3 5 7 3 11 13 3]))

(deftest odd-sieve-factorization-test
  (let [factorizer (f/make-factorization {:type :odd-sieve :upper-limit helpers/factorization-data-upper-limit})]
    (helpers/factorization-test-helper factorizer)
    ))

(deftest odd-sieve-primes-test
  (let [factorizer (f/make-factorization {:type :odd-sieve :upper-limit helpers/factorization-data-upper-limit})]
    (is (= (f/primes factorizer) (rest helpers/primes)))
    )
  )

(deftest odd-trial-division-factorization-test
  (let [factorizer (f/make-factorization {:type :odd-trial :cache-upper-limit helpers/factorization-data-upper-limit})]
    (helpers/factorization-test-helper factorizer)
    ))


(deftest odd-trial-division-primes-test
  (let [factorizer (f/make-factorization {:type :odd-trial :cache-upper-limit 11})]
    (is (= (take-while #(<= % 31 ) (f/primes factorizer)) (rest helpers/primes)))
    )
)










