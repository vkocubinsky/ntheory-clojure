(ns vk.ntheory.odd-fz
  (:require
   [vk.ntheory.odd-sieve-fz] ;; load make for limited factorizer
   [vk.ntheory.util :as util]
   [vk.ntheory.factorization :as fz]))

(def certainty 20)

(defn- trial-only-canidates [ odd-lim-fz]
  (let [upper-limit (fz/upper-limit odd-lim-fz)
        start (+ upper-limit 2)]
    (iterate #(+ % 2) start)))

(defn- prime-candidates [odd-lim-fz]
  (concat (fz/primes odd-lim-fz) (trial-only-canidates odd-lim-fz)))

(defn- trial-factors [odd-lim-fz n]
  (letfn [(lazy-factors [r
                         candidates
                         r-changed]
            (lazy-seq (cond
                        (= r 1) ()
                        (empty? candidates) (list r)
                        (fz/in-domain? odd-lim-fz r) (fz/factors odd-lim-fz r)
                        (zero? (mod r (first candidates))) (cons (first candidates) (lazy-factors (quot r (first candidates)) candidates true))
                        (and r-changed (util/probable-prime r certainty)) (list r)
                        :else (lazy-factors r (rest candidates) false))))]
    (lazy-factors n (take-while #(<= (* % %) n) (prime-candidates odd-lim-fz)) true)))

(defn- trial-prime? [odd-lim-fz n]
  (if (fz/in-domain? odd-lim-fz n)
    (fz/prime? odd-lim-fz n)
    (util/probable-prime n certainty)))

(defn- trial-primes [odd-lim-fz]
  (concat (fz/primes odd-lim-fz) (filter #(util/probable-prime % certainty) (trial-only-canidates odd-lim-fz))))

(defrecord OddFactorization [odd-lim-fz]
  fz/Factorization
  (factors [this n]
    (assert (fz/in-domain? this n))
    (trial-factors odd-lim-fz n))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (trial-prime? odd-lim-fz n))
  (primes [_] (trial-primes odd-lim-fz))
  (upper-limit [_] nil)
  (in-domain? [_ n]
    (and (pos? n) (odd? n))))

(defmethod fz/make :odd-fz [{:keys [odd-lim-fz] :as spec}]
  (->OddFactorization (cond
                        (satisfies? fz/Factorization odd-lim-fz) odd-lim-fz
                        (contains? odd-lim-fz :type) (fz/make odd-lim-fz)
                        :else (throw (ex-info "Expected either Factorization or a map" spec))
                      )))

(comment

  (let [odd-lim-fz (fz/make {:type :odd-sieve-fz :upper-limit 11})
        fz (fz/make {:type :odd-fz :odd-lim-fz odd-lim-fz})]
    (fz/factors fz 49))

  (let [fz (fz/make {:type :odd-fz :odd-lim-fz {:type :odd-sieve-fz :upper-limit 11}})]
    (fz/factors fz 49))

  )


