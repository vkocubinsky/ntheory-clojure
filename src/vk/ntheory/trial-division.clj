(ns vk.ntheory.trial-division
  (:require
   [vk.ntheory.odd-sieve :as s]
   [vk.ntheory.factorization :as f]))


(defn trial-factors [odd-factorization n]
  (f/factors odd-factorization n)
  ) 

(defn trial-prime? [odd-factorization n]
  (f/prime? odd-factorization n)
  )


(defn trial-primes [odd-factorization]
  (f/primes odd-factorization)
  )

(defrecord OddTrialDivision [odd-factorization]
  f/Factorization
  (factors [this n]
    (assert (pos-int? n))
    (trial-factors odd-factorization n))
  (prime? [this n]
    (assert (pos-int? n))
    (trial-prime? odd-factorization n))
  (primes [this] (trial-primes odd-factorization))
  (in-domain? [this n]
    (odd? n)))

(defmethod f/make-factorization :odd-trial-division [{:keys [cache-upper-limit]}]
  (assert (and (pos-int? cache-upper-limit) (odd? cache-upper-limit)))
  (let [odd-factorization (f/make-factorization {:type :odd-table :upper-limit cache-upper-limit})]
    (->OddTrialDivision odd-factorization)))

(comment
  (let [factorization (f/make-factorization {:type :odd-trial-division :cache-upper-limit 31})]
    (f/factors factorization 50))

  )


