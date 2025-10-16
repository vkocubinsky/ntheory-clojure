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


;; Load tools.namespace, it is optional for emacs
(try
  (require '[clojure.tools.namespace.repl :refer [refresh]])
  (println "Loaded tools.namespace" )
  (catch Exception e (println "Can't load tools.namespace"))
)

(println "Hello, Valery.")


