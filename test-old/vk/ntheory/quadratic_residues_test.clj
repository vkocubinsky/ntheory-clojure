(ns vk.ntheory.quadratic-residues-test
  (:require [clojure.test :refer [deftest is are testing]]
            [vk.ntheory.quadratic-residues :as qr]))




(deftest R-N-test
  (is (= #{1 3 4 5 9} (qr/R 11)))
  (is (= #{2 6 7 8 10} (qr/N 11))))

(deftest legendre-euler-test
  (testing "p is not an odd prime"
    (is (thrown? Exception (qr/legendre-by-euler-criteria 15 2))))
  (testing "p is not a prime"
    (is (thrown? Exception (qr/legendre-by-euler-criteria 15 4))))
  (testing "Qudratic residue"
    (doseq [n [1 3 4 5 9]]
      (is (= 1 (qr/legendre-by-euler-criteria n 11)))))
  (testing "Quadratic non residue"
    (doseq [n [2 6 7 8 10]]
      (is (= -1 (qr/legendre-by-euler-criteria n 11)))))
  (testing "p | n"
    (is (zero? (qr/legendre-by-euler-criteria 0 11)))))

(deftest legendre-gauss-test
  (testing "p is not an odd prime"
    (is (thrown? Exception (qr/legendre-by-gauss-lemma 15 2))))
  (testing "p is not a prime"
    (is (thrown? Exception (qr/legendre-by-gauss-lemma 15 4))))
  (testing "Qudratic residue"
    (doseq [n [1 3 4 5 9]]
      (is (= 1 (qr/legendre-by-gauss-lemma n 11)))))
  (testing "Quadratic non residue"
    (doseq [n [2 6 7 8 10]]
      (is (= -1 (qr/legendre-by-gauss-lemma n 11)))))
  (testing "p | n"
    (is (zero? (qr/legendre-by-gauss-lemma 0 11)))))

(deftest legendre-minus-one-test
  (testing "p is not an odd prime"
    (is (thrown? Exception (qr/legendre-minus-one 2))))
  (testing "p is not a prime"
    (is (thrown? Exception (qr/legendre-minus-one 4))))
  (testing "Compare with euler criteria"
    (are [p] (= (qr/legendre-by-euler-criteria -1 p) (qr/legendre-minus-one p))
      3 5 7 11 13 17 19 23 29)))

(deftest legendre-two
  (testing "p is not an odd prime"
    (is (thrown? Exception (qr/legendre-two 2))))
  (testing "p is not a prime"
    (is (thrown? Exception (qr/legendre-two 4))))
  (testing "Compare with euler criteria"
    (are [p] (= (qr/legendre-by-euler-criteria 2 p) (qr/legendre-two p))
      3 5 7 11 13 17 19 23 29)))
