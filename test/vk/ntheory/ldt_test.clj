(ns vk.ntheory.ldt-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.ldt :as t]
   [clojure.string :as str]))


(def factorizer-names [:full-ldt :odd-ldt])
(def prop-test-upper-limit 30)

(deftest full-table-make-test
  (are [x y] (= y (t/table-content (t/full-table-make x)))
    1  [[1 1] ]
    2  [[1 1] [2 2]]
    ,,,
    5  [[1 1] [2 2] [3 3] [4 4] [5 5]]))

(deftest full-table-test
  (let [table (t/full-table-make 5)]
    (is (t/table-contains? table 1))
    (is (t/table-contains? table 4))
    (is (t/table-contains? table 5))
    (is (not (t/table-contains? table 6)))
    (t/table-set-number! table 4 2)
    (is (= 2 (t/table-get-number table 4)))))

(deftest make-odd-table-test
  (are [x y] (= y (t/table-content (t/odd-table-make x)))
    1  [[1 1]]
    3  [[1 1] [3 3]]
    ,,,
    5  [[1 1] [3 3] [5 5]]))

(deftest odd-table-test
  (let [table (t/odd-table-make 11)]
    (is (t/table-contains? table 1))
    (is (t/table-contains? table 9))
    (is (t/table-contains? table 11))
    (is (not (t/table-contains? table 13)))
    (t/table-set-number! table 9 3)
    (is (= 3 (t/table-get-number table 9)))))

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

(defn factors-test-helper [factorizer-name]
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
        20 [2 2 5]))))

(deftest factors-test
  (doseq [factorizer-name factorizer-names]
    (testing (str "factorize " factorizer-name)
      (factors-test-helper factorizer-name)
      )
    )
  )



(defn factors-prop-test-helper [factorizer-name]
  (let [factorizer (f/make factorizer-name prop-test-upper-limit)]
    (doseq [n (range 1 prop-test-upper-limit)]
      (is (= n (->> n (f/factors factorizer) (apply *)))))))


(deftest factors-prop-test
  (doseq [factorizer-name factorizer-names]
    (testing (str "factorize " factorizer-name)
      (factors-prop-test-helper factorizer-name)
      )
    )
  )


(defn prime?-test-helper [factorizer-name]
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
        20 false))))

(deftest prime?-test
  (doseq [factorizer-name factorizer-names]
    (testing (str "factorize " factorizer-name)
      (prime?-test-helper factorizer-name)
      )
    )
  )

(defn primes-test-helper [factorizer-name]
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
    20 [2 3 5 7 11 13 17 19]))

(deftest primes-test
  (doseq [factorizer-name factorizer-names]
    (testing (str "factorize " factorizer-name)
      (primes-test-helper factorizer-name)
      )
    )
  )

(defn primes-prop-test-helper [factorizer-name]
  (doseq [upper-limit (range 1 prop-test-upper-limit)]
    (let [factorizer (f/make factorizer-name upper-limit)]
      (is (every? #(f/prime? factorizer %) (f/primes factorizer))))))


(deftest primes-test
  (doseq [factorizer-name factorizer-names]
    (testing (str "factorize " factorizer-name)
      (primes-prop-test-helper factorizer-name)
      )
    )
  )
