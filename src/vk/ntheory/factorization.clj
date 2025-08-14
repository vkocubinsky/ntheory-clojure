(ns vk.ntheory.factorization)

(defprotocol Factorization
  "Prime factorization protocol"
  (factors [this n] "Returns prime factors of `n`(with multiplicity).")
  (factor-counts [this n] "Returns a sequence of pairs [p k] where `p` is a prime and `k` is its exponent in factorization of `n`.")
  (distinct-factors [this n] "Returns the distinct prime factors of `n`.")
  (prime? [this n] "Returns true if `n` is a prime number, otherwise false.")
  (primes [this] "Returns a lazy sequence of primes for this factorizer.")
  (in-domain? [this n] "Returns true if value a is supported by this factorizer, otherwise false."))

(defn factors->distinct [xs]
  (dedupe xs))

(defn factors->partitions [xs]
  (partition-by identity xs))

(defn factors->counts [xs]
  (map (fn [ys] [(first xs) (count ys)]) (factors->partitions xs))
  )


