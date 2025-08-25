(ns vk.ntheory.ldt.multi-sieve
  "Functions for table."

  (:require
   [vk.ntheory.util :as u]
   [vk.ntheory.ldt.table :as t]))

(defrecord MultiTable [divisors quotients powers])

;; Valery start debug here, powers is incorrect, think about names
(defn sieve
  "Sieve of Erathosphene."
  [multi-table start]
  (let [{:keys [divisors quotients powers]} multi-table]
    (loop [p  start]
      (if (> (* p p) (t/table-upper-limit divisors))
        multi-table
        (let [p' (t/table-get-number divisors p)
              q (t/table-get-number quotients p)
              mark-step (if (= p 2) p (* p 2))
              iter-step (if (= p 2) 1 2)]
          (cond
            ;;prime
            (= p' p) (doseq [k (range (* p p) (inc (t/table-upper-limit divisors)) mark-step)]
                       (let [k' (t/table-get-number divisors k)]
                         (when (= k' k)
                           (t/table-set-number! divisors k p)
                           (t/table-set-number! quotients k (quot k p)))))
            ;;power of prime
            (= p' q) (doseq [k (range p (inc (t/table-upper-limit divisors)) mark-step)]
                       (let [k' (t/table-get-number divisors k)]
                         (when (= k' p')
                           (t/table-set-number! powers k (inc (t/table-get-number powers k)))
                           (t/table-set-number! quotients k (quot (t/table-get-number quotients k) p'))))))

          (recur (+ p iter-step)))))))

(defn table-factors
  "Factorize integer."
  [multi-table n]
  (let [{:keys [divisors quotients powers]} multi-table]
    (t/table-check-contains divisors n)
    (letfn [(factors [n] (lazy-seq
                          (when (> n 1)
                            (let [d (t/table-get-number divisors n)]
                              (cons d (factors (quot n d)))))))]
      (factors n))))

(defn table-prime?
  "Check does given integer is `n` prime."
  [multi-table n]
  (let [{:keys [divisors quotients powers]} multi-table]
    (t/table-check-contains divisors n)
    (let [n' (t/table-get-number divisors n)]
      (and (> n' 1)
           (= n' n)))))

(defn table-primes
  [multi-table]
  (let [{:keys [divisors quotients powers]} multi-table]
    (->> (map vector (t/table-keys divisors) (t/table-vals divisors))
         (filter (fn [[k v]] (= k v)))
         (map first)
         (drop-while #(< % 2)))))

(defn table-contains?
  [multi-table n]
  (let [{:keys [divisors quotients powers]} multi-table]
    (t/table-contains? divisors n)))




