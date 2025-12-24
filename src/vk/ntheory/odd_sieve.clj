(ns vk.ntheory.odd-sieve
  (:require
   [vk.ntheory.odd-table :as t]
   [vk.ntheory.factorization :as f]))

(defn- sieve
  "Sieve of Erathosphene."
  [table]
  (loop [p 3]
    (if (> (* p p) (:upper-limit table))
      table
      (let [p' (t/table-get-number table p)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (:upper-limit table)) (* p 2))]
            (let [k' (t/table-get-number table k)]
              (when (= k' k)
                (t/table-set-number! table k p)))))
        (recur (+ p 2))))))

(defn- table-factors
  "Factorize integer."
  [table ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (t/table-get-number table n)]
       (cons d (table-factors table (quot n d)))))))

(defn- table-prime?
  "Check does given integer n is a prime."
  [table n]
  (let [n' (t/table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn- table-primes
  "Retrun primes in table."
  [table]
  (->> (map vector (t/table-keys table) (t/table-vals table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))

(defrecord OddTableFactorization [table]
  f/Factorization
  (factors [_ n]
    (assert (t/table-contains-key? table n))
    (table-factors table n))
  (prime? [_ n]
    (assert (t/table-contains-key? table n))
    (table-prime? table n))
  (primes [_] (table-primes table))
  (in-domain? [_ n]
    (t/table-contains-key? table n)))

(defmethod f/make-factorization :odd-table [{:keys [upper-limit]}]
  (assert (and (pos-int? upper-limit) (odd? upper-limit)))
  (let [table (t/make-table upper-limit)]
    (sieve table)
    (->OddTableFactorization table)))

(comment
  (let [factorization (f/make-factorization {:type :odd-table :upper-limit 101})]
    (f/factors factorization 15))

  )


