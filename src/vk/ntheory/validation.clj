(ns vk.ntheory.validation
  (:require [clojure.spec.alpha :as s]))

(def max-int
  "Max integer which can be factorized."
  1000000)

(defn- less-equal-max-int?
  "Less or equal to `max-int`"
  [n]
  (<= n max-int))

(defn- check-spec
  "Check is `data` conform to `spec`.
  If not throw an exception."
  [spec data]
  (let [parsed (s/conform spec data)]
    (if (s/invalid? parsed)
      (throw (IllegalArgumentException. (str "Invalid input: " (s/explain-str spec data))))
      data)))

(defn check-int
  [n]
  (check-spec int? n))

(defn check-int-pos
  "Check if given `n` is positive integer."
  [n]
  (check-spec (s/and int? pos?) n))

(defn check-int-non-neg
  "Check is given integer `n` non negative integer."
  [n]
  (check-spec (s/and int? (complement neg?))  n))

(defn check-int-non-zero
  "Check is given integer `n` is not zero."
  [n]
  (check-spec (s/and int? (complement zero?)) n))

(defn check-int-pos-max
  "Check `0 < n <= max-int`."
  [n]
  (check-spec (s/and int? pos? less-equal-max-int?) n))

(defn check-int-non-neg-max
  "Check `0 <= n <= max-int`"
  [n]
  (check-spec (s/and int? (complement neg?) less-equal-max-int?) n))

(defn check-int-non-zero-max
  "Check is given integer `n` is not zero and less or equal to max-int "
  [n]
  (check-spec (s/and int? (complement zero?) less-equal-max-int?) n))

