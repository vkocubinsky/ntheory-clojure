(ns vk.ntheory.quadratic-residue
  "Quadratic Residue."
  (:require [clojure.set :as set]
            [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            [vk.ntheory.validation :as v]
            [clojure.spec.alpha :as s]
            [clojure.test :as t]))

(defn R
  "Returns set of quadratic residue to prime modulo `p`."
  [p] (->> (range 1 (/ p 2))
           (map #(b/m* p % %))
           (into (sorted-set))))

(defn N
  "Returns of set of quadratic non residue to prime modulo `p`."
  [p]
  (set/difference (into (sorted-set) (range 1 p))
                  (R p)))

(defn legendre-by-euler-criteria
  "Legendre's symbol (n|p) based on Euler criteria.
  Here `p` is a prime."
  [n p]
  (v/check-spec (s/and odd? p/prime?) p)
  (let [l (b/m** p n (/ (dec p) 2))]
    (condp = l
      1 1
      (dec p) -1
      0 0)))

(defn legendre-by-gauss-lemma
  "Legendre's symbol (n|p) based on Gauss lemma.
  Here `p` is a prime, `p` is not divides `n` .
  Slow compare to Euler Criteria."
  [n p]
  (v/check-spec (s/and odd? p/prime?) p)
  (if (p/divides? p n)
    0
    (let [p-half (/ p 2)]
      (apply * (for [r (range 1 p-half)
                     :let [v (b/m* p n r)]
                     :when (> v p-half)]
                 -1)))))

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










