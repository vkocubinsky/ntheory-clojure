(ns vk.ntheory.factorization-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as fz]
   [vk.ntheory.odd-fz]
   [vk.ntheory.odd-sieve-fz]
   [vk.ntheory.even-fz]
   [vk.ntheory.odd-table :as tbl]
   [vk.ntheory.test-helpers :as helpers]))

(deftest odd-table-content-test
  (Are [upper-limit numbers] (= numbers (let [fz (fz/make {:type :odd-sieve-fz :upper-limit upper-limit})
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

(def one-spec {:type :one-fz})
(def odd-sieve-spec {:type :odd-sieve-fz :upper-limit helpers/factorization-data-upper-limit})
(def odd-spec {:type :odd-fz :odd-sieve-fz odd-sieve-spec})
(def even-spec {:type :even-fz :odd-fz odd-spec})

(deftest factorization-test
  (doseq [spec [odd-sieve-spec odd-spec even-spec]]
    (testing (str "Spec " spec)
      (let [factorizer (fz/make spec)]
        (helpers/factorization-test-helper factorizer)))))

(deftest odd-primes-test
  (doseq [spec [odd-sieve-spec
                odd-spec]]
    (testing (str "Spec " spec)
      (let [fz (fz/make spec)]
        (is (= (take-while #(<= % helpers/factorization-data-upper-limit) (fz/primes fz)) (rest helpers/primes)))))))

(deftest even-primes-test
  (doseq [spec [even-spec]]
    (testing (str "Spec " spec)
      (let [fz (fz/make spec)]
        (is (= (take-while #(<= % helpers/factorization-data-upper-limit) (fz/primes fz)) helpers/primes))))))














