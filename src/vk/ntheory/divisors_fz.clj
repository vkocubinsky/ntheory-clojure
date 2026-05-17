(ns vk.ntheory.divisors-fz
  (:require [vk.ntheory.even-fz]
            [vk.ntheory.factorization :as fz]))

(defn divides?
  "Returns true if d divides n, otherwise false."
  [d n]
  (assert (and (pos? d) (pos? n)))
  (zero? (mod n d)))

;; todo: get rid from lazy
(defn divisors-factors [n partitions]
  (letfn [(shift [partitions]
            (let [head (first partitions)
                  head' (rest head)
                  tail (rest partitions)]
              (if (empty? head') tail (cons head' tail))))
          (lazy-factors [r partitions]
            (lazy-seq (cond
                        (= r 1) ()
                        (empty? partitions) (list r)
                        (zero? (mod r (ffirst partitions))) (cons (ffirst partitions) (lazy-factors (quot r (ffirst partitions)) (shift partitions)))
                        :else (lazy-factors r (rest partitions)))))]
    (lazy-factors n partitions)))

(defrecord DivisorsFactorization [parent-fz value partitions]
  fz/Factorization
  (next-prime [this n]
    (assert (fz/in-domain? this n))
    (fz/next-prime parent-fz n))
  (factors [this n]
    (assert (fz/in-domain? this n))
    (if (divides? n value)
      (divisors-factors n partitions)
      (fz/factors parent-fz n)))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (if (divides? n value)
      (some? (some #(= n %) (map first partitions)))
      (fz/prime? parent-fz n)))
  (primes [_]
    (fz/primes parent-fz))
  (upper-limit [_] (fz/primes parent-fz))
  (in-domain? [_ n]
    (fz/in-domain? parent-fz n)))

(defmethod fz/make :divisors-fz [{:keys [parent-fz value] :as spec}]
  (assert (pos? value))
  (let [parent-fz (fz/get-or-make-parent spec)]
    (->DivisorsFactorization parent-fz
                             value
                             (fz/factor-partitions parent-fz value))))

(comment

  (let [even-fz (fz/make {:type :even-fz
                          :parent-fz {:type :odd-fz
                                      :parent-fz {:type :odd-sieve-fz
                                                  :upper-limit 10001}}})
        fz  (fz/make {:type :divisors-fz :value 98
                      :parent-fz even-fz})
        limit 1000000]
    (println "test" limit)
    (time
     (doseq [x (range 1 limit 2)]
       (fz/factors fz x))))

  (let [even-fz (fz/make {:type :even-fz
                          :parent-fz {:type :odd-fz
                                      :parent-fz {:type :odd-sieve-fz
                                                  :upper-limit 10001}}})
        fz  (fz/make {:type :divisors-fz :value 98
                      :parent-fz even-fz})]
    (doseq [x (range 1 10 1)]
      (println (fz/factors fz 98))))

  (let [sieve-fz (fz/make {:type :odd-sieve-fz :upper-limit 1})
        even-fz (fz/make {:type :odd-fz :pseudo-prime? false :pseudo-certainty 100 :parent-fz sieve-fz})
        fz  (fz/make {:type :divisors-fz :value 98
                      :parent-fz even-fz})]
    (println "factors:" (fz/factors fz 77))
    (println "next prime:" (fz/next-prime fz 19))
    (println "primes:" (take 10 (fz/primes fz)))
    (println "next prime of 23:" (fz/next-prime fz 23)))
  nil)
