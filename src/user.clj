(ns user)

(set! *warn-on-reflection* true)

(try
  (require 'compliment.core)
  (catch Exception _))

(println "Hello from user namespace.")


