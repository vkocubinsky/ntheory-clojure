(ns vk.ntheory.util)

(defn power-of-two-parts
  "Returns power of two and rest for given number."
  [n]
  (assert (pos-int? n))
  (let [k (Long/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))


(defn power-of-two-parts'
  "Returns [power-of-two, odd-part] for given integer (Long, BigInteger, or clojure.lang.BigInt)."
  [n]
  (cond
    (instance? Long n)
      (let [k (Long/numberOfTrailingZeros n)
            r (bit-shift-right n k)]
        [k r])
    (instance? java.math.BigInteger n)
      (let [k (.getLowestSetBit n)
            r (.shiftRight n k)]
        [k r])
    (instance? clojure.lang.BigInt n)
      (let [bi (.toBigInteger n)]
        (let [k (.getLowestSetBit bi)
              r (.shiftRight bi k)]
          [k r]))
    :else
      (throw (IllegalArgumentException.
              "n must be Long, BigInteger, or clojure.lang.BigInt"))))



(comment
  (power-of-two-parts 7)

  (power-of-two-parts' 8N)

  )







