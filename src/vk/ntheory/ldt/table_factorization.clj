(ns vk.ntheory.ldt.table-factorization
  "Least divisor table namespace."

  (:require [vk.ntheory.util :as u]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.ldt.full-table] ;; load multimethods
            [vk.ntheory.ldt.odd-table] ;; load multimethods
            [vk.ntheory.ldt.sieve :as s]
            [vk.ntheory.factorization :as f]))

(defrecord FullTableFactorization [table]
  f/Factorization
  (factors [this n] (s/table-factors table n))
  (factor-counts [this n] (f/factors->counts (f/factors this n)))
  (distinct-factors [this n] (f/factors->distinct (f/factors this n)))
  (prime? [this n] (s/table-prime? table n))
  (primes [this] (s/table-primes table))
  (in-domain? [this n] (t/table-contains? table n)))

(defrecord OddTableFactorization [table upper-limit]
  f/Factorization
  (factors [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (s/table-factors table rest))))
  (factor-counts [this n] (f/factors->counts (f/factors this n)))
  (distinct-factors [this n] (f/factors->distinct (f/factors this n)))
  (prime? [this n] (u/check-pos-int n)
    (cond
      (< upper-limit 2) false
      (= n 2) true
      :else (let [[power-of-two rest] (u/power-of-two-parts n)]
              (and (zero? power-of-two) (s/table-prime? table rest)))))
  (primes [this] (let [seq (s/table-primes table)]
                   (if (>= upper-limit 2)
                     (cons 2 seq)
                     seq)))
  (in-domain? [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (t/table-contains? table rest))))

(defmethod f/make-factorization :full-table [{:keys [upper-limit]}]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [table (t/make-table {:table-type :full
                             :init-type :index
                             :array-type :int
                             :upper-limit upper-limit})]
    (s/sieve table 2)
    (->FullTableFactorization table)))

(defmethod f/make-factorization :odd-table [{:keys [upper-limit]}]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        table (t/make-table {:table-type :odd
                             :init-type :index
                             :array-type :int
                             :upper-limit odd-upper-limit})]
    (s/sieve table 3)
    (->OddTableFactorization table upper-limit)))



