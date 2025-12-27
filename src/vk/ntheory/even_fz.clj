(ns vk.ntheory.even-fz
  (:require [vk.ntheory.util :as util]
            [vk.ntheory.odd-fz :as od-fz]
            [vk.ntheory.factorization :as fz]))

(defrecord EvenFactorization [odd-fz]
  fz/Factorization
  (factors [this n]
    (assert (fz/in-domain? this n))
    (let [[power-of-two rest] (util/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (fz/factors odd-fz rest))))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (cond
        (= n 2) true
        :else (let [[power-of-two rest] (util/power-of-two-parts n)]
                (and (zero? power-of-two) (fz/prime? odd-fz rest)))))
  (primes [this] (cons 2 (fz/primes odd-fz)))
  (in-domain? [this n]
    (pos? n)))

(defmethod fz/make :even-fz [{:keys [cache-upper-limit]}]
  (assert (pos? cache-upper-limit))
  (let [odd-upper-limit (if (odd? cache-upper-limit)
                          cache-upper-limit
                          (dec cache-upper-limit))
        odd-fz (fz/make {:type :odd-fz :cache-upper-limit odd-upper-limit})]
    (->EvenFactorization odd-fz)))

(comment
  (let [fz (fz/make {:type :even-fz :cache-upper-limit 10})]
    (fz/prime? fz 101))

  (let [fz (fz/make {:type :even-fz :cache-upper-limit 10})]
    (take-while #(< % 100) (fz/primes fz)))

  (let [fz (fz/make {:type :even-fz :cache-upper-limit 100})]
    (fz/factors fz 45234257))

  (let [fz (fz/make {:type :even-fz :cache-upper-limit 100})]
    (fz/factors fz  6035457813276241))
  )


