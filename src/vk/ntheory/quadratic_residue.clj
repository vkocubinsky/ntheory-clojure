(ns vk.ntheory.quadratic-residue
  (:require [clojure.set :as set]
            [vk.ntheory.basic :as b]
            [vk.ntheory.validation :as v]
            [clojure.test :as t]))

(defn pow-mod
  "Raise `a` to the power of `n` modulo `m`"
  [a n m]
  (v/check-int a)
  (v/check-int-non-neg n)
  (v/check-int-non-neg m)
  (reduce (fn [acc v] (mod (* acc v) m)) 1 (repeat n a)))

(defn bit-count
  [n]
  (count (Integer/toBinaryString n)))

(defn pow-mod-quick
  "Raise `a` to the power of `n` modulo `m`."
  [a n m]
  (v/check-int a)
  (v/check-int-non-neg n)
  (v/check-int-non-neg m)
  (let [c (bit-count n)]
    (reduce
     (fn [acc bit] (let [s (mod (* acc acc) m)]
                     (if bit
                       (mod (* s a) m)
                       s)))
     1
     (for [b1 (range c 0 -1)
           :let [b0 (dec b1)
                 bit (bit-test n b0)]]
       bit))))

(defn R [p] (->> (range 1 p)
                 (map #(* % %))
                 (map #(mod % p))
                 (into (sorted-set))))

(defn N [p]
  (set/difference (into (sorted-set) (range 1 p))
                  (R p)))

(defn euler-criteria
  "Legendre's symbol (n|p) based on Euler criteria."
  [n p]
  (let [l (pow-mod-quick n (/ (dec p) 2) p)]
    (condp = l
      1 1
      (dec p) -1
      0 0)))

(defn gauss-lemma-criteria
  "Legendre's symbol (n|p) based on Gauss lemma.
  Slow."
  [n p]
  (let [p-half (/ p 2)]
    (apply * (for [r (range 1 p-half)
                   :let [v (mod (* n r) p)]
                   :when (> v p-half)]
               -1))))

(defn legendre-minus-one
  "Return (1|p)"
  [p]
  (let [r (mod p 4)]
    (condp = r
      0  0
      1  1
      3 -1)))

(defn legendre-two
  [p]
  (let [r (mod p 8)]
    (condp = r
      0   0 ;;  0 (mod 8)
      1   1 ;;  1 (mod 8)
      7   1 ;; -1 (mod 8)
      3  -1 ;;  3 (mod 8)
      5  -1 ;; -3 (mod 8)
      )))
(t/deftest euler-criteria-test
  (t/testing "Qudratic residue"
    (doseq [n (R 11)]
      (t/is (= 1 (euler-criteria n 11)))))
  (t/testing "Quadratic non residue"
    (doseq [n (N 11)]
      (t/is (= -1 (euler-criteria n 11)))))
  (t/testing "p | n"
    (t/is (zero? (euler-criteria 0 11)))))







