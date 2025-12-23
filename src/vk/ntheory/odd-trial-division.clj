(ns vk.ntheory.odd-trial-division
  (:require
   [vk.ntheory.odd-sieve :as s]
   [vk.ntheory.factorization :as f]))

(defn- prime-candidates [odd-table-factorization]
  (let [{{upper-limit :upper-limit} :table} odd-table-factorization]
    (concat (f/primes odd-table-factorization) (iterate #(+ % 2) (+ upper-limit 2)))))

(defn trial-factors [odd-table-factorization n]
  (f/factors odd-table-factorization n))

(defn trial-prime? [odd-table-factorization n]
  (if (f/in-domain? odd-table-factorization n)
    (f/prime? odd-table-factorization n)
    ;; Valery start here
    )
  )

(defn trial-primes [odd-table-factorization]
  (f/primes odd-table-factorization))

(defrecord OddTrialDivision [odd-table-factorization]
  f/Factorization
  (factors [this n]
    (assert (and (pos-int? n) (odd? n)))
    (trial-factors odd-table-factorization n))
  (prime? [this n]
    (assert (and (pos-int? n) (odd? n)))
    (trial-prime? odd-table-factorization n))
  (primes [this] (trial-primes odd-table-factorization))
  (in-domain? [this n]
    (and (pos-int? n)(odd? n))))

(defmethod f/make-factorization :odd-trial-division [{:keys [cache-upper-limit]}]
  (assert (and (pos-int? cache-upper-limit) (odd? cache-upper-limit)))
  (let [odd-factorization (f/make-factorization {:type :odd-table :upper-limit cache-upper-limit})]
    (->OddTrialDivision odd-factorization)))

(comment
  (let [factorization (f/make-factorization {:type :odd-trial-division :cache-upper-limit 11})]
    (f/prime? factorization 13))

  (let [factorization (f/make-factorization {:type :odd-table :upper-limit 11})]
    (take 10 (prime-candidates factorization))))


