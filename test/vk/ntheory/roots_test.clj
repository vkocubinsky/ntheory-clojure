(ns vk.ntheory.roots-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.roots :as r]))


(deftest order-test
  (are [x y] (= x y)
    10 (r/order 2 11))
  )
