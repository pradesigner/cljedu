"
clojure.org Learn Clojure guide
https://clojure.org/guides/learn/clojure
"

(ns user
  (:require [clojure.string :as s]
            [pradagio.person-names]))

;; * Getting Started
;; How do I learn the language?
;; 4Clojure - a large set of Clojure practice problems
;; exercism Clojure track - larger problems with mentor support
;; Other learning resources for more options
;; Ask Clojure qa knowledgebase (joined)
;; clojurians forum (joined)
;; clojureverse forum (joined)
;; * Structural Editing
;; What is structural editing?
;; - examine Clojure code, we note that it can be viewed as a nested set of forms
;; - focus on memorizing a small set of commands: kill, slurp forward, barf forward, splice, and raise
;; Balanced forms
;; Navigation and selection
;; -  Instead of selecting the next word, you can select the next expression or parent expression
;; Cut/paste aka kill/yank
;; - most structural editors will cut from the current location to the end of the current collection
;; Slurping and barfing
;; - slurps the expression after the current collection into the collection as the last item
;; - barf moves the last item of the collection outside
;; Splicing and raising
;; - splicing drops the items before or after into parent
;; - raising replaces the parent with only the value of the element at the cursor
;; Parinfer
;; - replacement for paredit
;; Resources
;; - Smartparens for Spacemacs: https://practical.li/spacemacs/structural-editing/
;; * Programming at the REPL
;; https://clojure.org/guides/repl/introduction
;; ** Introduction
;; *** What is a REPL?
;; *** Why use a REPL?
;; *** What this guide will cover

;; ** Launching a Basic REPL
;; *** Using the CLI tools
;; *** Using a project management tool
;; *** Using Java and the Clojure JAR

;; ** Basic Usage
;; *** Evaluating Clojure expressions
(defn factoria [n]
  (if (= n 0)
    1
    (* n (factoria (dec n)))))
(factoria 10);; => 3628800

;; *** The 2 flavors of printing
(println "helo world");; => "helo world" nil

;; ** Calling Clojure libs from the REPL
(s/upper-case "clojure");; => "CLOJURE" because of (:require)

;; ** Looking up documentation (in repl)

;; Documentation is available only for libs that have been required.

(doc nil?);; => nil
;; clojure.core/nil?
;; ([x])
;;   Returns true if x is nil, false otherwise.

(source nil?);; => nil
;; (defn nil?
;;   "Returns true if x is nil, false otherwise."
;;   {:tag Boolean
;;    :added "1.0"
;;    :static true
;;    :inline (fn [x] (list 'clojure.lang.Util/identical x nil))}
;;   [x] (clojure.lang.Util/identical x nil))

(dir user);; => nil
;; factoria

(apropos "apropos")
;; => (cider.nrepl/wrap-apropos clojure.repl/apropos)

(find-doc "indexed");; => nil
;; finds all docstr with indexed in it

;; ** Data Visualization

;; *** Pretty-printing using clojure.pprint
(defn number-summary
  "Computes a summary of the arithmetic properties of a number, as a data structure."
  [n]
  (let [proper-divisors (into (sorted-set)
                          (filter
                            (fn [d]
                              (zero? (rem n d)))
                            (range 1 n)))
        divisors-sum (apply + proper-divisors)]
    {:n n
     :proper-divisors proper-divisors
     :even? (even? n)
     :prime? (= proper-divisors #{1})
     :perfect-number? (= divisors-sum n)}))

(mapv number-summary [5 6 7 12 28 42])
;; => [{:n 5, :proper-divisors #{1}, :even? false, :prime? true, :perfect-number? false} {:n 6, :proper-divisors #{1 2 3}, :even? true, :prime? false, :perfect-number? true} {:n 7, :proper-divisors #{1}, :even? false, :prime? true, :perfect-number? false} {:n 12, :proper-divisors #{1 2 3 4 6}, :even? true, :prime? false, :perfect-number? false} {:n 28, :proper-divisors #{1 2 4 7 14}, :even? true, :prime? false, :perfect-number? true} {:n 42, :proper-divisors #{1 2 3 6 7 14 21}, :even? true, :prime? false, :perfect-number? false}]

(clojure.pprint/pprint
 (mapv number-summary [5 6]));; => nil
[{:n 5,
  :proper-divisors #{1},
  :even? false,
  :prime? true,
  :perfect-number? false}
 {:n 6,
  :proper-divisors #{1 2 3},
  :even? true,
  :prime? false,
  :perfect-number? true}]

(clojure.pprint/print-table
 (mapv number-summary [6 12 28]));; => nil
| :n | :proper-divisors | :even? | :prime? | :perfect-number? |
|----+------------------+--------+---------+------------------|
|  6 |         #{1 2 3} |   true |   false |             true |
| 12 |     #{1 2 3 4 6} |   true |   false |            false |
| 28 |    #{1 2 4 7 14} |   true |   false |             true |

;; *** Truncating REPL output
;; (set! *print-level* 3)
;; (set! *print-level* nil)
;; *** Accessing recent results: *1, *2, *3 (def <some-name> *1)
;; *** Investigating Exceptions
stacktrace (pst *e)
*e
Exceptions convey data
Exceptions are chained

(defn divide-verbose
  "Divides two numbers `x` and `y`, but throws more informative Exceptions when it goes wrong.
  Returns a (double-precision) floating-point number."
  [x y]
  (try
    (double (/ x y))
    (catch Throwable cause
      (throw
        (ex-info
          (str "Failed to divide " (pr-str x) " by " (pr-str y))
          {:numerator x
           :denominator y}
          cause)))))

(defn average
  "Computes the average of a collection of numbers."
  [numbers]
  (try
    (let [sum (apply + numbers)
          cardinality (count numbers)]
      (divide-verbose sum cardinality))
    (catch Throwable cause
      (throw
        (ex-info
          "Failed to compute the average of numbers"
          {:numbers numbers}
          cause)))))

;; *** Graphical and web-based visualizations
(clojure.java.javadoc/javadoc #"a+") ;;lets you view the Javadoc of a class or object
C-c C-d C-j

(clojure.inspector/inspect-table (mapv number-summary [2 5 6 28 42]))?? didn't work

;; *** Dealing with mysterious values (advanced)
;; Viewing the type hierarchy using type and ancestors
;; Using Javadoc
;; Inspecting Java types with clojure.reflect

;; ** Navigating Namespaces
;; REPL is most valuable for putting yourself in the shoes of the program you are developing by giving your REPL the same context as your running program, which implies using REPL in the same namespaces where your code is defined.

;; *** The current namespace
;; if the current namespace is myapp.foo.bar and you evaluate (require [clojure.set :as cset :refer [union]]), you can now refer to the clojure.set/union Var either by cset/union (because of the :as cset alias) or just union because of :refer [union]

;; *** Creating a namespace with ns
;; You can create and switch to a new namespace by evaluating (ns MY-NAMESPACE-NAME)

;; *** Switching to an existing namespace with in-ns
;; You can switch to an existing namespace by evaluating (in-ns 'MY-NAMESPACE-NAME).

;; *** Working with libs
;; If a namespace is defined in a lib of your project, always make sure you have loaded the lib in the REPL before switching to it:
;; - require it directly: (require '[mylib.ns1])
;; - load a namespace which itself requires mylib.ns1 (directly or indirectly).
;; - evaluate manually all the code in the source file mylib.ns1

;; myproject example
pradagio.person-names/nicknames ;shows up in the repl, but not here??
(list pradagio.person-names/nicknames)
;; => ({"Robert" "Bob", "Abigail" "Abbie", "William" "Bill", "Jacqueline" "Jackie"})
;; probably because the repl makes a list by default


;; ** Enhancing your REPL workflow
;; REPL integration in editor
;; ergonomic terminal REPL clients: rebel-readline, Unravel
;; a 'dev' namespace (e.g myproject.dev) where you define functions for automating common development tasks
;; notebook format: Gorilla REPL

;; better data Visualization with special purpose tools
;; - oz: https://github.com/metasoarous/oz
;; - reveal: https://vlaaad.github.io/reveal/
;; - REBL: https://docs.datomic.com/cloud/other-tools/REBL.html
;; - datawalk: https://github.com/eggsyntax/datawalk
;; - system-viz: https://github.com/walmartlabs/system-viz

;; *** Editor integrations
;; What to expect from an in-editor REPL integration?

;; *** Debugging tools and techniques
;; Printing in-flight values with prn
;; 'spying' macros
;; tracing libraries

;; Intercepting and saving values on-the-fly

(defn average
  "a buggy function for computing the average of some numbers."
  [numbers]
  (let [sum (first numbers)
        n (count numbers)]
    (prn sum) ;; HERE printing an intermediary value
    (/ sum n)))
(average [1 2 3])

(defn average1
  "a buggy function for computing the average of some numbers."
  [numbers]
  (let [sum (first numbers)
        n (count numbers)]
    (/
      (doto sum prn) ;; HERE
      n)))
(average1 [1 2 3])

(defn average2
  [numbers]
  (let [sum (apply + numbers)
        n (count numbers)]
    (def n n) ;; FIXME remove when you're done debugging
    (/ sum n)))
(average2 [1 2 3])
;; => 2
n;; => 3

;; Reproducing the context of an expression
;; reproduce manually something that our program did automatically

;; Inline def: an effective* debugging technique for Clojure
;; https://blog.michielborkent.nl/inline-def-debugging.html

;; REPL Debugging: No Stacktrace Required
;; https://cognitect.com/blog/2017/6/5/repl-debugging-no-stacktrace-required

;; Community resources about REPL debugging
;; - The Clojure Toolbox provides a list a Clojure libraries for debugging.
;; - The Power of Clojure: a list of techniques for debugging at the REPL.
;; - Clojure From the Ground Up by Aphyr: a principled approach to debugging in general.
;; - REPL Debugging: No Stacktrace Required: Stuart Halloway demonstrates the quick feedback loop
;; - Eli Bendersky has written some Notes on debugging Clojure code.
;; - Debugging with the Scientific Method: conference talk by Stuart Halloway

;; ** Writing REPL-friendly programs
;; REPL-friendly code can be re-defined.
;; Beware of derived Vars.

;; ** Guidelines for REPL-Aided Development
;; Clojure REPLs are used for a broad spectrum of purposes, from learning the language to data exploration to live music performance.

;; *** The REPL is a User Interface to your program
;; *** Don’t get carried away by the REPL
;; rapid feedback is no substitute for software design and methodic problem-solving
;; *** Don’t forget to save your work, and make it accessible
;; *** The REPL is not the only tool for interactive development
;; - auto-reloading test suites (example: Midje)
;; - static code analysis tools (linters, static type checkers)
;; - hot-code reloading (example: Figwheel)
;; - 'visual' test suites (example: Devcards)

;; ** Community resources about the REPL
;; - In Running with Scissors: Live Coding with Data (slides here), Stuart Halloway presents his workflow for solving problems with the REPL, including little-known opportunities offered by the Clojure REPL without 3rd-party tools (such as custom reading). "You’re living in your program invoking your tools, instead of living in your tools invoking your program".
;; - The Ultimate Guide to Clojure REPLs on Lambda Island: this tutorial notably explores the wide variety of Clojure REPLs and related technology.
;; - Stuart Halloway on REPL Driven Development: a talk which reflects on the added value of REPLs compared to shells, and provides guidelines for using the Clojure REPL.
;; - Share the nitty-gritty details of your Clojure workflow: a ClojureVerse discussion where community members describe their development configurations and how they use it them in details.
;; - What makes a good REPL?: this blog post provides a general reflection on the benefits of REPLs, and the programming language features which enable them.
;; - REPL-Driven Development: Clojure’s Superpower: Sean Corfield at the London Clojurians online meetup, talking about why Clojure’s REPL is so powerful, including a live, 40 minute demo, building a simple web app via the REPL (starting at about 11 minutes in).
;; - REPL-Based Development Demo: a video showing how the REPL is used for development on an example project.
;; - The Seesaw REPL Tutorial teaches you to build Graphical User Interfaces at the Clojure REPL.

;; ** Troubleshooting
;; *** Calling a Var that has not been defined
;; *** Using a missing namespace alias
;; *** Referring to a namespace that has not been loaded
;; *** Trying to require a namespace that does not exist
;; * Learn Clojure
;; ** Syntax
;; Literals
;; Evaluation
;; REPL
;; - (require '[clojure.repl :refer :all]) provides some useful items like
;; - doc, find-doc, apropos, source, and dir
;; Clojure basics
;; ** Functions
;; - Creating Functions: multiarity, variadic, anonymous
;; - gotcha: #([%]) instead do: #(vector %) or (fn [x] [x])
;; - Applying Functions: useful when arguments are a sequence but function invoked with values of sequence
;; - Locals and Closures??: let, fn
;; - Java Interop
(defn greeting
  ([] (greeting "Hello" "World"))
  ([x] (greeting "Hello" x))
  ([x y] (str x ", " y "!")))

(defn do-nothing
  [x]
  x)

(defn always-thing
  [& xs]
  100)

(defn make-thingy
  [x]
  (fn [& ys] x))

(defn triplicate
  [f]
  (f) (f) (f))

(defn opposite
  [f]
  (fn [& args] (not (apply f args))))

(assert (= -1.0 (Math/cos Math/PI)))
(assert (= 1.0 (+ (Math/pow (Math/sin Math/PI) 2) (Math/pow (Math/cos Math/PI) 2))))

(defn http-get
  [url]
  (slurp
   (.openStream
    (java.net.URL. url))))

(defn one-less-arg
  [f x]
  (fn [& args]
    (apply f x args))) ;??

(defn two-fns
  [f g]
  ;; (comp f g)
  (fn [x]
    (f (g x))))

;; ** Sequential Collections
;; Ordered values into compound values
;; *** Vectors
;; - Indexed access: (get v i), (count v)
;; - Constructing: [], (vector i1 i2 ...)
;; - Adding elements: (conj v i1 i2 ...)
;; - Immutability: (conj v 1 2) doesn't change v
;; *** Lists
;; - Constructing: must quote and use first rest
;; - Adding elements: conj
;; - Stack access: peek pop
;; ** Hashed Collections
;; - Sets: #{}, conj, disj, contains?, into
;; - Maps: {}, assoc, dissoc, get, contains?, find, keys, vals, zipmap, into, reduce, merge, merge-with
;;   - keywords are also functions (with optional default value)
;;   - update item with assoc
;;   - nest and get-in, assoc-in (or update-in)
;; - Records: defrecord - positional and map constructor
;; ** Flow Control
;; *** Statements vs. Expressions
;; - everything is an expression
;; - block of multiple expressions returns the last value
;; *** Flow Control Expressions
;; - Flow control operators are composable
;; - Flow control operators are also extensible via macros
(str "2 is " (if (even? 2) "even" "odd")) ;; => "2 is even"
(if false "true" "false") ;; => "false"
(if nil "true" "false") ;; => "false"
(if 0 :truthy :falsey) ;; => :truthy
;; - (do ...)
;; - (when ...) is an if with only a then branch
;; - (cond ...) return nil if no match
;; - (case ...) will throw exception if no match or trailing expression
;; *** Iteration for Side Effects
;; - (dotimes [i N] ...)
;; - (doseq [i (range N)] ...) and can use multiple bindings like nested foreach
;; *** Clojure’s for
;; - (for ) like doseq, but returns list
;; *** Recursion
;; - loop ... recur but function arguments are implicit loop bindings
(loop [i 0]
  (if (< i 10)
    (recur (inc i))
    i)) ;; => 10
(defn increase
  [i]
  (if (< i 10)
    (recur (inc i))
    i))
(increase 0) ;; => 10
;; *** Exceptions
;; - try/catch/finally
;; - ex-info, ex-data
;; - with-open provides a concise write
;; ** Namespaces
;; *** Namespaces and names
;; - Namespaces provide a means to organize our code and the names we use in our code.
;; - All vars are globally accessible via their fully-qualified name.
;; *** Declaring namespaces
;; - The ns macro specifies the namespace name matching the file path location
;; - all of the clojure.core library vars have been referred into the current namespace
;; - :require specifies namespaces to load that current namespace depends on
;; - :as Aliases let us use shorter versions of longer fully-qualified aliases.
;; - :refer Refer allows us to use names without a namespace qualifier 
;; * Language
;; ** spec Guide
;; *** Getting started
;; *** Predicates
;; *** Registry
;; *** Composing predicates
;; *** Explain
;; *** Entity Maps
;; *** multi-spec
;; *** Collections
;; *** Sequences
;; *** Using spec for validation
;; *** Spec’ing functions
;; *** Higher order functions
;; *** Macros
;; *** A game of cards
;; *** Generators
;; *** Instrumentation and Testing
;; *** Wrapping Up
;; *** More information
;; ** Reading Clojure Characters
;; ** Destructuring
;; ** Threading Macros
;; ** Equality
;; ** Comparators
;; ** Reader Conditionals
;; ** Higher Order Functions
;; * Usage
;; ** Dev Startup Time
;; * Tools
;; ** Deps and CLI
;; *** Running a REPL and using libraries
;; % clj -X:deps find-versions :lib clojure.java-time/clojure.java-time
;; *** Writing a program
;; *** Using a main
;; *** Using local libraries
;; *** Using git libraries
;; *** Include a test source directory
;; *** Use a test runner to run all tests
;; *** Prep source dependency libs
;; *** Add an optional dependency
;; *** Use a local jar on disk
;; *** Ahead-of-time (AOT) compilation
;; *** Run a socket server remote repl
;; ** tools.build
;; * Libraries
;; ** Go Block 
;; ** test.check



