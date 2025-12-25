(ns vk.ntheory.factorization-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as fz]
   [vk.ntheory.odd-trial :as trial]
   [vk.ntheory.odd-sieve :as sieve]
   [vk.ntheory.even-wrapper :as even]
   [vk.ntheory.odd-table :as tbl]
   [vk.ntheory.test-helpers :as helpers]))

(deftest odd-table-content-test
  (are [upper-limit numbers] (= numbers (let [fz (fz/make {:type :odd-sieve :upper-limit upper-limit})
                                              table (:table fz)]
                                          (tbl/tvals table)))
    1  [1]
    3  [1 3]
    5  [1 3 5]
    7  [1 3 5 7]
    9  [1 3 5 7 3]
    11 [1 3 5 7 3 11]
    13 [1 3 5 7 3 11 13]
    15 [1 3 5 7 3 11 13 3]))

(deftest factorization-test
  (doseq [spec [{:type :odd-sieve :upper-limit helpers/factorization-data-upper-limit}
                {:type :odd-trial :cache-upper-limit 11}
                {:type :even-wrapper :cache-upper-limit 11}]]
    (testing (str "Spec " spec)
      (let [factorizer (fz/make spec)]
        (helpers/factorization-test-helper factorizer)))))


(deftest odd-primes-test
  (doseq [spec [{:type :odd-sieve :upper-limit helpers/factorization-data-upper-limit}
                {:type :odd-trial :cache-upper-limit 11}
                ]]
    (testing (str "Spec " spec)
      (let [fz (fz/make spec)]
        (is (= (take-while #(<= % helpers/factorization-data-upper-limit) (fz/primes fz)) (rest helpers/primes)))))))


(deftest even-primes-test
  (doseq [spec [
                {:type :even-wrapper :cache-upper-limit 11}]]
    (testing (str "Spec " spec)
      (let [fz (fz/make spec)]
        (is (= (take-while #(<= % helpers/factorization-data-upper-limit) (fz/primes fz)) helpers/primes))))))














