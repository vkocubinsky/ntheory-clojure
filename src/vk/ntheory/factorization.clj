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
  (next-prime [this n] "Returns next prime more than given n or nil if no next prime for finite factorizer.")
  (factors [this n] "Returns prime factors of n with multiplicity.")
  (prime? [this n] "Returns true if n is a prime number, otherwise false.")
  (primes [this] "Returns sequence of primes for this factorizer.")
  (upper-limit [this] "Returns the max number supported, or nil if unlimited.")
  (in-domain? [this n] "Returns true if value a is supported by this factorizer, otherwise false."))

(defn factor-distincts
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
  Factorization spec is a map
  1. with key :type and value on from
    - :odd-sieve-fz
    - :odd-fz
    - :even-fz
    - :divisors-fz
  2. optional key :parent-fz with value which either Factorization or spec
  "
  (fn [fz-spec] (:type fz-spec)))

(defn get-or-make-parent
  "Extract or make parent factorization from spec."
  [spec]
  (let [parent-spec (spec :parent-fz)]
    (cond
      (satisfies? Factorization parent-spec) parent-spec
      (and (map? parent-spec) (contains? parent-spec :type)) (make parent-spec)
      :else (throw (ex-info "Expected either Factorization or a map contains :type key" spec)))))


(comment
  (make {:type :one-fz})

  (get-or-make-parent {:type :one-fz :parent-fz {:type :one-fz}})

  )

