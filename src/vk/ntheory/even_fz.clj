(ns vk.ntheory.even-fz
  "Implementation of Factorization which extract odd part of an integer and delegate
  rest of job to odd factorization."

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

(defmethod fz/make :even-fz [{:keys [odd-fz] :as spec}]
  (->EvenFactorization (cond
                         (satisfies? fz/Factorization odd-fz) odd-fz
                         (contains? odd-fz :type) (fz/make odd-fz)
                         :else (throw (ex-info "Expected either Factorization or a map" spec)))))

(comment

  (let [fz (fz/make {:type :even-fz :odd-fz {:type :odd-fz :odd-lim-fz {:type :odd-sieve-fz :upper-limit 11}}})]
    (println fz)
    (println (take 26 (fz/primes fz)))
    (println (fz/factors fz 404))
    )


  )
