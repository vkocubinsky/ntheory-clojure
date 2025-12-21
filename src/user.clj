(ns user)

(set! *warn-on-reflection* true)

;; Compiling libraries
;; (compile 'my.namespace)  
;; (binding [*compile-files* true] (require 'user :reload-all))

(println "Hello, Valery!")

(comment
  (require '[vk.ntheory.factorization :as f])
  (def table (f/make-factorization {:type :odd-table
                                    :upper-limit 100}))

  )



