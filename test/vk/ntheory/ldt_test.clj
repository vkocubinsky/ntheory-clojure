(ns vk.ntheory.ldt-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.ldt-full :as ldt]
   [clojure.string :as str]))

(def prop-test-upper-limit 30)

(deftest int->factors-test
  (let [factorizer (ldt/make-factorization 20)]
    (testing "Out of range"
      (is (thrown-with-msg? Exception #"Out of range" (f/int->factors factorizer 0)))
      (is (thrown-with-msg? Exception #"Out of range" (f/int->factors factorizer -1)))
      (is (thrown-with-msg? Exception #"Out of range" (f/int->factors factorizer 31))))
    (testing "Positive numbers"
      (are [x y] (= (f/int->factors factorizer x) y)
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

(deftest int->factors-prop-test
  (let [factorizer (ldt/make-factorization prop-test-upper-limit)]
    (doseq [n (range 1 prop-test-upper-limit)]
      (is (= n (->> n (f/int->factors factorizer) (apply *)))))))

(deftest prime?-test
  (let [factorizer (ldt/make-factorization 20)]
    (testing "Out of range"
      (is (thrown-with-msg? Exception #"Out of range" (f/prime? factorizer 0)))
      (is (thrown-with-msg? Exception #"Out of range" (f/prime? factorizer -1)))
      (is (thrown-with-msg? Exception #"Out of range" (f/prime? factorizer 31))))
    (testing "Positive numbers"
      (are [x y] (= (f/prime? factorizer x) y)
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
  (are [x y] (= (f/primes (ldt/make-factorization x)) y)
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

(deftest primes-prop-test
  (doseq [upper-limit (range 1 prop-test-upper-limit)]
    (let [factorizer (ldt/make-factorization upper-limit)]
      (is (every? #(f/prime? factorizer %) (f/primes factorizer)))
      )
    )
  )





