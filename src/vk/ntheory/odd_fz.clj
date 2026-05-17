(ns vk.ntheory.odd-fz
  "Trial factorization for odd numbers.
  "
  (:require
   [clojure.math :refer [sqrt]]
   [vk.ntheory.odd-sieve-fz] ;; load make for limited factorizer
   [vk.ntheory.util :as util]
   [vk.ntheory.factorization :as fz]))

(defn trials
  "Trials for find factors."
  [{:keys [parent-fz parent-upper-limit] :as spec} start end]
  (assert (odd? start))
  (let [primes (fz/primes parent-fz)]
    (cond
      (not (<= start end)) ()
      (<= parent-upper-limit start end)  (range start (inc end) 2)
      (<= start parent-upper-limit end)  (concat (take-while #(<= start % parent-upper-limit) primes)
                                                 (range (+ 2 parent-upper-limit) (inc end) 2))
      (<= start end parent-upper-limit)  (take-while #(<= start % end) primes))))

;; eager version
(defn- odd-factors [{:keys [parent-upper-limit parent-fz] :as spec} n]
  (loop [r n
         factors []
         trials (trials spec 3 (sqrt (+ n 0.5)))]
    (let [trial (first trials)]
      (cond
        (nil? trial) (conj factors r)
        (= r 1) factors
        (> (* trial trial) r) (conj factors r)
        (fz/in-domain? parent-fz r) (into factors (fz/factors parent-fz r))
        (zero? (mod r trial)) (recur (quot r trial) (conj factors trial) trials)
        :else (recur r factors (next trials))))))

(defn- odd-prime? [{:keys [pseudo-prime? pseudo-certainty parent-fz parent-upper-limit]} n]
  (letfn [(first-divisor [start n]
            (first (filter #(zero? (mod n %)) (range start (inc n) 2))))]
    (cond
      (fz/in-domain? parent-fz n) (fz/prime? parent-fz n)
      pseudo-prime? (util/probable-prime n pseudo-certainty)
      :else (= n (first-divisor (+ 2 parent-upper-limit) n)))))

(defn- odd-primes [{:keys [parent-upper-limit parent-fz] :as spec}]
  (concat (fz/primes parent-fz) (filter #(odd-prime? spec %) (iterate #(+ 2 %) (+ 2 parent-upper-limit)))))

(defn- odd-next-prime [{:keys [parent-fz pseudo-prime?] :as spec} n]
  (letfn [(next-prime' [n]
            (if pseudo-prime?
              (util/next-probable-prime n)
              (first (filter #(odd-prime? spec %) (iterate #(+ 2 %) (+ 2 n))))))]
    (if (fz/in-domain? parent-fz n)
      (if-let [p (fz/next-prime parent-fz n)]
        p
        (next-prime' n))
      (next-prime' n))))

(defrecord OddFactorization [spec]
  fz/Factorization
  (next-prime [this n]
    (assert (fz/in-domain? this n))
    (odd-next-prime spec n))
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
   (let [parent-fz (fz/make {:type :odd-sieve-fz :upper-limit 100001})
         fz (fz/make {:type :odd-fz :pseudo-prime? false :pseudo-certainty 100 :parent-fz parent-fz})
         limit 1000000]
     (println "test" limit)
     (doseq [x (range 1 limit 2)]
       (fz/factors fz x))))

  (let [parent-fz (fz/make {:type :odd-sieve-fz :upper-limit 1})
        fz (fz/make {:type :odd-fz :pseudo-prime? false :pseudo-certainty 100 :parent-fz parent-fz})]
    (println "factors:" (fz/factors fz 77))
    (println "next prime:" (fz/next-prime fz 19))
    (println "primes:" (take 10 (fz/primes fz)))
    (println "next prime of 23:" (fz/next-prime fz 23))

    (println "factor partitions:" (fz/factor-partitions fz 77))
    (println "factors:" (fz/factor-distincts fz 77))
    (println "factor-counts:" (fz/factor-counts fz 77))
    )

  (let [fz (fz/make {:type :odd-fz :pseudo-prime? true :pseudo-certainty 100 :parent-fz {:type :odd-sieve-fz :upper-limit 11}})]
    (println (fz/factors fz 491111113331)))
  nil)

