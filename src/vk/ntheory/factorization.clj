(ns vk.ntheory.factorization)

(defprotocol Factorization
  "Prime factorization protocol"
  (int->factors [this a] "Ordered factors with their multiplicity.")
  (prime? [this a] "Check does given a is a prime.")
  (primes [this] "Lazy sequence of primes.")
  (in-domain? [this a] "Returns true if value a is supported otherwise false.")
  )
