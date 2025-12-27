(ns vk.ntheory.repl
  (:require
   [vk.ntheory.even-fz]
   [vk.ntheory.factorization :as fz])
  )

;; todo: use atom
(defonce fz (fz/make {:type :even-fz :cache-upper-limit 8192}))

(defn primes [n]
  (take n (fz/primes fz))
  )

(defn prime? [n]
  (fz/prime? fz n)
  )

(defn factors [n]
  (fz/factors fz n)
  )

(defn factor-counts [n]
  (fz/factor-counts fz n)
  )


(defn distinct-factors [n]
  (fz/distinct-factors fz n)
  )



