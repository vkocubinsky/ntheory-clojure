
(inf-clojure-eval-string "(+ 2 3)\n")


(inf-clojure-query-string "(+ 1 1)\n")

(comint-proc-query (inf-clojure-proc) "(+ 1 2)\n")
(comint-send-string (inf-clojure-proc) "(+ 1 3)")



(inf-clojure-query-old "(apropos \"map\")\n")


(inf-clojure--prompt-repl-type)
