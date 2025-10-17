(ns user)

(set! *warn-on-reflection* true)

;; Load Compliment, it is optional for emacs
(try
  (require 'compliment.core)
  (println "Loaded compliment" )
  (catch Exception e (println "Can't load compliment"))
  )

;; Load clj-reload, it is optional for emacs
(try
  (require '[clj-reload.core :as reload])
  (println "Loaded clj-reload" )
  (catch Exception e (println "Can't load clj-reload"))
)

;; Load Decompiler, it is optional for emacs
(try
  (require '[clj-java-decompiler.core :refer [decompile]])
  (println "Loaded decompiler" )
  (catch Exception e (println "Can't load decompiler"))
)


(def foo (atom {}))

;;(reset! foo {:a 5})

(deref foo)

(println "Hello, Valery.")


