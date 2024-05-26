(ns vk.ntheory.primitive-roots-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.primitive-roots :as pr]))

(deftest order-test
  (are [x y] (= (pr/order 11 x) y)
    ;;residue -> order
    1 1
    2 10
    3 5
    4 5
    5 5
    6 10
    7 10
    8 10
    9 5
    10 2))

(deftest order-count-test
  (is (= {1 1
          2 1
          5 4
          10 4} (pr/order-count 11))))

(deftest classify-residues-test
  (are [x y] (= (pr/classify-residues x) y)
    1 ::pr/mod-1
    2 ::pr/mod-any-p
    3 ::pr/mod-any-p
    4 ::pr/mod-any-p**e
    5 ::pr/mod-any-p
    6 ::pr/composite
    7 ::pr/mod-any-p
    8 ::pr/mod-any-p**e
    9 ::pr/mod-any-p**e
    10 ::pr/composite
    15 ::pr/composite))

(deftest reduced-residues-test
  (are [x y] (= (sort (pr/reduced-residues x)) y)
    ;;modulo -> reduced residues
    1 [0]
    2 [1]
    3 [1 2]
    4 [1 3]
    5 [1 2 3 4]
    6 [1 5]
    7 [1 2 3 4 5 6]
    8 [1 3 5 7]
    9 [1 2 4 5 7 8]
    10 [1 3 7 9])
  15 [1 2 4 7 8 11 13 14])

(deftest reduced-residues-vs-brute-force-test
  (doseq [m (range 1 30)]
    (testing (str "Test modulo " m)
      (is (= (sort (pr/reduced-residues' m))
             (sort (pr/reduced-residues m)))))))

(deftest classify-modulo-test
  (are [x y] (= (pr/classify-modulo x) y)
    1 ::pr/mod-1
    2 ::pr/mod-2
    3 ::pr/mod-p
    4 ::pr/mod-4
    5 ::pr/mod-p
    6 ::pr/mod-2p**e
    7 ::pr/mod-p
    8 ::pr/mod-2**e
    9 ::pr/mod-p**e
    10 ::pr/mod-2p**e
    15 ::pr/composite))

(deftest find-primitive-root-test
  (is (= 0 (pr/find-primitive-root 1)))
  (is (= 1 (pr/find-primitive-root 2)))
  (is (= 2 (pr/find-primitive-root 3)))
  (is (= 3 (pr/find-primitive-root 4)))
  (is (= 2 (pr/find-primitive-root 5)))
  (is (nil? (pr/find-primitive-root 8)))
  (is (= 2 (pr/find-primitive-root 11)))
  (is (nil? (pr/find-primitive-root 20))))

(deftest primitive-root?-test
  (are [x y] (= (pr/primitive-root? 11 x) y)
    1 false
    2 true
    3 false
    4 false
    5 false
    6 true
    7 true
    8 true
    9 false
    10 false))

(deftest primitive-roots-test
  (are [x y] (= (set (pr/primitive-roots x)) y)
    1 #{0}
    2 #{1}
    3 #{2}
    4 #{3}
    5 #{2 3}
    6 #{5}
    7 #{3 5}
    8 #{}
    9 #{2 5}
    10 #{3 7}
    11 #{2 6 7 8}
    12 #{}
    13 #{2 6 7 11}
    14 #{3 5}
    15 #{}
    16 #{}
    17 #{3 5 6 7 10 11 12 14}
    18 #{5 11}
    19 #{2 3 10 13 14 15}
    20 #{}
    21 #{}
    22 #{7 13 17 19}
    23 #{5 7 10 11 14 15 17 19 20 21}
    24 #{}
    25 #{2 3 8 12 13 17 22 23}
    26 #{7 11 15 19}
    27 #{2 5 11 14 20 23}
    28 #{}
    29 #{2 3 8 10 11 14 15 18 19 21 26 27}
    30 #{}
    31 #{3 11 12 13 17 21 22 24}))

(deftest index-test
  (is (thrown-with-msg? IllegalArgumentException #"not relatively prime"
                        (pr/index 6 2)))
  (is (thrown-with-msg? IllegalArgumentException #"has not primitive root"
                        (pr/index 20 3)))
  (are [x y] (= y (pr/index 11 2 x))
    1 10
    2 1
    3 8
    4 2
    5 4
    6 9
    7 7
    8 3
    9 6
    10 5))

(deftest power-residue?-test
  (testing "mod 11"
    (are [x y] (= y (pr/power-residue? 11 2 x))
      1 true
      2 false
      3 true
      4 true
      5 true
      6 false
      7 false
      8 false
      9 true
      10 false))
  (testing "mod 8"
    (are [x y] (= y (pr/power-residue? 8 2 x))
      1 true
      3 false
      5 false
      7 false)))

(defn power-residue?-vs-brute-force-comparison
  [m ns]
  (doseq [a (pr/reduced-residues m)
          n ns]
    (is (= (pr/power-residue?' m n a) (pr/power-residue? m n a)))))

(deftest power-residue?-vs-brute-force-test
  (power-residue?-vs-brute-force-comparison 1 [1])
  (power-residue?-vs-brute-force-comparison 2 (range 1 2))
  (power-residue?-vs-brute-force-comparison 4 (range 1 4))
  (power-residue?-vs-brute-force-comparison 7 (range 1 7))
  (power-residue?-vs-brute-force-comparison 16 (range 1 16))
  (power-residue?-vs-brute-force-comparison 24 (range 1 24)))

(deftest solve-power-residue-test
  (are [x y] (= y (pr/solve-power-residue 11 2 x))
    1 #{1 10}
    2 #{}
    3 #{5 6}
    4 #{2 9}
    5 #{4 7}
    6 #{}
    7 #{}
    8 #{}
    9 #{8 3}
    10 #{}))

(defn solve-power-residue-vs-brute-force-comparison
  [m ns]
  (doseq [a (pr/reduced-residues m)
          n ns]
    (is (= (pr/solve-power-residue' m n a) (pr/solve-power-residue m n a)))))

(deftest solve-power-residue-vs-brute-force-test
  (solve-power-residue-vs-brute-force-comparison 1 [1])
  (solve-power-residue-vs-brute-force-comparison 2 (range 1 2))
  (solve-power-residue-vs-brute-force-comparison 4 (range 1 4))
  (solve-power-residue-vs-brute-force-comparison 7 (range 1 7))
  (solve-power-residue-vs-brute-force-comparison 8 (range 1 8))
  (solve-power-residue-vs-brute-force-comparison 16 (range 1 16))
  (solve-power-residue-vs-brute-force-comparison 24 (range 1 24)))

(deftest power-residues-test
  (is (= #{1 4 5 9 3} (apply sorted-set (pr/power-residues 11 2)))))

(defn power-residues-vs-brute-force-comparison
  [m ns]
  (doseq [n ns]
    (is (= (apply sorted-set (pr/power-residues' m n))
           (apply sorted-set (pr/power-residues m n))))))

(deftest power-residues-vs-brute-force-test
  (power-residues-vs-brute-force-comparison 1 [1])
  (power-residues-vs-brute-force-comparison 2 (range 1 2))
  (power-residues-vs-brute-force-comparison 4 (range 1 4))
  (power-residues-vs-brute-force-comparison 7 (range 1 7))
  (power-residues-vs-brute-force-comparison 8 (range 1 8))
  (power-residues-vs-brute-force-comparison 16 (range 1 16))
  (power-residues-vs-brute-force-comparison 24 (range 1 24)))


