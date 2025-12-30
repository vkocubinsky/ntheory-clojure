(ns user
  (:import [java.io File]
           )
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.java.doc.api :refer [jdoc jdoc-data sigs]]
            )
  
  )

(set! *warn-on-reflection* true)

;; Compiling libraries
;; (compile 'my.namespace)  
;; (binding [*compile-files* true] (require 'user :reload-all))

(println "Hello, Valery from dev/user.clj")


(defn load-files [folder]
  (doseq [f (->> (io/file folder)
      file-seq
      (filter File/.isFile)
      (filter #(-> % File/.getName (String/.endsWith ".clj")))
      (remove #(= (File/.getName %) "user.clj"))
      (map File/.getPath)
      )]
    (println "Loading file " f " ...")
    (load-file f)
    (println "Done")
    )
  )




(comment
  (load-files "src")
  (load-files "test")
  
  
  )





