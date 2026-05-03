(ns vk.ntheory.repl
  (:require
   [vk.ntheory.even-fz]
   [vk.ntheory.factorization :as fz]))

(defonce session-atom (atom nil))

(defn session-init! [cache-upper-limit]
  (reset! session-atom (fz/make
                        {:type :even-fz
                         :parent-fz {:type :odd-fz
                                     :pseudo-prime? false
                                     :pseudo-certanity 100
                                     :parent-fz {:type :odd-sieve-fz
                                                 :upper-limit cache-upper-limit}}})))


(defn session-clear! []
  (reset! session-atom nil))

(defn get-fz []
  (if-let [fz @session-atom]
    fz
    (session-init! 65535)))

(defn next-prime [n]
  (fz/next-prime (get-fz) n))

(defn primes [n]
  (take n (fz/primes (get-fz))))

(defn prime? [n]
  (fz/prime? (get-fz) n))

(defn factors [n]
  (fz/factors (get-fz) n))

(defn factor-counts [n]
  (fz/factor-counts (get-fz) n))

(defn factor-distincts [n]
  (fz/factor-distincts (get-fz) n))

(defn factor-paritions [n]
  (fz/factor-partitions (get-fz) n))

(comment
  (factors  8081111111111111))
