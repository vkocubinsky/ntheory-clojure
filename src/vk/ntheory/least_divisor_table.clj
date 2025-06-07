(ns vk.ntheory.least-divisor-table
  (:require [clojure.pprint :as pp]))

(defn- table-find-next-prime
  "Find next prime in least divisor table.
  Parameters: 
    xs    - least divisor table
    start - start index
    end   - end index
  "
  ([xs start] (table-find-next-prime xs start (count xs)))
  ([^ints xs start end]
   (when (< start end)
     (let [e (aget xs start)]
       (if (and (> start 1) (= e start))
         start
         (recur xs (inc start) end))))))

(defn- table-update
  "Update least divisor table to new value if it was not set."
  [^ints xs ^Integer k ^Integer v]
  (let [e (aget xs k)]
    (when-not (< e k)
      (aset xs k v))))

(defn- make-table
  "Build least divisor table.
  Use slightly modified Eratosthenes algorithm for build least divisor table."
  [n]
  (loop [xs (int-array (range (inc n)))
         p (table-find-next-prime xs 2)]
    (if (or (nil? p) (> (* p p) n))
      xs
      (do
        (doseq [k (range (* p p) (inc n) p)]
          (table-update xs k p))
        (recur xs (table-find-next-prime xs (inc p)))))))

(defn- int->factors
  [^ints xs ^Integer n]
  (lazy-seq
   (when (> n 1)
     (let [d (aget xs n)]
       (cons d (int->factors' xs (quot n d)))))))

;; (make-table 10)

(defn table-print [xs]
  (->> xs
       (map-indexed (fn [idx val] {:index idx :value val}))
       (pp/print-table)))

(-> 10
    make-table
    table-print)

(int->factors (make-table 100) 100)
