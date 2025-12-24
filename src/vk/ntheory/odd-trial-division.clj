(ns vk.ntheory.odd-trial-division
  (:require
   [vk.ntheory.odd-sieve :as s]
   [vk.ntheory.factorization :as f]))

(defn- prime-candidates [odd-table-factorization]
  (let [{{upper-limit :upper-limit} :table} odd-table-factorization]
    (concat (f/primes odd-table-factorization) (iterate #(+ % 2) (+ upper-limit 2)))))

(defn- first-divisor
  "Try candidate x until x^2 < n, return x if x divides n otherwise nil."
  [n candidates]
  (some #(when (= (rem n %) 0) %) (take-while #(<= (* % %) n) candidates))
  )

(defn trial-factors [odd-table-factorization n]
  (if (f/in-domain? odd-table-factorization n)
    (f/factors odd-table-factorization n)
    (loop [factors []
           r n
           candidates (prime-candidates odd-table-factorization)
           ]
      (if (f/in-domain? odd-table-factorization r)
        (concat factors (f/factors odd-table-factorization r))
        (if-let [p (first-divisor r candidates )]
          (recur (conj factors p) (quot r p) candidates)
          (conj factors r)
        )))))

(defn trial-prime? [odd-table-factorization n]
  (if (f/in-domain? odd-table-factorization n)
    (f/prime? odd-table-factorization n)
    (nil? (first-divisor n (prime-candidates odd-table-factorization)))
    )
  )

(defn trial-primes [odd-table-factorization]
  (let [{{upper-limit :upper-limit} :table} odd-table-factorization]
    (concat (f/primes odd-table-factorization) (filter #(trial-prime? odd-table-factorization %) (iterate #(+ % 2) (+ upper-limit 2)))))
  )

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
    (f/prime? factorization 91))

  (let [factorization (f/make-factorization {:type :odd-trial-division :cache-upper-limit 11})]
    (take 20 (f/primes factorization)))

  (let [factorization (f/make-factorization {:type :odd-trial-division :cache-upper-limit 15})]
    (f/factors factorization 45))

  )


