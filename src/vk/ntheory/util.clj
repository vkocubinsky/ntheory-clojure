(ns vk.ntheory.util)


(defn check-true
  "Throws exception when x is not true."
  [x err-msg err-map]
  (when-not x
    (throw (ex-info err-msg err-map))))

(defn check-pos-int [n]
  (check-true (pos-int? n) "Expected positive integer."
              {:n n}))


(defn power-of-two-parts
  "Returns power of two and rest for given number."
  [n]
  (assert (pos-int? n))
  (let [k (Integer/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))
