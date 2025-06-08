;; Least divisor table namespace.
;;
;; Least divisor table is an java array where element at index idx
;; equal to least(prime) divisor of idx. Elements with index 0 and 1
;; doesn't used.

(ns vk.ntheory.least-divisor-table
  (:require [clojure.pprint :as pp]))

(defn- find-prime
  "Find prime in least divisor table.
  
  Parameters: 
  - table: Least divisor table.
  - start: Start index inclusive.
  
  Returns:
  - first prime starts from given index, that is value in array for which index are equals to value.
  "
  [table start]
  (let [end (count table)]
    (loop [idx start]
      (when (< idx end)
        (let [val (aget table idx)]
          (if (= val idx)
            idx
            (recur (inc idx))))))))

(defn- mark-multiple
  "Mark idx in least divisor table as multiple of val if it is not already marked.

  Parameters:
  - table: Least divisor table
  - idx: index
  - val: multiple of

  Return
  "
  [^ints table ^Integer idx ^Integer val]
  (let [curr-val (aget table idx)]
    (when (= curr-val idx)
      (aset table idx val))))

(defn make-table
  "Make least divisor table.
  
  Use slightly modified Eratosthenes algorithm for build least divisor table.

  Parameters:
  - n: upper limit inclusive
  "
  [n]
  (loop [table (int-array (range (inc n)))
         p  (find-prime table 2)]
    (if (or (nil? p) (> (* p p) n))
      table
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (mark-multiple table k p))
        (recur table (find-prime table (inc p)))))))

(defn upper-limit
  "Upper limit for given table"
  [table]
  (let [len (count table)]
    (if (> len 0)
      (dec len)
      0)))

(defn int->factors
  "Factorize integer.

  Returns: ordered factors of given integer
  "
  [^ints table ^Integer n]
  (when-not (and (<= n (upper-limit table)) (> n 0))
    (throw (ex-info "Bad argument" {:upper-limit (upper-limit table) :value n})))
  (lazy-seq
   (when (> n 1)
     (let [d (aget table n)]
       (cons d (int->factors table (quot n d)))))))

(defn prime?
  "Check does given integer is a prime."
  [table n]
  (when-not (and (<= n (upper-limit table)) (> n 0))
    (throw (ex-info "Bad argument" {:upper-limit (upper-limit table) :value n})))
  (let [val (aget table n)]
    (= val n)))

(defn primes
  "Make primes sequecne from least divisor table"
  [table]
  (->> table
       (keep-indexed #(when (= %1 %2) %1))
       (drop-while #(< % 2))))

(defn print-table [table]
  (->> table
       (map-indexed (fn [idx val] {:index idx :value val}))
       (pp/print-table)))

;; (-> 10 make-table print-table)
;; (int->factors (make-table 100) 100)


