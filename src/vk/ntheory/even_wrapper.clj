(ns vk.ntheory.even-wrapper

  (:require [vk.ntheory.util :as util]
            [vk.ntheory.odd-trial :as trial]
            [vk.ntheory.factorization :as fz]))

(defrecord EvenFactorization [odd-trial-fz]
  fz/Factorization
  (factors [this n]
    (assert (fz/in-domain? this n))
    (let [[power-of-two rest] (util/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (fz/factors odd-trial-fz rest))))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (let [cache-upper-limit (trial/cache-upper-limit odd-trial-fz)]
      (cond
        (< cache-upper-limit 2) false
        (= n 2) true
        :else (let [[power-of-two rest] (util/power-of-two-parts n)]
                (and (zero? power-of-two) (fz/prime? odd-trial-fz rest))))))

  (primes [this] (let [seq (fz/primes odd-trial-fz)
                       cache-upper-limit (trial/cache-upper-limit odd-trial-fz)]
                   (if (>= cache-upper-limit 2)
                     (cons 2 seq)
                     seq)))
  (in-domain? [this n]
    (pos-int? n)))

(defmethod fz/make :even-wrapper [{:keys [cache-upper-limit]}]
  (assert (pos-int? cache-upper-limit))
  (let [odd-upper-limit (if (odd? cache-upper-limit)
                          cache-upper-limit
                          (dec cache-upper-limit))
        odd-fz (fz/make {:type :odd-trial :cache-upper-limit odd-upper-limit})]
    (->EvenFactorization odd-fz)))


(comment
  (let [fz (fz/make {:type :even-wrapper :cache-upper-limit 10})]
    (fz/prime? fz 101))

  (let [fz (fz/make {:type :even-wrapper :cache-upper-limit 100})]
    (take 20 (fz/primes fz)))

  (let [fz (fz/make {:type :even-wrapper :cache-upper-limit 100})]
    (fz/factors fz 45234257))

  (let [fz (fz/make {:type :even-wrapper :cache-upper-limit 100})]
    (fz/factors fz 1223411))



  
  )


