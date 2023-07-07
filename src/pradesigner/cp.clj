"
clojure programming
"

(ns user
  (:require [clojure.string :as s]))


;; * ch01 down the rabbit hole p1
;; why clojure p1
;; obtaining clojure p3
;; The Clojure REPL p3
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))


(average [60 80 100 400])
;; => 160

“A classpath primer” on page 331

;; No, Parentheses Actually Won’t Make You Go Blind p6
clojure really doesn't use that many more parentheses

;; Expressions, Operators, Syntax, and Precedence p7
all clojure code made up of expressions
pick on java, python, ruby p8
“On the Importance of Values” on page 52
“Ahead-of-TimeCompilation” on page 337

;; Homoiconicity
Clojure programs are written using Clojure data structures that represent that AST directly
code-as-data is significant simplification compared to non-homoiconic languages
basis for macros
you are programming using Clojure data structure literals
This isn’t just a bunch of text that is somehow transformed into a function definitionthrough the operation of a black box

;; The Reader
;; scalar literals p13
;; strings
;; booleans
;; nil

;; characters
\c \h \u00ff \o41 \space \newline ...

;; ** keywords

(def person {:name "Sandra"
             :city "Portland"})
(:name person)
;; => "Sandra"
(person :name)
;; => "Sandra"

(def pizza {:name "Ramunto's"
            :location "Claremont, NH"
            ::location "43.3734,-72.3365"})
pizza
;; => {:name "Ramunto's", :location "Claremont, NH", :user/location "43.3734,-72.3365"}
(pizza :name)
;; => "Ramunto's"
(pizza :location)
;; => "Claremont, NH"
(pizza :user/location)
;; => "43.3734,-72.3365"
(name :user/location)
;; => "location"
(namespace :user/location)
;; => "user"
(name :location)
;; => "location"
(namespace :location)
;; => nil
(name ::location)
;; => "location"
(namespace ::location)
;; => "user"


;; ** symbols
eg average, cljing/average

;; ** numbers
in addition to common ones
Hexadecimal notation
Octal notation
Flexible numeralbases
Arbitrary-precision numbers
Rational numbers

Chapter 11
“Bounded Versus Arbitrary Precision” on page 428
“Rationals” on page 424


;; ** regexp

(class #"(p|h)ail")
;; => java.util.regex.Pattern
(re-seq #"(\d+)-(\d+)" "1-3")
;; => (["1-3" "1" "3"])
(re-seq #"(...) (...)" "foo bar")
;; => (["foo bar" "foo" "bar"])
(re-seq #"(...) (...)" "regex finds foo bar and a lot more")
;; => (["gex fin" "gex" "fin"] ["foo bar" "foo" "bar"] ["and a l" "and" "a l"])
(re-seq #"ab" "abc bca bca cab")
;; => ("ab" "ab")
(re-seq #"(...)" "abc bca bca cab")
;; => (["abc" "abc"] [" bc" " bc"] ["a b" "a b"] ["ca " "ca "] ["cab" "cab"])
(re-seq #"(...) (...)" "abc bca bca cab")
;; => (["abc bca" "abc" "bca"] ["bca cab" "bca" "cab"])
(re-seq #"(...) (...) (...)" "abc bca bca cab")
;; => (["abc bca bca" "abc" "bca" "bca"])
(re-seq #"(..) (..)" "abc bca bca cab")
;; => (["bc bc" "bc" "bc"] ["ca ca" "ca" "ca"])

TODO investigate regex
full specification of what forms the Java regular expressionimplementation supports:
http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html


;; ** Comments

(read-string "(+ 1 2 (* 2 2) 8)")
;; => (+ 1 2 (* 2 2) 8)
(read-string "(+ 1 2 #_(* 2 2) 8)")
;; => (+ 1 2 8)

#_(is really useful to make things 'vanish' at times!)


;; Whitespace and Commas
no diff

;; ** Collection literals

'(a b :name 12.5)       ;; list
['a 'b :name 12.5]      ;; vector
{:name "Chas" :age 31}  ;; map
#{1 2 3}                ;; set

;; ** Miscellaneous Reader Sugar

“Suppressing Evaluation: quote” on page 24
“Function literals” on page 40
“Referring to Vars: var” on page 44
“Clojure Reference Types” on page 170
Macrosare explored in Chapter 5
“Java Interop: .and new” on page 44
“Metadata” on page 134


;; ** Namespaces
Clojure’s fundamental unit of code modularity

*ns* ;in repl gives user
(ns foo) ;will put us in namespace foo
(def x "foo") ;gives x the value of foo, but in namespace foo
(ns user) ;takes us back to user
x ;produces error because it wasn't defined in user, but in foo

namespaces by default import all classes in java.lang and clojure.core

“Clojure Reference Types” on page 170
“Vars Are Not Variables” on page 206
“Defining  and  Using  Namespaces” on page 322

;; ** Symbol Evaluation
there are many symbols, each of which refers to either a var in scope in the currentnamespace or a local value
;; ** Special Forms
evaluation to:
1. value of named var or local
2. special form  Clojure’s primitive building blocks of computation (even build other languages on top of these!)


The Roots of Lisp (http://www.paulgraham.com/rootsoflisp.html)

;; ** Suppressing Evaluation: quote
'(+ x x)
;; => (+ x x)
'@x
;; => (clojure.core/deref x)
'`(a b ~c)
;; =>
(clojure.core/seq
 (clojure.core/concat
  (clojure.core/list 'user/a)
  (clojure.core/list 'user/b)
  (clojure.core/list c)))

;; ** Code Blocks: do
(do
  (println "hi")
  (apply * [4 5 6]))
;; =>
hi
120

;; ** Defining Vars: def
(def p "foo")

;; ** Local Bindings: let
let is implicitly used anywhere locals are required

(defn hypot
  [x,y]
  (let [x2 (* x x)
        y2 (* y y)]
    (Math/sqrt (+ x2 y2))))

;; ** Destructuring (let, Part 2)
allows functions and libraries to be trivially composed around the data
destructuring provides a concise syntax for declaratively pulling apart collec-tions and binding values

                                        ; sequential destructuring
Destructuring forms are intended to mirror the structure of the collection that is beingbound.

(def v [42 "foo" 99.2 [5 12]])
(let [[x _ y] v]
  (+ x y))
;; => 141.2
(let [[x _ _ [y z]] v]
  (+ x y z))
;; => 59
(let [[x & rest] v]
  rest)
;; => ("foo" 99.2 [5 12])
(let [[x _ z :as original-vector] v]
  (conj original-vector (+ x z)))
;; => [42 "foo" 99.2 [5 12] 141.2]
(let [[_ _ _ [five twelve]] v]
  [five twelve])
;; => [5 12]


;; ** map destructuring
Map destructuring is conceptually identical to sequential destructuring

(def m {:a 5, :b 6, :c [7 8 9], :d {:e 10, :f 11}, "foo" 88, 42 false})

(let [a (:a m)
      b (:b m)]
  (+ a b))
;; => 11
(let [{a :a b :b} m]
  (+ a b))
;; => 11
(let [{f "foo"} m]
  (+ f 12))
;; => 100
(let [{v 42} m]
  (if v 1 0))
;; => 0

(let [{x 3 y 8} [12 0 0 -18 44 6 0 0 1]]
  (+ x y))
;; => -17

(let [{{e :e} :d} m]
  (* 2 e))
;; => 20

(let [{[x _ y] :c} m]
  (+ x y))
;; => 16

TODO p33
polymorphic behavior of get - what does that mean?
what exactly is meant by composition - eg of both map&seq destructuring

(def map-in-vector ["James" {:birthday (java.util.Date. 73 1 6)}])
(let [[name {bd :birthday}] map-in-vector]
  (str name " was born on " bd))
;; => "James was born on Tue Feb 06 00:00:00 PST 1973"

                                        ; Retaining  the  destructured  value
(let [{r1 :x r2 :y :as randoms}
      (zipmap [:x :y :z] (repeatedly
                          (partial rand-int 10)))]
  (assoc randoms :sum (+ r1 r2)))
;; => {:x 3, :y 9, :z 5, :sum 12}

                                        ; Default values
(let [{k :unknown
       x :a
       :or {k 50}} m] ; better than k (or k 50)
  (+ k x))
;; => 55

TODO figure this out!
(let [{opt1 :option} {:option false} opt1 (or opt1 true)
      {opt2 :option :or {opt2 true}} {:option false}]
  {:opt1 opt1 :opt2 opt2})
;; => {:opt1 true, :opt2 false}

                                        ; Binding values to their keys’ names with :keys :strs :syms
(def chas {:name "Chas" :age 31 :location "Massachusetts"})
(let [{name :name age :age location :location} chas]
  (format "%s is %s years old and lives in %s." name age location))
;; => "Chas is 31 years old and lives in Massachusetts."
(let [{:keys [name age location]} chas]
  (format "%s is %s years old and lives in %s." name age location))
;; => "Chas is 31 years old and lives in Massachusetts."
(let [{:keys [location age name]} chas]
  (format "%s is %s years old and lives in %s." name age location))
;; => "Chas is 31 years old and lives in Massachusetts."

(def brian {"name" "Brian" "age" 31 "location" "British Columbia"})
(let [{:strs [name age location]} brian]
  (format "%s is %s years old and lives in %s." name age location))
;; => "Brian is 31 years old and lives in British Columbia."

(def christophe {'name "Christophe" 'age 33 'location "Alps"})
(let [{:syms [name location age]} christophe]
  (format "%s is %s years old and lives in %s" name age location))
;; => "Christophe is 33 years old and lives in Alps"

                                        ; Destructuring rest sequences as map key/value pairs
(def user-info ["robert8990" 2011 :name "Bob" :city "Boston"])
(let [[username year & {:keys [name city]}] user-info]
  (format "%s is in %s" name city))
;; => "Bob is in Boston"


;; ** Creating Functions: fn

((fn [x y z]
   (+ x y z))
 7 8 9)
;; => 24

                                        ; multiple arities
(defn strange-adder
  "adds 1 if one input or adds up two inputs"
  ([x] (strange-adder x 1))
  ([x y] (+ x y)))
(strange-adder 5);; => 6
(strange-adder 5 6);; => 11

                                        ; this is really
(def strange-adder1 (fn adder-self-reference
                      ([x] (adder-self-reference x 1))
                      ([x y] (+ x y))))

                                        ; mutually recursive functions with letfn
;;works only if true eg (odd? 1) or (even? 2)
;;sof if false eg (odd? 2) or (even? 1)
(letfn [(odd? [n]
          (even? (dec n)))
        (even? [n]
          (or (zero? n)
              (odd? (dec n))))]
  (even? 2))


                                        ; Destructuring function arguments
(defn concat-rest
  [x & rest] ; variadic
  (apply str (butlast rest)))
(concat-rest 0 1 2 3 4 5 6 7 8 9)
;; => "12345678"

(defn make-user
  [& [user-id]] ;;TODO what is the reason [] works here?
  {:user-id (or user-id
                (str (java.util.UUID/randomUUID)))})
(make-user)
;; => {:user-id "ba791fbc-89dc-4d03-a4e3-a5f11924ed9d"}
(make-user "bob")
;; => {:user-id "bob"}

                                        ;verbose way to destructure
(defn make-user-kwd-verbose
  [username & {email :email
               join-date :join-date
               :or {join-date (java.util.Date.)}
               }]
  {:username username
   :email email
   :join-date join-date
   })
(make-user-kwd-verbose "bob")
;; => {:username "bob", :email nil, :join-date #inst "2020-08-11T20:56:21.310-00:00"}

                                        ;efficient way to destructure
(defn make-user-kwd
  [username & {:keys [email join-date] ;same idea as with :keys in let
               :or {join-date (java.util.Date.)}}]
  {:username username
   :join-date join-date
   :email email
   :exp-date (java.util.Date. (long (+ 2.592e9 (.getTime join-date))))})
(make-user-kwd "bob")
;; => {:username "bob", :join-date #inst "2020-08-11T19:47:00.301-00:00", :email nil, :exp-date #inst "2020-09-10T19:47:00.301-00:00"}
(make-user-kwd
 "bob"
 :join-date (java.util.Date. 111 0 1)
 :email "bobby@example.com")
;; => {:username "bob", :join-date #inst "2011-01-01T08:00:00.000-00:00", :email "bobby@example.com", :exp-date #inst "2011-01-31T08:00:00.000-00:00"}

                                        ;fail destructuring inside body
(defn make-user-kwd-fail
  [username & {:keys [email join-date]
               :or {join-date (java.util.Date.)}}]
  {:keys [username join-date email (java.util.Date. (long (+ 2.592e9 (.getTime join-date))))]})
(make-user-kwd-fail "bobby")
;; => {:keys ["bobby" #inst "2020-08-11T19:53:35.138-00:00" nil #inst "2020-09-10T19:53:35.138-00:00"]}
;; doesn't work of course because you can't destructure within the body

(defn foo
  [& {k ["m" 1]}]
  k)
(foo ["m" 1] 0)

“Preconditions and Postconditions” on page 487

                                        ; function literals
(fn [x y]
  (println (str x \^ y))
  (Math/pow x y))
#(do (println (str %1 \^ %2))
     (Math/pow %1 %2))

;; Arity and arguments specified using unnamed positional symbols
(fn [x & rest]
  (- x (apply + rest)))
#(- % (apply + %&))
;; Function literals cannot be nested

;; interesting functions for infix from joc showing arity
(defn r->lfix
  ([a op b]              (op a b))
  ([a op1 b op2 c]       (op1 a (op2 b c)))
  ([a op1 b op2 c op3 d] (op1 a (op2 b (op3 c d)))))
(r->lfix 1 + 2 * 3)
;; => 7

(defn l->rfix
  ([a op b]              (op a b))
  ([a op1 b op2 c]       (op2 c (op1 a b)))
  ([a op1 b op2 c op3 d] (op3 d (op2 c (op1 a b)))))
(l->rfix 1 + 2 * 3)
;; => 9

(def order {+ 0 - 0 * 1 / 1})

(defn infix3
  [a op1 b op2 c]
  (if (< (get order op1) (get order op2))
    (r->lfix a op1 b op2 c)
    (l->rfix a op1 b op2 c)))
(infix3 1 + 2 * 3)
;; => 7


;; Conditionals: if
;; true unless nil or false

;; when
(when (= 1 1)
  (println "true"))
(when (= 1 2)
  (println "true")) ;body not executed

;; cond
(cond
  (= 1 2) (println "false")
  (= 1 1) (println "true"))

;; when-let
(when-let [s [1 2 3]]
  (rest s))
;; => (2 3)

;; if-let
(if-let [x false]
  (str "then is " x)
  (str "else can't show x because it isn't bound"))

;; true? false? unrelated to if conditionals
(false? "stiring")
;; => false
(true? "stiring")
;; => false
(if "stiring"
  \t
  \f)
;; => \t



;; ** Looping: loop and recur

(loop [x 5]
  (if (neg? x)
    x
    (recur (dec x))))

(defn countdown
  [x]
  (if (zero? x)
    :blastoff!
    (do (println x)
        (recur (dec x)))))
(countdown 5)
;; => 5 4 3 2 1
;; => :blastoff!

(dotimes [n 6]
  (if (= n 5)
    (prn "blastoff")
    (prn (- 5 n))))

(doseq [x [1 2 3]
        y [4 5 6]]
  (prn (* x y)))
;; => prints 4 5 6 8 10 12 12 15 18
;;=> nil

(dotimes [n 2]
  (println "n is " n))
;; => prints n is 0, n is 1
;; => nil

“Visualizing the Mandelbrot Set in Clojure” on page 449



;; ** Referring to Vars: var
(def x 5)
(var x)

;;;; Java Interop: . and new
;;;; Exception Handling: try and throw
;;;; Specialized Mutation: set!
;;;; Primitive Locking: monitor-enter and monitor-exit



;; ** Putting It All Together

;; eval is rarely used in clojure programs

(defn embedded-repl
  "a naive clojure repl"
  []
  (print (str (ns-name *ns*)))
  (flush)
  (let [expr (read)
        value (eval expr)]
    (when (not= :quit value)
      (println value)
      (recur))))


;; * ch02 functional programming p51
;; ** What Does Functional Programming Mean?
;; ** On the Importance of Values

                                        ;p57

(def h {[1,2] 3})
(h [1,2])
(conj (first (keys h)) 3)
(h [1,2]) ;; => 3
(h [1,2,3]) ;; => nil
h ;; => {[1 2] 3}
;; h key remains unchanged

;;TODO brian goetz discusses pitfalls of mutability

                                        ;p62

;; ** First-Class and Higher-Order Functions

;;map always returns a sequence
;;(map ƒ [a bc]) is equivalent to [(ƒ a) (ƒ b) (ƒ c)]
;;(map ƒ [a b c] [x y z])is equivalent to [(ƒ a x) (ƒ b y) (ƒ c z)]
(map clojure.string/lower-case
     ["Java" "Imperative" "Weeping" "Clojure" "Learning" "Peace"])
;; => ("java" "imperative" "weeping" "clojure" "learning" "peace")
(map * [1 2 3 4] [5 6 7 8])
;; => (5 12 21 32)
(map + [1 2 3] [4 5 6] [7 8 9])
;; => (12 15 18)

                                        ;p63
;;reduce produces a value from applying a function to a collection
(reduce max [0 -3 10 48])
;; => 48
;; essentially (max (max (max 0 -3) 10) 48)
(apply max [0 -3 10 48])
;; => 48

(reduce + 50 [1 2 3 4])
;; => 60
;; essentially (+ 50 (+ 4 (+ 3 (+ 1 2))))

(reduce
 (fn [m v]               ;the function
   (assoc m v (* v v)))
 {}                      ;the initial value in this case an empty maps
 [1 2 3 4])
;; => {1 1, 2 4, 3 9, 4 16}
(reduce
 #(assoc % %2 (* %2 %2))
 {}
 [1 2 3 4])
;; => {1 1, 2 4, 3 9, 4 16}

                                        ;p65
;;partial - function application
(apply hash-map [:a 5 :b 4])
;; => {:b 4, :a 5}
(apply * 0.5 3 [2 -2 10])
;; => -60.0

;;characteristic of partial it can be used to easily toss off a derivative of a function
;; without concern for what the remainder of the function’s arguments
(def only-strings (partial filter string?))
(only-strings ["a" 5 "b" 8])
;; => ("a" "b")
(filter string? ["a" 4 "b" 5])
;; => ("a" "b")
(#(filter string? %) ["a" 4 "b" 5])
;; => ("a" "b")
(#(filter % ["a" 4 "b" 5]) string?)
;; => ("a" "b")
;;function literals force you to fully specify all of the arguments
(#(map *) [1 2 3] [4 5 6] [7 8 9])
;;Wrong number of args (3) passed
(#(map * % %2 %3) [1 2 3] [4 5 6] [7 8 9])
;; => (28 80 162)
(#(map * % %2 %3) [1 2 3] [4 5 6])
;;Wrong number of args (2) passed
(#(apply map * %&) [1 2 3] [4 5 6] [7 8 9])
;; => (28 80 162)
(#(apply map * %&) [1 2 3])
;; => (1 2 3)

;;but using partial is more concise
(def mm (partial map *))
(mm [1 2 3] [4 5 6] [7 8 9])
;; => (28 80 162)
(mm [1 2 3])
;; => (1 2 3)


                                        ;p68 composition of functionality
;;result of each function in the composite must be
;;a suitable argument for the function that precedes it syntactically.
(def str-neg-sum (comp str - +))
(str-neg-sum 10 12 3.4)
;; => "-25.4"
(apply str-neg-sum [10 12 3.4])
;; => "-25.4"

(def camel->keyword (comp keyword
                          str/join
                          (partial interpose \-)
                          (partial map str/lower-case)
                          #(str/split % #"(?<=[a-z])(?=[A-Z])")))
(camel->keyword "CamelCase")
;; => :camel-case
(camel->keyword "lowerCamelCase")
;; => :lower-camel-case

(def camel-pairs->map (comp (partial apply hash-map)
                            (partial map-indexed (fn [i x]
                                                   (if (odd? i)
                                                     x
                                                     (camel->keyword x))))))
(camel-pairs->map ["CamelCase" 5 "lowerCamelCase" 3])
;; => {:camel-case 5, :lower-camel-case 3}

                                        ;p71 writing hofs???
(defn adder
  "adder returns a function that adds its argument to adder's argument"
  [n]
  (fn [x] (+ n x)))
((adder 5) 18)
;; => 23

(defn doubler
  "doubler returns a function that applies doubler's function argument to the function's sequence argument"
  [f]
  (fn [& args]
    (* 2 (apply f args))))
(def double-+ (doubler +))
(double-+ 1 2 3)
;; => 12

                                        ;p72 primitive logging system with composable hofs???




;; * ch03 collections and data structures p83
;; * ch04 concurrency and parallelism p159
;; * ch05 macros p229
;; * ch06 datatypes and protocols p263
;; * ch07 multimethods p301
;; * ch08 organizing and building clojure projects p321
;; ** Project Geography
(defn a [] 20) ;; is in 'user

;; refer, use, require effectively p325
;; namespace file rules p328
;; classpath primer
;; location x 3 or projecct layout conventions

;; ** The Functional Organization of Clojure Codebases
how to organize Clojure code from a functional perspective:
How many functions should be used to implement a particular algorithm?
How many functions should a namespace contain?
How many namespaces should a project contain?

there’s probably no such thing as a “typical structure” of Clojure programs.

Basic project organization principles:
Keep different things separate
Keep related things together
Define vars that contain implementation-specific data or functions as much
as possible
Don’t repeat yourself: define constants only once in a designated namespace



;; * ch09 java and jvm interoperability p355
;; * ch10 repl-oriented programming p393
;; * ch11 numerics and mathematics p421
;; * ch12 design patterns p457
;; * ch13 testing p471
;; * ch14 using relational databases p491
;; * ch15 using non-relational databases p511
;; * ch16 clojure and the web p527
;; * ch17 deploying clojure web applications p557
;; * ch18 choosing clojure type definition forms wisely p573
;; * ch19 introducing clojure into your workplace p577
;; * ch20 what's next?
