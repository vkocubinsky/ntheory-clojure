(ns user)

(set! *warn-on-reflection* true)




;; Load Compliment, it is optional for emacs
(try
  (require 'compliment.core)
  (println "Loaded compliment" )
  (catch Exception _ (println "Can't load compliment"))
  )

;; Load clj-reload, it is optional for emacs
(try
  (require '[clj-reload.core :as reload])
  ;;(reload/init {:dirs ["src" "dev" "test"]})
  (println "Loaded clj-reload" )
  (catch Exception _ (println "Can't load clj-reload"))
)

;; Decompiler, it is optional for emacs
(try
  (require '[clj-java-decompiler.core :refer [decompile]])
  (println "Loaded decompiler" )
  (catch Exception _ (println "Can't load decompiler"))
)

;; Compiling libraries
;; (compile 'my.namespace)  
;; (binding [*compile-files* true] (require 'user :reload-all))

(println "Hello, Valery!")




