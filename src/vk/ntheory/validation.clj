(ns vk.ntheory.validation)


(def max-integer 1000000)


(defn check-integer-non-negative
  "Throw an execption if given `n` is negative."
  [n]
  (when (neg? n) (throw (Exception. "Expected non negative number")))
  n)


(defn check-integer-pos
  "Throw an execption if given `n` is not positive."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  n)




(defn check-integer-max
  "Throw an execption if given `n` is not positive."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  n)

(defn check-integer-range
  "Throw an execption if given `n` is not positive or more than `max-integer`."
  [n]
  (check-integer-pos n)
  (check-integer-max n)
  n)
