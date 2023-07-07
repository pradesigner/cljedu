"
exception handling
try catch finally
examples
"

;; * clojure exception handling
;; https://www.tutorialspoint.com/clojure/clojure_exception_handling.htm

;; Checked Exception
"The classes that extend Throwable class except RuntimeException and Error are known as checked exceptions. E.g. IOException, SQLException, etc. Checked exceptions are checked at compile-time."

;; This program displays Hello World
(defn Example []
  (def string1 (slurp "Example.txt"))
  (println string1))
(Example)
"Execution error (FileNotFoundException) at java.io.FileInputStream/open0 (FileInputStream.java:-2).
Example.txt (No such file or directory)"

;; Unchecked Exception
"The classes that extend RuntimeException are known as unchecked exceptions. For example, ArithmeticException, NullPointerException, ArrayIndexOutOfBoundsException, etc. Unchecked exceptions are not checked at compile-time rather they are checked at runtime."

(defn Example []
  (try
    (aget (int-array [1 2 3]) 5)
    (catch Exception e (println (str "caught exception: " (.toString e))))
    (finally (println "This is our final block")))
  (println "Let's move on"))
(Example)
"caught exception: java.lang.ArrayIndexOutOfBoundsException: Index 5 out of bounds for length 3
This is our final block
Let's move on"

;; Error
"Error is irrecoverable e.g. OutOfMemoryError, VirtualMachineError, AssertionError, etc. These are errors which the program can never recover from and will cause the program to crash."

;; catching exceptions
(defn Example []
  (try
    (def string1 (slurp "Example.txt"))
    (println string1)
    (catch Exception e
      (println (str "caught exception: " (.getMessage e))))))
(Example)
"caught exception: Example.txt (No such file or directory)"

;; multiple catch blocks
(defn Example []
  (try
    (def string1 (slurp "Example.txt"))
    (println string1)
    
    (catch java.io.FileNotFoundException e
      (println (str "caught file exception: " (.getMessage e))))
    
    (catch Exception e
      (println (str "caught exception: " (.getMessage e)))))
  (println "Let's move on"))
(Example)
"caught file exception: Example.txt (No such file or directory)
Let's move on"

;; finally block
"The finally block follows a try block or a catch block. A finally block of code always executes, irrespective of occurrence of an Exception."

(defn Example []
  (try
    (def string1 (slurp "Example.txt"))
    (println string1)
    
    (catch java.io.FileNotFoundException e (println (str "caught file exception: " (.getMessage e))))
    
    (catch Exception e (println (str "caught exception: " (.getMessage e))))

    (finally (println "This is our final block")))
  (println "Let's move on"))
(Example)
"caught file exception: Example.txt (No such file or directory)
This is our final block
Let's move on"

;; * recur from exceptions
;; ** Clojure: How to to recur upon exception?
https://stackoverflow.com/questions/1879885/clojure-how-to-to-recur-upon-exception
;;several ideas here some using macros
;;one below and one other don't use macros

(defn try3 []
 (loop [tries 3]
   (when (try
           (/ 3 (rand-int 2))
           false ; so 'when' is false, whatever 'might-throw-exception' returned
           (catch Exception e ;; doesn't go here unless exception
             (pos? tries) ;; but we check if tries has gone -ve to change when's true/false
             ))
     (prn tries)
     (recur (dec tries)) ;; tries dec and sent back to loop
     )))
;; problem is that there seems to be no way to get the answer out
;; so it's ok for side-effects but not for using values in program

;; ** retrying something 3 times before throwing an exception - in clojure

(defn retry
  [retries f & args]
  (let [res (try {:value (apply f args)}
                 (catch Exception e
                   (if (zero? retries)
                     (throw e)
                     {:exception e})))]
    (if (:exception res)
      (recur (dec retries) f args)
      (:value res))))

(retry 3 (fn [] 
          (println "foo") 
          (if (zero? (rand-int 2))
              (throw (Exception. "foo"))
              2)))



;; * some trys
(defn test
  []
  (prn "here we go")
  (try (/ 1 0)
       (catch Exception e (prn (str "exception: " (.getMessage e))))
       (finally (prn "moving on")))
  (prn "oops"))


(defn div-err []
  (let [ns [0 2 4]
        ds [0 1 2]]
    (for [n ns d ds]
      (try
        (/ n d)
        (catch Exception e)))))
;;by catching the exception, function keeps going instead of crashing

(defn keep-trying []
  (loop [d 3
         k 9]
    (prn k
         (try (/ 7 d)
              (catch Exception e)))
    (if (= k 0)
      "done"
      (recur
        (rand-int 7)
        (dec k)))))


(for [i [1 2 3]]
  (do
    (try
      (slurp "resources/slurpit")
      (catch Exception e))
    (prn i))) ;; 1 2 3 even when no slurpit file

(for [i [1 2 3]]
  (do
    (try
      (slurp "resources/slurpit")
      (catch Exception e (prn (str "error: on " i " " (.toString e)))))
    )) ;;; this works too printing error only if no slurpit file
