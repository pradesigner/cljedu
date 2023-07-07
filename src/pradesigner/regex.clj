"
regex
"

(ns user)

;; Regular Expressions in Clojure
https://nakkaya.com/2009/10/25/regular-expressions-in-clojure/

(re-find #"quick" "The quick brown fox jumps over the lazy dog")
;; => "quick"
(re-find #"(f(oo ba)r)" "foo bar")
;; => ["foo bar" "foo bar" "oo ba"]
(re-find #"(f(o(o b)ar))" "foo bar")
;; => ["foo bar" "foo bar" "oo bar" "o b"]

;; Unix lines mode can enabled via (?d).
;; Case-insensitive mode can enabled via (?i).
;; Multiline mode can enabled via (?m).
;; Dotall mode can enabled via (?s).
;; Unicode-aware case folding can enabled via (?u).


;;; Regular Expressions: The Complete Tutorial by Jan Goyvaerts

;;;; 3. First Look at How a Regex Engine Works Internally 
;; regex directed or text-directed
(re-find #"regex|regex not" "regex not")
;; => "regex"
(re-seq #"cat" "He captured a catfish for his cat.")
;; => ("cat" "cat")

;;;; 4. Character Classes or Character Sets
(re-seq #"gr[ae]y" "what is this gray bit of grey?")
;; => ("gray" "grey")

;; Find a word, even if it is misspelled, such as «sep[ae]r[ae]te» or «li[cs]en[cs]e».
;; Find an identifier in a programming language with «[A-Za-z_][A-Za-z_0-9]*».
;; Find a C-style hexadecimal number with «0[xX][A-Fa-f0-9]+».

;; Negated Character Classes 
(re-find #"q[^u]" "Iraq")
(re-find #"q[^u]" "Iraq is a country")
(re-find #"q(?!u)" "Iraq")

;; Metacharacters Inside Character Classes 
(re-seq #"q[u^]" "quit being quite so q^uiet")

;; Shorthand Character Classes
(def scc #"[\d\W]")
(re-seq scc "9 times 10 giving 90!")
;; => ("9" " " " " "1" "0" " " " " "9" "0" "!")

(re-seq #"[\s\d]" "1 + 2 = 3")
;; => ("1" " " " " "2" " " " " "3")
(re-find #"[\s\d]" "1 + 2 = 3")
;; => "1"
(re-seq #"\s\d" "1 + 2 = 3")
;; => (" 2" " 3")

;; Negated Shorthand Character Classes
(re-find #"\D" "9th") ;; => "t"
(re-find #"[^\d]" "9th") ;; => "t"
(re-find #"[^\d\s]" " ")
(re-find #"[\D\S]" " ")

;; Repeating Character Classes
(re-find #"\d" "999")
(re-find #"\d?" "999")
(re-find #"\d+" "999")
(re-find #"\d*" "999")
(re-find #"([0-9])\1+" "999")
;; => ["999" "9"]
(re-find #"([0-9])\1+" "789")
;; => nil
(re-find #"([0-9])\1+" "123999789")
;; => ["999" "9"]


;;;; 5. The Dot Matches (Almost) Any Character
. is ^\n (don't overuse)
(re-find #"\d\d[-/.]\d\d[-/.]\d\d" "99/99/99")
;; => "99/99/99"
(re-find #"\d\d[-/.]\d\d[-/.]\d\d" "99-99-99")
;; => "99-99-99"
(re-find #"\d\d[-/.]\d\d[-/.]\d\d" "99.99.99")
;; => "99.99.99"

;; Use Negated Character Sets Instead of the Dot
;; the idea may be to make things more specific?
(re-find #"\".*\"" "Put a \"string\"")
(re-find #"\".*\"" "Houston, we have a problem with \"string one\" and \"string two\"")
;; => "\"string one\" and \"string two\""
(re-find #"[^\"\n]*" "Houston, we have a problem with \"string one\" and \"string two\"")
;; => "Houston, we have a problem with " ????!!!!
;; TODO this can't work so why is it here? what is it trying to do?
(re-seq #"\".*?\"" "Houston, we have a problem with \"string one\" and \"string two\"")
;; => ("\"string one\"" "\"string two\"") which solves the problem


;;;; 6. Start of  String and End of  String Anchors
(re-find #"^\d+" "a999") ;; => nil
(re-find #"^\d+$" "a999") ;; => nil
(re-find #"^\d+$" "89a99") ;; => nil
(re-find #"\d+$" "8ab99") ;; => "99"
(re-find #"^\d+$" "8999") ;; => "8999"

;;multiline matches
(re-seq #"\w{6}.\w{6}" "nthaue\nanteeu")
(re-seq #"(?s)\w{6}.\w{6}" "nthaue\nanteeu")

;; Permanent Start of String and End of String Anchors
(re-find #"\A\w+" "here is something\nthat is there");; => "here"
(re-find #"\w+\Z" "here is something\nthat is there");; => "there"
(re-seq #"(?s)\A\w+" "here is something\nthat is there")

(re-find #"\d{3}.\d{3}" "123\n456");; => nil
(re-find #"(?s)\d{3}.\d{3}" "123\n456");; => "123\n456"
(re-find #"(?m)\d{3}.\d{3}" "123\n456");; => nil
(re-find #"(?m)\d{3}" "123\n456");; => "123"
(re-find #"(?m)\d{3}\Z" "123\n456");; => "456"
(re-find #"(?m)\d{3}\z" "123\n456");; => "456"
https://stackoverflow.com/questions/15020669/clojure-multiline-regular-expression

;; Strings Ending with a Line Break
(re-find #"\w+\Z" "aesah\n");; => "aesah"
(re-find #"\w+\z" "aesah\n")

(re-find #"(?m)^4$" "749\n486\n4");; => "4"


;;;; 7. Word Boundaries

