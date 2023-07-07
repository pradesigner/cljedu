;; -----------------------------------------------
;; src/myproject/welcome.clj
(ns pradesigner.welcome
  (:require [pradesigner.person-names :as pnames])) ;; NOTE: `myproject.welcome` requires `myproject.person-names`

(defn greet
  [first-name last-name]
  (str "Hello, " (pnames/familiar-name first-name last-name)))
