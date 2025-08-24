(ns vk.ntheory.ldt.multi-table-factorization
  "Least divisor table namespace."

  (:require [clojure.pprint :as pp]
            [vk.ntheory.util :as u]
            [vk.ntheory.ldt.table :as t]
            [vk.ntheory.ldt.multi-sieve :as s]
            [vk.ntheory.factorization :as f]))

(defrecord FullMultiTableFactorization [multi-table]
  f/Factorization
  (factors [this n] (s/table-factors multi-table n))
  (factor-counts [this n] (f/factors->counts (f/factors this n)))
  (distinct-factors [this n] (f/factors->distinct (f/factors this n)))
  (prime? [this n] (s/table-prime? multi-table n))
  (primes [this] (s/table-primes multi-table))
  (in-domain? [this n] (t/table-contains? multi-table n)))

(defrecord OddMultiTableFactorization [multi-table upper-limit]
  f/Factorization
  (factors [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (concat (repeat power-of-two 2) (s/table-factors multi-table rest))))
  (factor-counts [this n] (f/factors->counts (f/factors this n)))
  (distinct-factors [this n] (f/factors->distinct (f/factors this n)))
  (prime? [this n] (u/check-pos-int n)
    (cond
      (< upper-limit 2) false
      (= n 2) true
      :else (let [[power-of-two rest] (u/power-of-two-parts n)]
              (and (zero? power-of-two) (s/table-prime? multi-table rest)))))
  (primes [this] (let [seq (s/table-primes multi-table)]
                   (if (>= upper-limit 2)
                     (cons 2 seq)
                     seq)))
  (in-domain? [this n] (u/check-pos-int n)
    (let [[power-of-two rest] (u/power-of-two-parts n)]
      (t/table-contains? multi-table rest))))

(defmethod f/make-factorization :full-multi-table [{:keys [upper-limit]}]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [divisors (t/make-table {:table-type :full
                                :init-type :index
                                :array-type :int
                                :upper-limit upper-limit})
        quotients (t/make-table {:table-type :full
                                 :init-type :ones
                                 :array-type :int
                                 :upper-limit upper-limit})
        powers (t/make-table {:table-type :full
                              :init-type :ones
                              :array-type :short
                              :upper-limit upper-limit})
        multi-table (s/->MultiTable divisors quotients powers)
        ]

    (s/sieve (s/->MultiTable divisors quotients powers) 2)
    (->FullMultiTableFactorization multi-table)))

(defmethod f/make-factorization :odd-multi-table [{:keys [upper-limit]}]
  (when-not (pos-int? upper-limit)
    (throw (ex-info "Upper limit must be positive integer" {:upper-limit upper-limit})))
  (let [odd-upper-limit (if (odd? upper-limit)
                          upper-limit
                          (dec upper-limit))
        divisors (t/make-table {:table-type :odd
                                :init-type :index
                                :array-type :int
                                :upper-limit odd-upper-limit})
        quotients (t/make-table {:table-type :odd
                                 :init-type :ones
                                 :array-type :int
                                 :upper-limit odd-upper-limit})
        powers (t/make-table {:table-type :odd
                              :init-type :ones
                              :array-type :short
                              :upper-limit odd-upper-limit})
        multi-table (s/->MultiTable divisors quotients powers)
        ]
    (s/sieve multi-table 3)
    (->OddMultiTableFactorization multi-table upper-limit)))




