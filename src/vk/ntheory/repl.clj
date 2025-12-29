(ns vk.ntheory.repl
  (:require
   [vk.ntheory.even-fz]
   [vk.ntheory.factorization :as fz]
   [vk.ntheory.util :as util]))

(defonce session-atom (atom nil))

(defn- session-init! [cache-upper-limit]
  (reset! session-atom (fz/make
                        {:type :even-fz
                         :odd-fz {:type :odd-fz
                                  :odd-lim-fz {:type :odd-sieve-fz
                                           :upper-limit cache-upper-limit}}})))


(defn get-fz []
  (if-let [fz @session-atom]
    fz
    (session-init! 65535)))



(defn primes [n]
  (take n (fz/primes (get-fz))))

(defn prime? [n]
  (fz/prime? (get-fz) n))

(defn factors [n]
  (fz/factors (get-fz) n))

(defn factor-counts [n]
  (fz/factor-counts (get-fz) n))

(defn distinct-factors [n]
  (fz/distinct-factors (get-fz) n))

(defn next-prime [n]
  (util/next-probable-prime n))

(comment
  (factors  12323425437863876837638763829768932672389678394763))
