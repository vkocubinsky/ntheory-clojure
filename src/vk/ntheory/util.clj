(ns vk.ntheory.util
  (:import [clojure.lang BigInt])
  )

(defmulti power-of-two-parts
  "Returns vector [k r] where k is p-adic order of 2 and r is a rest
  such that 2^k * r = n, r is odd."
  class)

(defmethod power-of-two-parts Number
  [n]
  (assert (pos-int? n))
  (let [k (Long/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))

(defmethod power-of-two-parts BigInt
  [n]
  (let [n' (biginteger n)
        k (.getLowestSetBit n')
        r (.shiftRight n' k)]
    [k  (bigint r)]))

(defn probable-prime
  [n certainty]
  (BigInteger/.isProbablePrime (biginteger n) certainty))

(defn next-probable-prime
  [n]
  (BigInteger/.nextProbablePrime (biginteger n)))


(defn mod-inverse [k m]
  (let [k' (biginteger k)
        m' (biginteger m)]
    (BigInteger/.modInverse k' m')))

(defn mod-pow [k exp m]
  (let [k' (biginteger k)
        m' (biginteger m)
        exp' (biginteger exp)
        ]
    (BigInteger/.modPow k' exp' m')))

(defn gcd [a b]
  (let [a' (biginteger a)
        b' (biginteger b)]
    (BigInteger/.gcd a' b')
    )
  )



(comment
  (gcd 0 0)

  (power-of-two-parts 8123342352453463563334455555556))







