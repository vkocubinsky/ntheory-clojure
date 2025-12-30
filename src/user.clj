(ns user
  (:import [java.io File]
           [java.sql Connection])
  (:require [clojure.java.io :as io]
            [clojure.pprint :as pp]
            [clojure.java.doc.api :refer [jdoc jdoc-data sigs]]
            )
  
  )

(set! *warn-on-reflection* true)

;; Compiling libraries
;; (compile 'my.namespace)  
;; (binding [*compile-files* true] (require 'user :reload-all))

(println "Hello, Valery!")


(defn load-all-files []
  (doseq [f (->> (io/file "src")
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

  (load-all-files)
  

  )





