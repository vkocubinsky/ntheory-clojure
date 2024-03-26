(ns vk.ntheory.primitive-roots-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.primitive-roots :as pr]))


(deftest order-test
  (are [x y] (= x y)
    10 (pr/order 2 11))
  )
