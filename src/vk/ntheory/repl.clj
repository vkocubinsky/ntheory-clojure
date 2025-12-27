(ns vk.ntheory.repl
  (:require
   [vk.ntheory.even-fz]
   [vk.ntheory.factorization :as fz]
   [vk.ntheory.util :as util]))

(defonce session-atom (atom nil))

(defn- session-new! [cache-upper-limit]
  (reset! session-atom (fz/make {:type :even-fz :cache-upper-limit cache-upper-limit})))

(defn session-init! [cache-upper-limit]
  (do
    (session-new! cache-upper-limit)
    nil))



(defn get-fz []
  (if-let [fz @session-atom]
    fz
    (session-new! 65535)))

(defn session-info []
  {:cache-upper-limit (-> (get-fz) :odd-fz :odd-sieve-fz :table :upper-limit)})

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
 (factors  12323425437863876837638763829768932672389678394763)
  )
