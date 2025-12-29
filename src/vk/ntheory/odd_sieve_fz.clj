(ns vk.ntheory.odd-sieve-fz
  (:require
   [vk.ntheory.odd-table :as tbl]
   [vk.ntheory.factorization :as fz]))

(defn- sieve
  "Sieve of Erathosphene."
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

(defn- table-factors
  "Factorize integer."
  [table ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (tbl/tget-number table n)]
       (cons d (table-factors table (quot n d)))))))

(defn- table-prime?
  "Check does given integer n is a prime."
  [table n]
  (let [n' (tbl/tget-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- table-primes
  "Retrun primes in table."
  [table]
  (->> (map vector (tbl/tkeys table) (tbl/tvals table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defrecord OddSieveFactorization [table]
  fz/Factorization
  (factors [this n]
    (assert (fz/in-domain? this n))
    (table-factors table n))
  (prime? [this n]
    (assert (fz/in-domain? this n))
    (table-prime? table n))
  (primes [_] (table-primes table))
  (upper-limit [_] (:upper-limit table))
  (in-domain? [_ n]
    (tbl/tcontains-key? table n)))


(defmethod fz/make :odd-sieve-fz [{:keys [upper-limit]}]
  (assert (and (pos-int? upper-limit) (odd? upper-limit)))
  (let [table (tbl/make upper-limit)]
    (sieve table)
    (->OddSieveFactorization table)))

(comment
  (let [fz (fz/make {:type :odd-sieve-fz :upper-limit 101})]
    (fz/primes fz))
  )


