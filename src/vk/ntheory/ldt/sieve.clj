(ns vk.ntheory.ldt.sieve
  "Functions for table."

  (:require
   [vk.ntheory.util :as u]
   [vk.ntheory.ldt.table :as t]))

(defn sieve
  "Sieve of Erathosphene."
  [table start]
  (loop [p  start]
    (if (> (* p p) (t/table-upper-limit table))
      table
      (let [p' (t/table-get-number table p)
            mark-step (if (= p 2) p (* p 2))
            iter-step (if (= p 2) 1 2)]
        (when (= p' p)
          (doseq [k (range (* p p) (inc (t/table-upper-limit table)) mark-step)]
            (let [k' (t/table-get-number table k)]
              (when (= k' k)
                (t/table-set-number! table k p)))))
        (recur (+ p iter-step))))))

(defn table-factors
  "Factorize integer."
  [table ^Integer n]
  (t/table-check-contains table n)
  (lazy-seq
   (when (> n 1)
     (let [d (t/table-get-number table n)]
       (cons d (table-factors table (quot n d)))))))

(defn table-prime?
  "Check does given integer is `n` prime."
  [table n]
  (t/table-check-contains table n)
  (let [n' (t/table-get-number table n)]
    (and (> n' 1)
         (= n' n))))

(defn table-primes
  [table]
  (->> (map vector (t/table-keys table) (t/table-vals table))
       (filter (fn [[k v]] (= k v)))
       (map first)
       (drop-while #(< % 2))))






