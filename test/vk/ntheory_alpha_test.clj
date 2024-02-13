(ns vk.ntheory-alpha-test
  (:require
   [clojure.test :refer [deftest is are testing]]
   [vk.ntheory-alpha :as  na]))

(deftest test-gcd
  (are [x y] (= x y)
    0 (na/gcd 0 0)
    6 (na/gcd 6 0)
    6 (na/gcd -6 0)
    6 (na/gcd 0 6)
    6 (na/gcd 0 -6)
    6  (na/gcd 12 18)
    6 (na/gcd -12 18)
    6 (na/gcd 12 -18)
    6 (na/gcd -12 -18)))

(deftest test-gcd-extended
  (doseq [a (range -12 12)
          b (range -12 12)]
    (let [[d s t] (na/gcd-extended a b)]
      (is (= d (+ (* a s) (* b t))))
      (is (= (na/gcd a b) d)))))
