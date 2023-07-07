"
clojure cookbook
"

(ns user
  (:require [clojure.string :as s]))

;; * ch01 Primitive Data p1

(s/blank? "")
;; => true

;; ** 1.1 Changing the Capitalization of a String
(s/capitalize "this is a sentence")
;; => "This is a sentence"

(s/upper-case "loud noises!")
;; -> "LOUD NOISES!"

(s/lower-case "COLUMN_HEADER_ONE")
;; -> "column_header_one"


;; ** 1.2 Cleaning Up Whitespace in a String
(s/trim " \tBacon ipsum dolor sit.\n")
;; => "Bacon ipsum dolor sit."
(s/triml "  Column Header  ")
;; => "Column Header  "
(s/trimr "  Column Header  ")
;; => "  Column Header"

(s/replace "Who\t\nput  all this\fwhitespace here?" #"\s+" " ")
;; => "Who put all this whitespace here?"

(s/replace "Line 1\r\nLine 2" "\r\n" "\n") ;windows return replacement
;; => "Line 1\nLine 2"


;; ** 1.3 Building a String from Parts
(str "hello there. " "how are you " "doing?")
;; => "hello there. how are you doing?"
(apply str "hello there. " ["how " "are " "you " "doing?" ])
;; => "hello there. how are you doing?"
(apply str "ROT13: " [\W \h \y \v \h \f \  \P \n \r \f \n \e])
;; => "ROT13: Whyvhf Pnrfne"

;; Constructing a CSV from a header string and vector of rows
(def header "first_name,last_name,employee_number\n")
(def rows ["luke,vanderhart,1","ryan,neufeld,2"])
(apply str header (interpose "\n" rows))
;; => "first_name,last_name,employee_number\nluke,vanderhart,1\nryan,neufeld,2"

(str (s/join "\n" [header]) (s/join "\n" rows))
;; => "first_name,last_name,employee_number\nluke,vanderhart,1\nryan,neufeld,2"

(def food-items["milk""butter""flour""eggs"])
(s/join ", " food-items)
;; => "milk, butter, flour, eggs"

(s/join [1 2 3 4])
;; => "1234"


;; ** 1.4 Treating a String as a Sequence of Characters
(seq "Hello, world!")
;; => (\H \e \l \l \o \, \space \w \o \r \l \d \!)

(frequencies (s/lower-case "An adult all about A's"))
;; => {\space 4, \a 5, \b 1, \d 1, \' 1, \l 3, \n 1, \o 1, \s 1, \t 2, \u 2}

(defn yelling? [s]
  (every? #(or ;the predicate function
            (not (Character/isLetter %)) ;true if not letter
            (Character/isUpperCase %)) ;true if uppercase
          s))
(yelling? "LOUD NOISES")
;; => true
(yelling? "not SO LOUd")
;; => false


;; ** 1.5 Converting Between Characters and Integers
(int \a);; => 97
(int \0);; => 48
(map int "Hello, world!")
;; => (72 101 108 108 111 44 32 119 111 114 108 100 33)
(apply str (map char '(72 101 108 108 111 44 32 119 111 114 108 100 33)))
;; => "Hello, world!"


;; ** 1.6 Formatting Strings
(def me {:first-name "Ryan", :favorite-language "Clojure"})
(str "My name is " (:first-name me)
     ", and I really like to program in " (:favorite-language me))
;; => "My name is Ryan, and I really like to program in Clojure"

(apply str (interpose "   " [1 2.000 (/ 3 1) (/ 4 9)]))
;; => "1   2.0   3   4/9"

(defn filename [name i]
  (format "%04d-%s" i name))
(filename "my-awesome-file.txt" 42)
;; => "0042-my-awesome-file.txt"

(defn tableify [row]
  (apply format "%-20s | %-20s | %-20s" row))
(def header ["First Name", "Last Name", "Employee ID"])
(def employees [["Ryan", "Neufeld", 2]
                ["Luke", "Vanderhart", 1]])
(map println (map tableify (concat [header] employees)))
;; First Name           | Last Name            | Employee ID         
;; Ryan                 | Neufeld              | 2                   
;; Luke                 | Vanderhart           | 1                   

(->> (concat [header] employees)
     (map tableify)
     (mapv println))
;; First Name           | Last Name            | Employee ID         
;; Ryan                 | Neufeld              | 2                   
;; Luke                 | Vanderhart           | 1

;;not sure why it is mapv
;;http://bit.ly/javadoc-formatter for more about formatting


;; ** 1.7 Searching a String by Pattern

(re-find #"\d+""I've just finished reading Fahrenheit 451")
;; => "451"
(re-find #"inis" "I've just finished reading Fahrenheit 451")
;; => "inis"
;; probably makes more sense to use regex than actual string unless one is confirming it is there

;; In find, #"\w+" is any contiguous word characters
(re-find #"\w+""my-param")
;; => "my"

;; Unlike re-find, which matches any portion of a string,
;; re-matches matches if and only if the entire string matches the pattern
(re-matches #"\w+""my-param")
;; => nil
(re-matches #"\w+""myparam")
;; => "myparam"


;; ** 1.8 Pulling Values Out of a String Using Regular Expressions

(re-seq #"\w+""My Favorite Things")
;; => ("My" "Favorite" "Things")
(re-seq #"\d{3}-\d{4}""My phone number is 555-1234.")
;; => ("555-1234")
(re-seq #"\w+" "My phone number is 555-1234")
;; => ("My" "phone" "number" "is" "555" "1234") numbers seem to be contiguous word characters
(re-seq #"\d+" "My phone number is 555-1234")
;; => ("555" "1234")


(defn mentions
  [tweet]
  (re-seq #"(@|#)(\w+)" tweet))
(mentions "So long, @earth, and thanks for all the #fish. #goodbyes")
;; => (["@earth" "@" "earth"] ["#fish" "#" "fish"] ["#goodbyes" "#" "goodbyes"])
(re-find #"(@|#)(\w+)" "So long, @earth, and thanks for all the #fish. #goodbyes")
;; => ["@earth" "@" "earth"] which gets only the first one


;; ** 1.9. Performing Find and Replace on Strings


;; * ch02 Composite Data p63



;; * ch03 General Computing p121



;; * ch04 Local I/O 165



;; * ch05 Network I/O and Web Services p219



;; * ch06 Databases p253



;; * ch07 Web Applications p305



;; * ch08 Performance and Production p343



;; * ch09 Distributed Comuptation p375



;; * ch10 Testing p403




