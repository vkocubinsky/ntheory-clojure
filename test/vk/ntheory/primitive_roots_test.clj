(ns vk.ntheory.primitive-roots-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.primitive-roots :as pr]))


(deftest order-test
  (are [x y] (= x y)
    10 (pr/order 2 11))
  )

(deftest find-primitive-root-test
  (is (= 2 (pr/find-primitive-root 11)))
  )

(deftest primitive-root?-test
  (is (pr/primitive-root? 2 11))
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
    )
  )
