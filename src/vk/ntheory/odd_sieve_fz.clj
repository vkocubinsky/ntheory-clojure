(ns vk.ntheory.odd-sieve-fz
  "Implementation of Factorization for odd numbers.

  Implementation based on sieve of Erathosphene's. Keep
  in table least prime divisor of a number.
  "
  (:require
   [vk.ntheory.odd-table :as tbl]
   [vk.ntheory.factorization :as fz]))

(defn- sieve!
  "Fill table Sieve of Erathosphene."
  [table]
  (loop [p 3]
    (if (> (* p p) (:upper-limit table))
      table
      (let [p' (tbl/tget-number table p)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (:upper-limit table)) (* p 2))]
            (let [k' (tbl/tget-number table k)]
              (when (= k' k)
                (tbl/tset-number! table k p)))))
        (recur (+ p 2))))))

(defn- sieve-factors
  "Factorize integer."
  [table ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (tbl/tget-number table n)]
       (cons d (sieve-factors table (quot n d)))))))

(defn- sieve-prime?
  "Check does given integer n is a prime."
  [table n]
  (let [n' (tbl/tget-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- sieve-primes
  "Retrun primes in table."
  [table]
  (->> (map vector (tbl/tkeys table) (tbl/tvals table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

;; TODO: optimize this
(defn- sieve-next-prime [table n]
  (first (filter #(> % n) (sieve-primes table))))

;; TODO: finish this
(defn- sieve-next-prime' [table n]
  (let [k n
        v (tbl/tget-number table n)]
    (if (= k v)
      k
      (recur table (inc n))
      )
  ))


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
    (assert (integer? n))
    (tbl/tcontains-key? table (int n))))

(defmethod fz/make :odd-sieve-fz [{:keys [upper-limit]}]
  (assert (and (pos-int? upper-limit) (odd? upper-limit)))
  (let [table (tbl/make upper-limit)]
    (sieve! table)
    (->OddSieveFactorization table)))

(comment
  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/next-prime fz 101N))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/factors fz 15N))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/prime? fz 1))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (take 10 (fz/primes fz)))

  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/in-domain? fz 101N))

  )


