(ns vk.ntheory.util-test
  (:require
   [clojure.test :refer [deftest is are]]
   [vk.ntheory.util :as util]
   ))


(deftest power-of-two-parts-test
  (are [n parts] (= parts (util/power-of-two-parts n))
    1  [0 1]
    2  [1 1]
    3  [0 3]
    4  [2 1]
    5  [0 5]
    6  [1 3]
    7  [0 7]
    8  [3 1]
    9  [0 9]
    10 [1 5]
    )
  )


(deftest power-of-two-parts-prop-test
  (let [n (inc (rand-int 1000))
        [pow rest] (util/power-of-two-parts n)
        pow-value (apply * (repeat pow 2))
        ]
    (is (odd? rest))
    (is (= n (* pow-value rest)))
    )
     
  )



