(ns vk.ntheory.odd-trial
  (:require
   [vk.ntheory.odd-sieve :as sieve]
   [vk.ntheory.factorization :as fz]))


(defn- trial-only-canidates [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (iterate #(+ % 2) (+ upper-limit 2)))
  )

(defn- prime-candidates [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (concat (fz/primes odd-sieve-fz) (trial-only-canidates odd-sieve-fz))))


;; (defn trial-factors [odd-sieve-fz n]
;;   (loop [factors []
;;            r n
;;            candidates (prime-candidates odd-sieve-fz)
;;            ]
;;       (if (fz/in-domain? odd-sieve-fz r)
;;         (concat factors (fz/factors odd-sieve-fz r))
;;         (if-let [p (first-divisor r candidates )]
;;           (recur (conj factors p) (quot r p) candidates)
;;           (conj factors r)
;;         ))))

(defn trial-prime? [odd-sieve-fz n]
  (if (fz/in-domain? odd-sieve-fz n)
    (fz/prime? odd-sieve-fz n)
    (every? #(> (rem n %) 0) (take-while #(<= (* % %) n) (prime-candidates odd-sieve-fz)))
    
    )
  )

(defn trial-primes [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (concat (fz/primes odd-sieve-fz) (filter #(trial-prime? odd-sieve-fz %) (trial-only-canidates odd-sieve-fz))))
  )

(defrecord OddTrialDivision [odd-sieve-fz]
  fz/Factorization
  (factors [this n]
    (assert (and (pos-int? n) (odd? n)))
    ;;(trial-factors odd-sieve-fz n)
    nil
    )
  (prime? [this n]
    (assert (and (pos-int? n) (odd? n)))
    (trial-prime? odd-sieve-fz n))
  (primes [this] (trial-primes odd-sieve-fz))
  (in-domain? [this n]
    (and (pos-int? n)(odd? n))))

(defmethod fz/make :odd-trial [{:keys [cache-upper-limit]}]
  (assert (and (pos-int? cache-upper-limit) (odd? cache-upper-limit)))
  (let [odd-sieve-fz (fz/make {:type :odd-sieve :upper-limit cache-upper-limit})]
    (->OddTrialDivision odd-sieve-fz)))

(comment
  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 11})]
    (fz/prime? fz 101))

  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 11})]
    (take 20 (fz/primes fz)))

  ;;(let [fz (fz/make {:type :odd-trial :cache-upper-limit 15})]
  ;;  (fz/factors fz 45))

  )


