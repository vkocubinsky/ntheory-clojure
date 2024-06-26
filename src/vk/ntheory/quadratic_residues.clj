(ns vk.ntheory.quadratic-residues
  "Quadratic Residue."
  (:require [clojure.set :as set]
            [vk.ntheory.basic :as b]
            [vk.ntheory.primes :as p]
            ))

(defn R
  "Returns set of quadratic residue to prime modulo `p`."
  [p] (->> (range 1 (/ p 2))
           (map #(b/mod-mul p % %))
           (into (sorted-set))))

(defn N
  "Returns of set of quadratic non residue to prime modulo `p`."
  [p]
  (set/difference (into (sorted-set) (range 1 p))
                  (R p)))

(defn legendre-by-euler-criteria
  "Legendre's symbol (n|p) based on Euler criteria."
  [n p]
  (p/check-odd-prime p)
  (let [l (b/mod-pow p n (/ (dec p) 2))]
    (condp = l
      1 1
      (dec p) -1
      0 0)))

(defn legendre-by-gauss-lemma
  "Legendre's symbol (n|p) based on Gauss lemma.
  Slow compare to Euler Criteria."
  [n p]
  (p/check-odd-prime p)
  (if (b/divides? p n)
    0
    (let [p-half (/ p 2)]
      (apply * (for [r (range 1 p-half)
                     :let [v (b/mod-mul p n r)]
                     :when (> v p-half)]
                 -1)))))

(defn legendre-minus-one
  "Return (-1|p)."
  [p]
  (p/check-odd-prime p)
  (let [r (mod p 4)]
    (condp = r
      0  0
      1  1
      3 -1)))

(defn legendre-two
  "Return (2|p)."
  [p]
  (p/check-odd-prime p)
  (let [r (mod p 8)]
    (condp = r
      0   0 ;;  0 (mod 8)
      1   1 ;;  1 (mod 8)
      7   1 ;; -1 (mod 8)
      3  -1 ;;  3 (mod 8)
      5  -1 ;; -3 (mod 8)
      )))

