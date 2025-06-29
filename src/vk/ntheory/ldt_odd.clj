(ns vk.ntheory.ldt-odd
  "Least divisor table(full) namespace.

   Least divisor table(full) is an java array where element at index idx
   equal to least(prime) divisor of idx. Elements with index 0 is not used,
   element with index 1 contains 1."
  
  (:require [clojure.pprint :as pp]
            [vk.ntheory.factorization :as f]))

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

(defn- make-table
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

(defn- ldt-upper-limit
  "Upper limit for given table."
  [table]
  (let [len (count table)]
    (if (> len 0)
      (dec len)
      0)))

(defn- in-table?
  "Does table support n."
  [table a]
  (and (<= a (ldt-upper-limit table)) (> a 0))
  )

(defn- check-in-table 
  "Check does given number in table.

  Parameters:
  - table: least divisor table.
  - a: number.  
  "
  [table a]
  (when-not (in-table? table a)
    (throw (ex-info "Out of range" {:upper-limit (ldt-upper-limit table) :value a}))))

(defn- ldt-int->factors
  "Factorize integer.

  Returns: ordered factors of given integer
  "
  [^ints table ^Integer a]
  (check-in-table table a)
  (lazy-seq
   (when (> a 1)
     (let [d (aget table a)]
       (cons d (ldt-int->factors table (quot a d)))))))

(defn- ldt-prime?
  "Check does given integer is a prime."
  [table a]
  (check-in-table table a)
  (let [val (aget table a)]
    (and (> val 1)
         (= val a))))

(defn- ldt-primes
  "Make primes sequence from least divisor table"
  [table]
  (->> table
       (keep-indexed #(when (= %1 %2) %1))
       (drop-while #(< % 2))))

(defn- ldt-print-table [table]
  (->> table
       (map-indexed (fn [idx val] {:index idx :value val}))
       (pp/print-table)))


(defrecord LeastDivisorTable [table]
  f/Factorization
  (int->factors [this a] (ldt-int->factors (:table this) a))
  (prime? [this a] (ldt-prime? (:table this) a))
  (primes [this] (ldt-primes (:table this)))
  (in-domain? [this a] (in-table? (:table this) a))
  )

(defn make-factorization [n]
  (LeastDivisorTable. (make-table n))
  )

(defn upper-limit [ldt]
  (ldt-upper-limit (:table ldt))
  )


(defn print-table [ldt]
  (ldt-print-table (:table ldt))
  )


