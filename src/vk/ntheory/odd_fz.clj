(ns vk.ntheory.odd-fz
  (:require
   [vk.ntheory.odd-sieve-fz :as odd-sieve-fz]
   [vk.ntheory.util :as util]
   [vk.ntheory.factorization :as fz]))

(def certainty 100)

(defn- trial-only-canidates [odd-sieve-fz]
  (let [upper-limit (odd-sieve-fz/upper-limit odd-sieve-fz)
        start (+ upper-limit 2)]
    (iterate #(+ % 2) start)))

(defn- prime-candidates [odd-sieve-fz]
  (concat (fz/primes odd-sieve-fz) (trial-only-canidates odd-sieve-fz)))


(defn- trial-factors [odd-sieve-fz n]
  (letfn [(lazy-factors [r
                         candidates
                         r-changed]
            (lazy-seq (cond
                        (= r 1) ()
                        (empty? candidates) (list r)
                        (fz/in-domain? odd-sieve-fz r) (fz/factors odd-sieve-fz r)
                        (= 0 (rem r (first candidates))) (cons (first candidates) (lazy-factors (quot r (first candidates)) candidates true))
                        (and r-changed (util/probable-prime r certainty)) (list r)
                        :else (lazy-factors r (rest candidates) false))))]
    (lazy-factors n (take-while #(<= (* % %) n) (prime-candidates odd-sieve-fz)) true)))

(defn- trial-prime? [odd-sieve-fz n]
  (if (fz/in-domain? odd-sieve-fz n)
    (fz/prime? odd-sieve-fz n)
    (util/probable-prime n certainty)))

(defn- trial-primes [odd-sieve-fz]
  (concat (fz/primes odd-sieve-fz) (filter #(util/probable-prime % certainty) (trial-only-canidates odd-sieve-fz))))

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
    (and (pos? n) (odd? n))))

(defmethod fz/make :odd-fz [{:keys [cache-upper-limit]}]
  (assert (and (pos-int? cache-upper-limit) (odd? cache-upper-limit)))
  (let [odd-sieve-fz (fz/make {:type :odd-sieve-fz :upper-limit cache-upper-limit})]
    (->OddTrialDivision odd-sieve-fz)))

(comment
  (let [fz (fz/make {:type :odd-fz :cache-upper-limit 11})]
    (fz/prime? fz 101))

  (let [fz (fz/make {:type :odd-fz :cache-upper-limit 11})]
    (take 20 (fz/primes fz)))

  (let [fz (fz/make {:type :odd-fz :cache-upper-limit 8191})]
    (fz/factors fz 45234257))

  (let [fz (fz/make {:type :odd-fz :cache-upper-limit 45})]
    (fz/factors fz (* 3 3 5 5 7 11 13)))



  )


