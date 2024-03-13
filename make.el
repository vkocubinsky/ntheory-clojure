;;select buffer
;;run export
(progn
  (org-md-export-as-markdown)
  (org-latex-export-to-pdf) 
 )
