(ns vk.ntheory.ldt.table-factorization-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.ldt.table-factorization :as tf]
   [vk.ntheory.ldt.multi-table-factorization :as mf]
   [vk.ntheory.ldt.table :as t]
   [vk.ntheory.ldt.full-table :as ft]
   [vk.ntheory.ldt.odd-table :as ot]
   [clojure.string :as str]))

(def test-prop-upper-limit 30)

(def factorizer-specs [{:type :full-table
                        :upper-limit test-prop-upper-limit}
                       {:type :odd-table
                        :upper-limit test-prop-upper-limit}
                       {:type :full-multi-table
                        :upper-limit test-prop-upper-limit}
                       {:type :odd-multi-table
                        :upper-limit test-prop-upper-limit}
                       ])

(defn run-for-all-factorizers [f]
  (doseq [factorizer-spec factorizer-specs]
    (testing (str "factorizer " factorizer-spec)
      (let [factorizer (f/make-factorization factorizer-spec)]
        (f factorizer)))))

(defn run-for-all-numbers [f]
  (letfn [(test-helper [factorizer]
            (doseq [n (range 1 test-prop-upper-limit)]
              (testing (str "number " n)
                (f factorizer n))))]
    (run-for-all-factorizers test-helper)))



(deftest temporary-test
  (let [factorizer (f/make-factorization {:type :full-multi-table :upper-limit test-prop-upper-limit})]
    (is (= [2 5] (f/factors factorizer 10)))
    ))

(deftest factors-test
  (letfn [(test-helper [factorizer]
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
                20 [2 2 5])))]
    (run-for-all-factorizers test-helper)))

(deftest factors-prop-test
  (letfn [(test-helper [factorizer n]
            (let [factors (f/factors factorizer n)]
              (is (= n (apply * factors)) "Product of factors n equals to n")
              (is (= factors (sort factors)) "Factors must be ordered")))]
    (run-for-all-numbers test-helper)))

(deftest factor-counts-test
  (letfn [(test-helper [factorizer]
            (testing "Positive numbers"
              (are [x y] (= y (f/factor-counts factorizer x))
                1  []
                2  [[2 1]]
                3  [[3 1]]
                4  [[2 2]]
                5  [[5 1]]
                6  [[2 1] [3 1]]
                7  [[7 1]]
                8  [[2 3]]
                9  [[3 2]]
                10 [[2 1] [5 1]]
                11 [[11 1]]
                12 [[2 2] [3 1]]
                13 [[13 1]]
                14 [[2 1] [7 1]]
                15 [[3 1] [5 1]]
                16 [[2 4]]
                17 [[17 1]]
                18 [[2 1] [3 2]]
                19 [[19 1]]
                20 [[2 2] [5 1]])))]
    (run-for-all-factorizers test-helper)))

(deftest factor-counts-prop-test
  (letfn [(test-helper [factorizer n]
            (let [counts (f/factor-counts factorizer n)
                  factors (f/counts->factors counts)]
              (is (= n (apply * factors)) "Product of factors n equals to n")
              (is (= factors (sort factors)) "Factors must be ordered")))]
    (run-for-all-numbers test-helper)))

(deftest distinct-factors-test
  (letfn [(test-helper [factorizer]
            (testing "Positive numbers"
              (are [x y] (= y (f/distinct-factors factorizer x))
                1  []
                2  [2]
                3  [3]
                4  [2]
                5  [5]
                6  [2 3]
                7  [7]
                8  [2]
                9  [3]
                10 [2 5]
                11 [11]
                12 [2 3]
                13 [13]
                14 [2 7]
                15 [3 5]
                16 [2]
                17 [17]
                18 [2 3]
                19 [19]
                20 [2 5])))]
    (run-for-all-factorizers test-helper)))

(deftest prime?-test
  (letfn [(test-helper [factorizer]
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
                20 false)))]
    (run-for-all-factorizers test-helper)))

(deftest primes-test
  (letfn [(test-helper [factorizer]
            (is [2 3 5 7 11 13 17 19] (take-while #(< % 20) (f/primes factorizer))))]
    (run-for-all-factorizers test-helper)))

(deftest primes-prop-test
  (letfn [(test-helper [factorizer]
            (is (every? #(f/prime? factorizer %) (f/primes factorizer))))]
    (run-for-all-factorizers test-helper)))

(deftest in-domain?-test
  (letfn [(test-helper [factorizer n]
            (is (f/in-domain? factorizer n)))]
    (run-for-all-numbers test-helper)))

(deftest odd-factorizer-test
  (let [factorizer (f/make-factorization {:type :odd-table :upper-limit test-prop-upper-limit})]
    (doseq [n (range 1 test-prop-upper-limit)
            k (range 1 3)]
      (testing (str "number: " n " power of 2: " k)
        (let [n' (apply * n (repeat k 2))
              factors' (f/factors factorizer n')]
          (is (f/in-domain? factorizer n'))
          (is (= (repeat k 2) (take k factors'))))))))


