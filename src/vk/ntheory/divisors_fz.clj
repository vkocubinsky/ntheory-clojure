(ns vk.ntheory.divisors-fz
  (:require [vk.ntheory.even-fz :as even-fz]
            [vk.ntheory.factorization :as fz]))

(defn divides?
  "Returns true if d divides n, otherwise false."
  [d n]
  (assert (and (pos? d) (pos? n)))
  (zero? (mod n d)))

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
  (primes [this]
    (fz/primes parent-fz))

  (in-domain? [this n]
    (pos? n)))

(defmethod fz/make :divisors-fz [{:keys [parent-fz value]}]
  (assert (pos? value))
  (let [parent-fz (if (satisfies? fz/Factorization parent-fz)
                    parent-fz
                    (fz/make parent-fz))]
    (->DivisorsFactorization parent-fz
                             value
                             (fz/factor-partitions parent-fz value))))

(comment

  (fz/make {:type :even-fz :odd-fz {:type :odd-fz :odd-sieve-fz {:type :odd-sieve-fz :upper-limit 11}}})

  (fz/make {:type :divisors-fz :value 98 :parent-fz {:type :even-fz :odd-fz {:type :odd-fz :odd-sieve-fz {:type :odd-sieve-fz :upper-limit 11}}}})
  )
