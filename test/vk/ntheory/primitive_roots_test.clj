(ns vk.ntheory.primitive-roots-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.primitive-roots :as pr]))


(deftest reduced-residue-test
  (is #{1 2 3 4} (pr/reduced-residues 5))
  (is #{1 2 4 5 7 8} (pr/reduced-residues 9))
  (is #{1 5} (pr/reduced-residues 6)))

(deftest order-test
  (are [x y] (= x y)
    10 (pr/order 11 2))
  )

(deftest find-primitive-root-test
  (is (= 2 (pr/find-primitive-root 11)))
  )

(deftest primitive-root?-test
  (is (pr/primitive-root? 11 2))
  )

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
    31 #{3 11 12 13 17 21 22 24}
    )
  )
