(ns vk.ntheory.ldt-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.ldt :as t]
   [clojure.string :as str]))

(def factorizer-names [:full-ldt :odd-ldt])
(def test-prop-upper-limit 30)

(defn run-for-all-factorizers [f]
  (let [upper-limit 30]
    (doseq [factorizer-name factorizer-names]
      (testing (str "factorizer " factorizer-name)
        (let [factorizer (f/make factorizer-name test-prop-upper-limit)]
          (f factorizer))))))

;; Example of run-for-all-factorizers
(deftest run-for-all-factorizers-test
  (letfn [(test-helper [factorizer]
            (is (some? factorizer)))]
    (run-for-all-factorizers test-helper)))

(defn run-for-all-numbers [f]
  (letfn [(test-helper [factorizer]
            (doseq [n (range 1 test-prop-upper-limit)]
              (testing (str "number " n)
                (f factorizer n))))]
    (run-for-all-factorizers test-helper)))

;; Example of run-for-all-numbers
(deftest run-for-all-numbers-test
  (letfn [(test-helper [factorizer n]
            (is (some? factorizer))
            (is (some? n)))]
    (run-for-all-numbers test-helper)))

(deftest full-table-make-test
  (are [x y] (= y (t/table-content (t/full-table-make x)))
    1  [[1 1]]
    2  [[1 1] [2 2]]
    3  [[1 1] [2 2] [3 3]]
    4  [[1 1] [2 2] [3 3] [4 4]]
    5  [[1 1] [2 2] [3 3] [4 4] [5 5]]))

(deftest make-odd-table-test
  (are [x y] (= y (t/table-content (t/odd-table-make x)))
    1  [[1 1]]
    3  [[1 1] [3 3]]
    5  [[1 1] [3 3] [5 5]]
    7  [[1 1] [3 3] [5 5] [7 7]]))

(defn table-test-helper [upper-limit table numbers]
  (is (= (t/table-upper-limit table) upper-limit))
  (doseq [n numbers]
    (is (t/table-contains? table n))
    (let [v (inc (rand-int upper-limit))]
      (t/table-set-number! table n v)
      (is (= v (t/table-get-number table n)))))
  (is (not (t/table-contains? table (inc upper-limit)))))

(deftest full-table-test
  (table-test-helper 10 (t/full-table-make 10) (range 1 11)))

(deftest odd-table-test
  (table-test-helper 11 (t/odd-table-make 11) (range 1 12 2)))

;; Valery , start here, make single function, think about use factorization-test
;; for retry factorization

(deftest full-factorization-test
  (let [factorizer (f/make :full-ldt 16)]
    (is (= [2 2 3] (f/factors factorizer 12)))
    (is (f/prime? factorizer 2))
    (is (f/prime? factorizer 3))
    (is (= [2 3 5 7 11 13] (f/primes factorizer)))))

(deftest odd-factorization-test
  (let [factorizer (f/make :odd-ldt 16)]
    (is (= [2 2 3] (f/factors factorizer 12)))
    (is (f/prime? factorizer 2))
    (is (f/prime? factorizer 3))
    (is (= [2 3 5 7 11 13] (f/primes factorizer)))))

(deftest factors-test
  (letfn [(test-helper [factorizer-name]
            (let [factorizer (f/make factorizer-name 20)]
              (testing "Out of range"
                (is (thrown? Exception (f/factors factorizer 0)))
                (is (thrown? Exception (f/factors factorizer -1)))
                (is (thrown? Exception (f/factors factorizer 21))))
              (testing "Positive numbers"
                (are [x y] (= y (f/factors factorizer x))
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
                  20 [2 2 5]))))]
    (run-for-all-factorizers test-helper)))

(deftest factors-prop-test
  (letfn [(test-helper [factorizer-name n]
            (let [factorizer (f/make factorizer-name test-prop-upper-limit)]
              (is (= n (->> n (f/factors factorizer) (apply *))))))]
    (run-for-all-numbers test-helper)))

(deftest prime?-test
  (letfn [(test-helper [factorizer-name]
            (let [factorizer (f/make factorizer-name 20)]
              (testing "Out of range"
                (is (thrown? Exception #"Out of range" (f/prime? factorizer 0)))
                (is (thrown? Exception #"Out of range" (f/prime? factorizer -1)))
                (is (thrown? Exception #"Out of range" (f/prime? factorizer 31))))
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
                  20 false))))]
    (run-for-all-factorizers test-helper)))

(deftest primes-test
  (letfn [(test-helper [factorizer-name]
            (are [x y] (= y (f/primes (f/make factorizer-name x)))
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
              20 [2 3 5 7 11 13 17 19]))]
    (run-for-all-factorizers test-helper)))

(deftest primes-prop-test
  (letfn [(test-helper [factorizer-name n]
            (let [factorizer (f/make factorizer-name test-prop-upper-limit)]
              (is (every? #(f/prime? factorizer %) (f/primes factorizer)))))]
    (run-for-all-factorizers test-helper)))
