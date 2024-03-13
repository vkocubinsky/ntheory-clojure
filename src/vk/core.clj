(ns vk.core)


(defn pow
  "Power function."
  [a n]
  (when (neg? n) (throw (Exception. "Expected non negative number")))
  (apply * (repeat n a)))

(def max-integer 1000000)

(defn check-pos
  "Throw an execption if given `n` is not positive."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  n)

(defn check-integer-range
  "Throw an execption if given `n` is not positive or more than `max-integer`."
  [n]
  (when-not (pos? n) (throw (Exception. "Expected positive number")))
  (when-not (<= n max-integer) (throw (Exception. "Expected number <= ")))
  n)
