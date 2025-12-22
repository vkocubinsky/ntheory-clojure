(ns vk.ntheory.util)

(defn power-of-two-parts
  "Returns power of two and rest for given number."
  [n]
  (assert (pos-int? n))
  (let [k (Long/numberOfTrailingZeros n)
        r (bit-shift-right n k)]
    [k r]))


(comment
  (power-of-two-parts 7)

  )







