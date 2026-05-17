(ns vk.ntheory.even-fz
  "Implementation of Factorization which extract odd part of an integer and delegate
  rest of job to odd factorization."

  (:require [vk.ntheory.util :as util]
            [vk.ntheory.odd-fz]
            [vk.ntheory.factorization :as fz]))

(defrecord EvenFactorization [parent-fz]
  fz/Factorization
  (next-prime [this n]
    {:pre [(fz/in-domain? this n)]}
    (if (= n 1)
      2
      (let [[_ rest] (util/power-of-two-parts n)]
        (fz/next-prime parent-fz rest))))
  (factors [this n]
    {:pre [(fz/in-domain? this n)]}
    (let [[power-of-two rest] (util/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (fz/factors parent-fz rest))))
  (prime? [this n]
    {:pre [(fz/in-domain? this n)]}
    (cond
      (= n 2) true
      :else (let [[power-of-two rest] (util/power-of-two-parts n)]
              (and (zero? power-of-two) (fz/prime? parent-fz rest)))))
  (primes [_] (cons 2 (fz/primes parent-fz)))
  (upper-limit [_] nil)
  (in-domain? [_ n]
    (pos? n)))

(defmethod fz/make :even-fz [spec]
  (let [parent-fz (fz/get-or-make-parent spec)]
    (->EvenFactorization parent-fz)))

(comment

  (let [fz (fz/make {:type :even-fz
                     :parent-fz {:type :odd-fz
                                 :parent-fz {:type :odd-sieve-fz
                                             :upper-limit 11}}})]
    (time
     (doseq [x (range 1 100 1)]
       (fz/factors fz x))))

  (let [fz (fz/make {:type :even-fz
                     :parent-fz {:type :odd-fz
                                 :parent-fz {:type :odd-sieve-fz
                                             :upper-limit 11}}})]
    (time
     (doseq [x (range 1 100 1)]
       (println (fz/factors fz x)))))

  (let [fz (fz/make {:type :even-fz :parent-fz {:type :odd-fz :parent-fz {:type :odd-sieve-fz :upper-limit 11}}})]
    (println fz)
    (println "prime?" (fz/prime? fz 13N))
    (println "next-prime" (fz/next-prime fz 135N))
    (println "factors" (fz/factors fz 52))
    (println "primes" (take 26 (fz/primes fz)))))
