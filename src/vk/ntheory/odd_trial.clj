(ns vk.ntheory.odd-trial
  (:require
   [vk.ntheory.odd-sieve :as sieve]
   [vk.ntheory.factorization :as fz]))

(def probable-prime-enabled true)
(def certainty 20)

(defn- trial-only-canidates [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (iterate #(+ % 2) (+ upper-limit 2))))

(defn- prime-candidates [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (concat (fz/primes odd-sieve-fz) (trial-only-canidates odd-sieve-fz))))

(defn trial-factors [odd-sieve-fz n]
  (loop [factors []
         r n
         candidates (take-while #(<= (* % %) n) (prime-candidates odd-sieve-fz))]
    (cond
      (= r 1) factors
      (empty? candidates) (conj factors r)
      (fz/in-domain? odd-sieve-fz r) (concat factors (fz/factors odd-sieve-fz r))
      (= 0 (rem r (first candidates))) (recur (conj factors (first candidates)) (quot r (first candidates)) candidates)
      (and probable-prime-enabled (> r (sieve/upper-limit odd-sieve-fz)) (.isProbablePrime (BigInteger/valueOf r) certainty)) (conj factors r)
      :else (recur factors r (rest candidates)))))

(defn trial-prime? [odd-sieve-fz n]
  (if (fz/in-domain? odd-sieve-fz n)
    (fz/prime? odd-sieve-fz n)
    (every? #(> (rem n %) 0) (take-while #(<= (* % %) n) (prime-candidates odd-sieve-fz)))))

(defn trial-primes [odd-sieve-fz]
  (let [upper-limit (sieve/upper-limit odd-sieve-fz)]
    (concat (fz/primes odd-sieve-fz) (filter #(trial-prime? odd-sieve-fz %) (trial-only-canidates odd-sieve-fz)))))

(defrecord OddTrialDivision [odd-sieve-fz]
  fz/Factorization
  (factors [this n]
    (assert (fz/in-domain? this n))
    (trial-factors odd-sieve-fz n))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (trial-prime? odd-sieve-fz n))
  (primes [this] (trial-primes odd-sieve-fz))
  (in-domain? [this n]
    (and (pos-int? n) (odd? n))))

;;(defn cache-upper-limit
;;  [odd-trial-fz]
;;  (sieve/upper-limit (:odd-sieve-fz odd-trial-fz)))

(defmethod fz/make :odd-trial [{:keys [cache-upper-limit]}]
  (assert (and (pos-int? cache-upper-limit) (odd? cache-upper-limit)))
  (let [odd-sieve-fz (fz/make {:type :odd-sieve :upper-limit cache-upper-limit})]
    (->OddTrialDivision odd-sieve-fz)))

(comment
  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 11})]
    (fz/prime? fz 101))

  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 11})]
    (take 20 (fz/primes fz)))

  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 8191})]
    (fz/factors fz 45234257))

  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 8191})]
    (fz/factors fz 1223411))

  (let [fz (fz/make {:type :odd-trial :cache-upper-limit 8191})]
    (cache-upper-limit fz)))


