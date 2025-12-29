(ns vk.ntheory.even-fz
  (:require [vk.ntheory.util :as util]
            [vk.ntheory.odd-fz]
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
  (primes [_] (cons 2 (fz/primes odd-fz)))
  (upper-limit [_] nil)
  (in-domain? [_ n]
    (pos? n)))


(defmethod fz/make :even-fz [{:keys [odd-fz]}]
  (->EvenFactorization (if (satisfies? fz/Factorization odd-fz)
                        odd-fz
                        (fz/make odd-fz))))

(comment

  (fz/make {:type :even-fz :odd-fz {:type :odd-fz :odd-fz {:type :odd-sieve-fz :upper-limit 11}}})
  
  )






