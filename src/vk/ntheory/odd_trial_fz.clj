(ns vk.ntheory.odd-trial-fz
  "Trial factorization."
  (:require
   [vk.ntheory.odd-sieve-fz] ;; load make for limited factorizer
   [vk.ntheory.factorization :as fz]))

(defn- trial-only-canidates [odd-lim-fz]
  (let [upper-limit (fz/upper-limit odd-lim-fz)
        start (+ upper-limit 2)]
    (iterate #(+ % 2) start)))

(defn- prime-candidates [odd-lim-fz]
  (concat (fz/primes odd-lim-fz) (trial-only-canidates odd-lim-fz)))

(defn- trial-factors [odd-lim-fz n]
  (letfn [(lazy-factors [r
                         candidates]
            (lazy-seq (cond
                        (= r 1) ()
                        (empty? candidates) (list r)
                        (fz/in-domain? odd-lim-fz r) (fz/factors odd-lim-fz r)
                        (zero? (mod r (first candidates))) (cons (first candidates) (lazy-factors (quot r (first candidates)) candidates))
                        :else (lazy-factors r (rest candidates)))))]
    (lazy-factors n (take-while #(<= (* % %) n) (prime-candidates odd-lim-fz)))))


;; todo: optimize
(defn- trial-only-prime?
  [odd-lim-fz n]
  (assert (<= (fz/upper-limit odd-lim-fz) n))
  (filter #(zero? (mod n %)) (trial-only-canidates odd-lim-fz))
  )

(defn- trial-prime? [odd-lim-fz n]
  (if (fz/in-domain? odd-lim-fz n)
    (fz/prime? odd-lim-fz n)
    (trial-only-prime? odd-lim-fz n)))

(defn- trial-primes [odd-lim-fz]
  (concat (fz/primes odd-lim-fz) (filter #(trial-only-prime? odd-lim-fz %) (trial-only-canidates odd-lim-fz))))

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

(defmethod fz/make :odd-trial-fz [{:keys [odd-lim-fz] :as spec}]
  (->OddFactorization (cond
                        (satisfies? fz/Factorization odd-lim-fz) odd-lim-fz
                        (contains? odd-lim-fz :type) (fz/make odd-lim-fz)
                        :else (throw (ex-info "Expected either Factorization or a map" spec)))))

(comment

  (let [odd-lim-fz (fz/make {:type :odd-sieve-fz :upper-limit 11})
        fz (fz/make {:type :odd-fz :odd-lim-fz odd-lim-fz})]
    (println (take 30 (fz/primes fz)))
    (println (fz/factors fz 49)))

  (let [fz (fz/make {:type :odd-fz :odd-lim-fz {:type :odd-sieve-fz :upper-limit 11}})]
    (fz/factors fz 49)))


