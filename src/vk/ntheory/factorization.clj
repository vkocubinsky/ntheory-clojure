(ns vk.ntheory.factorization)

(defn- factors->distinct [xs]
  (dedupe xs))

(defn- factors->partitions [xs]
  (partition-by identity xs))

(defn- factors->counts [xs]
  (map (fn [ys] [(first ys) (count ys)]) (factors->partitions xs)))

;;(defn counts->factors [xs]
;;  (mapcat (fn [[p k]] (repeat k p)) xs))

;;(defn counts->distinct [xs]
;;  (map first xs))

(defprotocol Factorization
  "Prime factorization protocol"
  (factors [this n] "Returns prime factors of n with multiplicity.")
  (prime? [this n] "Returns true if n is a prime number, otherwise false.")
  (primes [this] "Returns a lazy sequence of primes for this factorizer.")
  (upper-limit [this] "Returns the max number supported, or nil if unlimited.")
  (in-domain? [this n] "Returns true if value a is supported by this factorizer, otherwise false."))

(defn distinct-factors
  "Returns the distinct prime factors of n."
  [fz n]
  (factors->distinct (factors fz n)))

(defn factor-counts
  "Returns a sequence of pairs [p k] where `p` is a prime and `k` is its exponent in factorization of n."
  [fz n]
  (factors->counts (factors fz n)))

(defn factor-partitions
  "Returns a sequence of partitions groups by factor."
  [fz n]
  (factors->partitions (factors fz n)))

(defmulti make
  "Make a factorization from factorization-spec.
  Factorization spec is a map with key :type and value on from
  - :one-fz
  - :odd-sieve-fz
  - :odd-fz
  - :even-fz
  "
  (fn [fz-spec] (:type fz-spec)))

(defrecord OneFactorization []
  Factorization
  (factors [this n]
    (assert (in-domain? this n))
    [])
  (prime? [this n]
    (assert (in-domain? this n))
    false)
  (primes [_] [])
  (upper-limit [_] 1)
  (in-domain? [_ n]
    (= 1 n)))


(defmethod make :one-fz [_]
  (->OneFactorization))

(comment
 (make {:type :one-fz})



 
 map
 
 (+ 1 2)
 (+ 3 4)
 
  )



