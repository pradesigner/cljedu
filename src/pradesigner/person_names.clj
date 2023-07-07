;; -----------------------------------------------
;; src/myproject/person_names.clj
(ns pradesigner.person-names
  (:require [clojure.string :as str]))

(def nicknames
  {"Robert"     "Bob"
   "Abigail"    "Abbie"
   "William"    "Bill"
   "Jacqueline" "Jackie"})

(defn familiar-name
  "What to call someone you may be familiar with."
  [first-name last-name]
  (let [fname (str/capitalize first-name)
        lname (str/capitalize last-name)]
    (or
      (get nicknames fname)
      (str fname " " lname))))
