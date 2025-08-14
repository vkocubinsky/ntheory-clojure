(ns vk.ntheory.ldt.table
  "Table for store positive integers."
  (:require [clojure.pprint :as pp]
            [vk.ntheory.util :as u]))

(defprotocol Table
  (table-set-number! [this k v] "Store value `v` for positive integer `k`.")
  (table-get-number [this k] "Get value for given positive integer `k`.")
  (table-contains? [this k] "Does given positive integer `k` in table")
  (table-upper-limit [this] "Return max number in table.")
  (table-keys [this] "Return all key numbers.")
  (table-vals [this] "Return all numbers.")
  )

(defn table-check-contains
  "Check does given number `n` in table."
  [table n]
  (u/check-true (table-contains? table n)
                "Out of range."
                {:upper-limit (table-upper-limit table) :value n}))


(defmulti make-table
  "Make an table from table-spec. Argument `table-spec` is a map with keys
  `:table-type`, `:init-type`, `:array-type`, `:upper-limit`."
  (fn [table-spec] [(:table-type table-spec) (:init-type table-spec) (:array-type table-spec)]))














