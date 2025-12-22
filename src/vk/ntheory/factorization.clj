(ns vk.ntheory.factorization)

(defn- factors->distinct [xs]
  (dedupe xs))

(defn- factors->partitions [xs]
  (partition-by identity xs))

(defn factors->counts [xs]
  (map (fn [ys] [(first ys) (count ys)]) (factors->partitions xs)))

;;(defn counts->factors [xs]
;;  (mapcat (fn [[p k]] (repeat k p)) xs))

;;(defn counts->distinct [xs]
;;  (map first xs))


(defprotocol Factorization
  "Prime factorization protocol"
  (factors [this n] "Returns prime factors of n with multiplicity.")
  (prime? [this n] "Returns true if `n` is a prime number, otherwise false.")
  (primes [this] "Returns a lazy sequence of primes for this factorizer.")
  (in-domain? [this n] "Returns true if value a is supported by this factorizer, otherwise false."))


(defn distinct-factors
  "Returns the distinct prime factors of n."
  [factorization n]
  (factors->distinct (factors factorization n)))


(defn factor-counts
  "Returns a sequence of pairs [p k] where `p` is a prime and `k` is its exponent in factorization of n."
  [factorization n]
  (factors->counts (factors factorization n))
  )



(defmulti make-factorization
  "Make a factorization from factorization-spec."
  (fn [factorization-spec] (:type factorization-spec)))



