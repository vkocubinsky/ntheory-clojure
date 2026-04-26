(ns vk.ntheory.odd-fz
  "Trial factorization."
  (:require
   [vk.ntheory.odd-sieve-fz] ;; load make for limited factorizer
   [vk.ntheory.util :as util]
   [vk.ntheory.factorization :as fz]))

(defn- odd-factors [{:keys [pseudo-prime? pseudo-certainty parent-upper-limit parent-fz]} n]
  (letfn [(prime-candidates []
            (concat (fz/primes parent-fz) (iterate #(+ % 2) (+ 2 parent-upper-limit))))
          (lazy-factors [r candidates r-changed?]
            (lazy-seq (cond
                        (= r 1) ()
                        (empty? candidates) (list r)
                        (fz/in-domain? parent-fz r) (fz/factors parent-fz r)
                        (zero? (mod r (first candidates))) (cons
                                                            (first candidates)
                                                            (lazy-factors (quot r (first candidates)) candidates true))
                        (and pseudo-prime? r-changed? (util/probable-prime r pseudo-certainty)) (list r)
                        :else (lazy-factors r (rest candidates) false))))]
    (lazy-factors n (take-while #(<= (* % %) n) (prime-candidates)) true)))

(defn- first-divisor
  [start n]
  (assert (<= start n))
  (first (filter #(zero? (mod n %)) (iterate #(+ % 2) start))))

(defn- odd-prime? [{:keys [pseudo-prime? pseudo-certainty parent-fz parent-upper-limit]} n]
  (cond
    (fz/in-domain? parent-fz n) (fz/prime? parent-fz n)
    pseudo-prime? (util/probable-prime n pseudo-certainty)
    :else (= n (first-divisor (+ parent-upper-limit 2) n))))

(defn- odd-primes [{:keys [parent-upper-limit parent-fz] :as spec}]
  (concat (fz/primes parent-fz) (filter #(odd-prime? spec %) (iterate #(+ % 2) (+ 2 parent-upper-limit)))))

(defrecord OddFactorization [spec]
  fz/Factorization
  (next-prime [this n] 1)
  (factors [this n]
    (assert (fz/in-domain? this n))
    (odd-factors spec n))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (odd-prime? spec n))
  (primes [_] (odd-primes spec))
  (upper-limit [_] nil)
  (in-domain? [_ n]
    (and (pos? n) (odd? n))))

(defn- normalize-spec [spec parent-fz]
  (let [parent-upper-limit (fz/upper-limit parent-fz)
        pseudo-prime? (or (spec :pseudo-prime?) true)
        pseudo-certainty (or (spec :pseudo-certainty) 100)]
    (assert (fz/upper-limit parent-fz))
    (assoc spec
           :parent-fz parent-fz
           :parent-upper-limit parent-upper-limit
           :pseudo-prime? pseudo-prime?
           :pseudo-certainty pseudo-certainty)))

(defmethod fz/make :odd-fz [spec]
  (let [parent-fz (fz/get-or-make-parent spec)
        spec (normalize-spec spec parent-fz)]
    (assert (fz/upper-limit parent-fz))
    (->OddFactorization spec)))

(comment

  (let [parent-fz (fz/make {:type :odd-sieve-fz :upper-limit 11})
        fz (fz/make {:type :odd-fz :pseudo-prime? true :pseudo-certainty 100 :parent-fz parent-fz})]
    (println (take 20 (fz/primes fz)))
    (println (fz/factors fz 19981))
    )

  (let [fz (fz/make {:type :odd-fz :pseudo-prime? true :pseudo-certainty 100 :parent-fz {:type :odd-sieve-fz :upper-limit 11}})]
    (println (fz/factors fz 491111113331)))


  )

