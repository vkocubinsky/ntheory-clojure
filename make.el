(save-window-excursion
  (let* ((prj (project-current t))
         (root (project-root prj))
         (readme (concat root "readme.org")))
    (find-file readme)
    (org-babel-execute-buffer)
    (find-file readme)
    (org-md-export-to-markdown)
    (find-file readme)
    (org-latex-export-to-pdf) 
    )
  )
