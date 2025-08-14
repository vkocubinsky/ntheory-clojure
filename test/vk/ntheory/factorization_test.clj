(ns vk.ntheory.factorization-test
  (:require [vk.ntheory.factorization :as f]
            [clojure.test :refer [deftest is are testing]])
  )

(deftest factors->distinct-test
  (is (= [2 3] (f/factors->distinct [2 2 3 3 3])))
  )

(deftest factors->partitions-test
  (is (= [[2 2] [3 3 3]] (f/factors->partitions [2 2 3 3 3]))))

(deftest factors->counts-test
  (is (= [[2 3] [3 2]] (f/factors->counts [2 2 2 3 3]))))

(deftest counts->factors-test
  (is (= [2 2 2 3 3] (f/counts->factors [[2 3] [3 2]])))
  )

