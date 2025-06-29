(ns vk.ntheory.ldt-full-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory.factorization :as f]
   [vk.ntheory.ldt-full :as ldt]
   [clojure.string :as str]))

(def prop-test-upper-limit 30)

(deftest make-factorization-test
  (are [x y] (= (vec (:table (ldt/make-factorization x))) y)
    0  [0]
    1  [0 1]
    2  [0 1 2]
    3  [0 1 2 3]
    4  [0 1 2 3 2]
    5  [0 1 2 3 2 5]
    6  [0 1 2 3 2 5 2]
    7  [0 1 2 3 2 5 2 7]
    8  [0 1 2 3 2 5 2 7 2]
    9  [0 1 2 3 2 5 2 7 2 3]
    10 [0 1 2 3 2 5 2 7 2 3 2]))

(deftest upper-limit-test
  (is (= (ldt/upper-limit (ldt/make-factorization -1)) 0))
  (is (= (ldt/upper-limit (ldt/make-factorization 0)) 0))
  (is (= (ldt/upper-limit (ldt/make-factorization 1)) 1)))

(deftest upper-limit-prop-test
  (doseq [upper-limit (range 0 prop-test-upper-limit)]
    (let [ldt (ldt/make-factorization upper-limit)]
      (is (= upper-limit (ldt/upper-limit ldt))))))


(deftest table-print-test
  (let [ldt (ldt/make-factorization 10)
        output (with-out-str (ldt/print-table ldt))
        expected-lines
        ["| :index | :value |"
         "|--------+--------|"
         "|      0 |      0 |"
         "|      1 |      1 |"
         "|      2 |      2 |"
         "|      3 |      3 |"
         "|      4 |      2 |"
         "|      5 |      5 |"
         "|      6 |      2 |"
         "|      7 |      7 |"
         "|      8 |      2 |"
         "|      9 |      3 |"
         "|     10 |      2 |"]
        expected-output (str/join \newline expected-lines)]
    (is (= (str/trim output) expected-output))))



