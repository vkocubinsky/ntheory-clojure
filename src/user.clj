(ns user
  (:import java.math BigInteger)
  )

(set! *warn-on-reflection* true)

;; Compiling libraries
;; (compile 'my.namespace)  
;; (binding [*compile-files* true] (require 'user :reload-all))

(println "Hello, Valery!")

(comment
  (BigInteger.)
  (remove #{'user 'clojure.core 'clojure.string} (all-ns))
  (remove-ns 'vk.ntheory.factorization)
  )



