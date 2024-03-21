(ns vk.ntheory.quadratic-residue
  (:require [clojure.set :as set]
            [vk.ntheory.basic :as b]
            [clojure.test :as t]))

(defn R [p] (->> (range 1 p)
                 (map #(* % %))
                 (map #(mod % p))
                 (into (sorted-set))))

(defn N [p]
  (set/difference (into (sorted-set) (range 1 p))
                  (R p)))

(defn euler-criteria
  "Legendre's symbol (n | p) based on Euler criteria."
  [n p]
  (let [l (mod (b/pow n (/ (dec p) 2)) p)]
    (condp = l
      1 1
      (dec p) -1
      0 0
      )))

(defn legendre-minus-one
  "Return (1|p)"
  [p]
  (let [r (mod p 4)]
    (condp = r
      0  0 
      1  1
      3 -1
    )))
  
(defn legendre-two
  [p]
  (let [r (mod p 8)]
    (condp = r
      0   0 ;;  0 (mod 8)
      1   1 ;;  1 (mod 8)
      7   1 ;; -1 (mod 8)
      3  -1 ;;  3 (mod 8)
      5  -1 ;; -3 (mod 8)
      ))
  )




(t/deftest euler-criteria-test
  (t/testing "Qudratic residue"
    (doseq [n (R 11)]
      (t/is (= 1 (euler-criteria n 11)))))
  (t/testing "Quadratic non residue"
    (doseq [n (N 11)]
      (t/is (= -1 (euler-criteria n 11)))))
  (t/testing "p | n"
      (t/is (zero? (euler-criteria 0 11)))
    ))

(defn report [p]
  (let [r (R p)
        n (N p)]
    (println "quadratic residue size: " (count r))
    (println "quadratic non residue size: " (count n))
    (println "quadratic residue: "  r)
    (println "quadratic non residue : " n)))





