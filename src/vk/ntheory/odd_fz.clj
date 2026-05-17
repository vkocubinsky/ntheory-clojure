(ns vk.ntheory.odd-fz
  "Trial factorization for odd numbers.
  "
  (:require
   [vk.ntheory.odd-sieve-fz] ;; load make for limited factorizer
   [vk.ntheory.util :as util]
   [vk.ntheory.factorization :as fz]))

;; todo: assume that primes is an array
(defn- next-trial [{:keys [parent-fz parent-upper-limit] :as spec}
                   idx
                   val]
  (let [primes (fz/primes parent-fz)
        next-idx (inc idx)]
    (if (< next-idx (count primes))
      (aget primes next-idx)
      (+ val 2))))

;; eager version
(defn- odd-factors [{:keys [pseudo-prime? pseudo-certainty parent-upper-limit parent-fz] :as spec} n]
  (loop [r n
         factors []
         r-changed? true
         trial-idx 0
         trial-val 3]
    (cond
      (= r 1) []
      (fz/in-domain? parent-fz r) (into factors (fz/factors parent-fz r))
      (zero? (mod r trial-val)) (recur (quot r trial-val) (conj factors trial-val) true trial-idx trial-val)
      (and pseudo-prime? r-changed? (util/probable-prime r pseudo-certainty)) (conj factors r)
      :else (recur r factors false (inc trial-idx) (next-trial spec trial-idx trial-val)))))

(defn- first-divisor
  "First divisor of `n` starts with `start` inclusive"
  [start n]
  {:pre [(> start 1) (<= start n)]}
  (first (filter #(zero? (mod n %)) (iterate #(+ % 2) start))))

(defn- odd-prime? [{:keys [pseudo-prime? pseudo-certainty parent-fz parent-upper-limit]} n]
  (cond
    (fz/in-domain? parent-fz n) (fz/prime? parent-fz n)
    pseudo-prime? (util/probable-prime n pseudo-certainty)
    :else (= n (first-divisor (+ parent-upper-limit 2) n))))

(defn- odd-primes [{:keys [parent-upper-limit parent-fz] :as spec}]
  (concat (fz/primes parent-fz) (filter #(odd-prime? spec %) (iterate #(+ % 2) (+ 2 parent-upper-limit)))))

(defn- next-prime-no-parent
  "Next prime after `n`"
  [{:keys [pseudo-prime?] :as spec} n]
  (if pseudo-prime?
    (util/next-probable-prime n)
    (first (filter #(odd-prime? spec %) (iterate #(+ % 2) (+ 2 n))))))

(defn- odd-next-prime [{:keys [parent-fz] :as spec} n]
  (if (fz/in-domain? parent-fz n)
    (if-let [p (fz/next-prime parent-fz n)]
      p
      (next-prime-no-parent spec n))
    (next-prime-no-parent spec n)))

(defrecord OddFactorization [spec]
  fz/Factorization
  (next-prime [this n]
    {:pre (fz/in-domain? this n)}
    (odd-next-prime spec n))
  (factors [this n]
    {:pre (fz/in-domain? this n)}
    (odd-factors spec n))
  (prime? [this n]
    {:pre (fz/in-domain? this n)}
    (odd-prime? spec n))
  (primes [_] (odd-primes spec))
  (upper-limit [_] nil)
  (in-domain? [_ n]
    (and (pos? n) (odd? n))))

(defn- normalize-spec [{:keys [pseudo-prime?
                               pseudo-certanity]
                        :or {pseudo-prime? true
                             pseudo-certanity 100} :as spec}
                       parent-fz]
  (assert (fz/upper-limit parent-fz))
  (assoc spec
         :parent-fz parent-fz
         :parent-upper-limit (fz/upper-limit parent-fz)
         :pseudo-prime? pseudo-prime?
         :pseudo-certainty pseudo-certanity))

(defmethod fz/make :odd-fz [spec]
  (let [parent-fz (fz/get-or-make-parent spec)
        spec (normalize-spec spec parent-fz)]
    (assert (fz/upper-limit parent-fz))
    (->OddFactorization spec)))

(comment

  (time
   (let [parent-fz (fz/make {:type :odd-sieve-fz :upper-limit 1})
         fz (fz/make {:type :odd-fz :pseudo-prime? true :pseudo-certainty 100 :parent-fz parent-fz})]
     (doseq [x (range 1 100000 2)]
       (fz/factors fz x))))

  (let [parent-fz (fz/make {:type :odd-sieve-fz :upper-limit 11})
        fz (fz/make {:type :odd-fz :pseudo-prime? false :pseudo-certainty 100 :parent-fz parent-fz})]
    (fz/factors fz 77))

  (let [fz (fz/make {:type :odd-fz :pseudo-prime? true :pseudo-certainty 100 :parent-fz {:type :odd-sieve-fz :upper-limit 11}})]
    (println (fz/factors fz 491111113331)))
  nil)

