(ns vk.ntheory.divisors-fz
  (:require [vk.ntheory.even-fz :as even-fz]
            [vk.ntheory.factorization :as fz])
  )


(defrecord DivisorsFactorization [even-fz value partitions]
  fz/Factorization
    (factors [this n]
    (assert (fz/in-domain? this n))
    nil)
  (prime? [this n]
    (assert (fz/in-domain? this n))
    nil)
  (primes [this] nil)
  (in-domain? [this n]
    (and (pos? value) (zero? (mod value n))))
  )


(defmethod fz/make :divisors-fz [{:keys [parent-fz value]} ]
  (assert (pos? value))
  (let [partitions (fz/factor-partitions parent-fz value)])
  (-> DivisorsFactorization parent-fz (fz/factor-partitions value))
  )

(comment

  (let [even-fz (fz/make {:type :even-fz :cache-upper-limit 100})
        fz (fz/make {:type :divisors-fz :parent-fz even-fz})]
    (fz/factors fz  98))
  )
