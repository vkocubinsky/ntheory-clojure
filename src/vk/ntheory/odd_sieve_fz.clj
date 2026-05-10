(ns vk.ntheory.odd-sieve-fz
  "Implementation of Factorization for odd numbers.
  Implementation based on sieve of Erathosphene's. Keep
  in table least prime divisor of a number.
  "
  (:import [java.util ArrayList])
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

(defn- table-prime?
  "Check does given integer n is a prime."
  [table n]
  (let [d (tbl/tget table n)]
    (and (> d 1)
         (= d n))))

(defn- make-primes
  "Make primes array for sieve table."
  [table]
  (letfn [(gather-primes [table]
            (let [upper-limit (:upper-limit table)
                  primes (ArrayList.)]
              (loop [c 1]
                (cond
                  (= c upper-limit) primes
                  (table-prime? table c) (do (.add primes c) (recur (+ c 2)))
                  :else (recur (+ c 2))))))]
    (int-array (gather-primes table))))

(defn-  table-factors
  "Factorize integer."
  [table ^Integer n]
  (loop [r n
         factors []]
    (if (= r 1)
      factors
      (let [d (tbl/tget table r)]
        (recur (quot r d) (conj factors d))))))

(defn- sieve-next-prime [table n]
  (first (filter #(= % (tbl/tget table %)) (tbl/odd-keys (+ n 2) (:upper-limit table)))))

(defrecord OddSieveFactorization [table primes]
  fz/Factorization
  (next-prime [this n]
    (assert (fz/in-domain? this n))
    (sieve-next-prime table (int n)))
  (factors [this n]
    (assert (fz/in-domain? this n))
    (table-factors table (int n)))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (table-prime? table (int n)))
  (primes [_] primes)
  (upper-limit [_] (:upper-limit table))
  (in-domain? [_ n]
    (if (<= n Integer/MAX_VALUE)
      (tbl/tcontains? table (int n))
      false)))

(defmethod fz/make :odd-sieve-fz [{:keys [upper-limit]}]
  {:pre [(pos-int? upper-limit) (odd? upper-limit)]}
  (let [table (tbl/make upper-limit)]
    (sieve! table)
    (->OddSieveFactorization table (make-primes table))))

(def foo 1)

(def bar 2)

(comment
  (let [table (tbl/make 35)]
    (sieve! table)
    table
    (make-primes table)
    )
(+ 1 3)
  
(+ 1 2)

  (map inc '(1 2 3)) 

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 105})]
    (fz/next-prime fz 101))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/factors fz 33))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/prime? fz -1))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (take 10 (fz/primes fz)))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/in-domain? fz -1))
  nil)


