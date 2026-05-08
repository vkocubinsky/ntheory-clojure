(ns vk.ntheory.repl
  (:require
   [clojure.math :refer :all]
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
    (session-init! 256001)))

(defn next-prime [n]
  (fz/next-prime (get-fz) n))
(alter-meta! #'next-prime assoc :doc (:doc (meta #'fz/next-prime)))

(defn primes [n]
  (take n (fz/primes (get-fz))))
(alter-meta! #'primes assoc :doc (:doc (meta #'fz/primes)))

(defn prime? [n]
  (fz/prime? (get-fz) n))
(alter-meta! #'prime? assoc :doc (:doc (meta #'fz/prime?)))

(defn factors [n]
  (fz/factors (get-fz) n))
(alter-meta! #'factors assoc :doc (:doc (meta #'fz/factors)))

(defn factor-counts [n]
  (fz/factor-counts (get-fz) n))
(alter-meta! #'factor-counts assoc :doc (:doc (meta #'fz/factor-counts)))

(defn factor-distincts [n]
  (fz/factor-distincts (get-fz) n))
(alter-meta! #'factor-distincts assoc :doc (:doc (meta #'fz/factor-distincts)))

(defn factor-partitions [n]
  (fz/factor-partitions (get-fz) n))
(alter-meta! #'factor-partitions assoc :doc (:doc (meta #'fz/factor-partitions)))








