(ns vk.ntheory.odd-sieve-fz
  "Implementation of Factorization for odd numbers.
  Implementation based on sieve of Erathosphene's. Keep
  in table least prime divisor of a number.
  "
  (:require
   [vk.ntheory.odd-table :as tbl]
   [vk.ntheory.factorization :as fz]))

(set! *warn-on-reflection* true)

(defn- sieve!
  "Fill table Sieve of Erathosphene."
  [table]
  (loop [p 3]
    (if (> (* p p) (:upper-limit table))
      table
      (let [p' (tbl/tget table p)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (:upper-limit table)) (* p 2))]
            (let [k' (tbl/tget table k)]
              (when (= k' k)
                (tbl/tset! table k p)))))
        (recur (+ p 2))))))

(defn- sieve-factors
  "Factorize integer."
  [table ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (tbl/tget table n)]
       (cons d (sieve-factors table (quot n d)))))))

(defn- sieve-prime?
  "Check does given integer n is a prime."
  [table n]
  (let [d (tbl/tget table n)]
    (and (> d 1)
         (= d n))))

(defn- sieve-primes
  "Return primes in table."
  [table]
  (->> (map vector (tbl/tkeys table) (tbl/tvals table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defn- sieve-next-prime [table n]
  (first (filter #(= % (tbl/tget table %)) (tbl/odd-keys (+ n 2) (:upper-limit table)))))

(defrecord OddSieveFactorization [table]
  fz/Factorization
  (next-prime [this n]
    (assert (fz/in-domain? this n))
    (sieve-next-prime table (int n)))
  (factors [this n]
    (assert (fz/in-domain? this n))
    (sieve-factors table (int n)))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (sieve-prime? table (int n)))
  (primes [_] (sieve-primes table))
  (upper-limit [_] (:upper-limit table))
  (in-domain? [_ n]
    (if (<= n Integer/MAX_VALUE)
      (tbl/tcontains? table (int n))
      false)))

(defmethod fz/make :odd-sieve-fz [{:keys [upper-limit]}]
  {:pre [(pos-int? upper-limit) (odd? upper-limit)]}
  (let [table (tbl/make upper-limit)]
    (sieve! table)
    (->OddSieveFactorization table)))

(comment
  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 105})]
    (fz/next-prime fz 101))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/factors fz 11))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/prime? fz -1))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (take 10 (fz/primes fz)))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/in-domain? fz -1))

  )


