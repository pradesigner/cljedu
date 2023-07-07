;;;; ch1 ;;;;

(defn sq
  "calculates squares of numbers"
  [x]
  (* x x))

(defn ss1
  "calculates sum of squares with an accumulator"
  [res y]
  (if (empty? y);(= 0 (count y))
    res
    (ss1 (+ res (sq (first y))) (rest y)))
)
(ss1 0 [1 2 3])
;; => 14

(defn ss2
  "calculates sum of squares with an accumulator"
  [y]
  (loop [res 0
         cnt (count y)]
    (if (= 0 cnt)
      res
      ;;(recur (+ res (sq (first y))) (rest y))
      (recur (+ res (sq (nth y (dec cnt))))
             (dec cnt))
      )))
(ss2 [1 2 3])
;; => 14


;; Exercise 1.3.
(defn sos [x y z]
  (let [[a b] (remove #(= % (min x y z)) [x y z])]
    (+ (sq a) (sq b))))
(sos 1 2 3)
;; => 13

;; Exercise 1.5.
(defn p [] (p))
(defn test [x y]
  (if (= x 0)
    0
    y))
(test 0 (p)) ;overflow, therefore trying to (p) and hence applicative


(defn good-enough?
  [guess x]
  (< (Math/abs (- (sq guess) x)) ;Math/abs can't handle fraction
     0.001))
(defn improved
  [guess x]
  (/ (+ guess (/ x guess))
     2))
(defn sqrt-iter
  ([guess x] ;use recursion
   (if (good-enough? guess x)
     guess
     (sqrt-iter (improved guess x)
                x)))
  ([guess x dud] ;using loop-recur
   (loop [g guess]
     (if (good-enough? g x)
       g
       (recur (improved g x))))))
(sqrt-iter 1 2.0)
;; => 1.4142156862745097
(sqrt-iter 1 2.0 :dud)
;; => 1.4142156862745097



;; Exercise 1.6.
(defn afunc-cond
  [a b]
  (cond
    (= a 0) b
    :else (afunc a b)))
                                        ;ok

(defn afunc-if
  [a b]
  (if (= a 0)
    b
    (afunc a b)))
                                        ;ok

(defn new-if-with-cond
  [pred then-clause else-clause]
  (cond
    pred then-clause
    :else else-clause))
(defn new-if-with-if
  [pred then-clause else-clause]
  (if pred
    then-clause
    else-clause))
(defn afunc
  [a b]
  (new-if-with-cond (= a 0)
    b
    (afunc a b)))
                                        ;stackoverflow with new-if-with-if and new-if-with-cond

;; in the applicative order mechanism the parameters are evaluated before being passed to other functions
;; therefore the new-ifs try to evaluate parameter else-clause which is the recursive call that doesn't end


;; Exercise 1.7.

(defn good-enough?
  [guess x]
  (< (Math/abs (- (sq guess) x)) ;Math/abs can't handle fraction
     0.001))
(defn improved
  [guess x]
  (/ (+ guess (/ x guess))
     2))
(defn sqrt-iter
  ([guess x] ;use recursion
   (if (good-enough? guess x)
     guess
     (sqrt-iter (improved guess x)
                x))))

(sqrt-iter 1.0 4)
;; => 2.0000000929222947

(sqrt-iter 1 0.1)
;; => 0.316245562280389
;; (* 0.316245562280389 0.316245562280389) => 0.10001125566203942

(sqrt-iter 1 0.01)
;; => 0.10032578510960605
;; (* 0.10032578510960605 0.10032578510960605) => 0.01006526315785885

(defn test-sqrt-iter
  "test values for sqrt-iter and gives errors"
  []
  (loop [itm 0.1
         sqrt (sqrt-iter 1 itm)
         err (* 100 (/ (- (sq sqrt) itm) itm))]
    (printf "for itm %f, sqrt = %f, sqrt^2 = %f, err = %f%% %n" itm sqrt (sq sqrt) err)
    (if (< itm 0.00001)
      "done"
      (recur (/ itm 10)
             (sqrt-iter 1 itm)
             (* 100 (/ (- (sq sqrt) itm) itm))))))

(defn test-sqrt-iter
  "test values for sqrt-iter and gives errors"
  []
  (loop [itm 0.1]
    (let [sqrt (sqrt-iter 1.0 itm)
          err (* 100 (/ (- (sq sqrt) itm) itm))]
      (printf "for itm %f, sqrt = %f, diff = %f, err = %f%% %n"
              itm sqrt (- (sq sqrt) itm) err)
      (if (< itm 0.00001)
        "done"
        (recur (/ itm 10))))))
"
but for 10000000000000.000000 we get Execution error (StackOverflowError) at cljings.core/good-enough?
for itm 1000000000000.000000, sqrt = 1000000.000000, diff = 0.000000, err = 0.000000% 
for itm 100000000000.000000, sqrt = 316227.766017, diff = -0.000015, err = -0.000000% 
for itm 10000000000.000000, sqrt = 100000.000000, diff = 0.000000, err = 0.000000% 
for itm 1000000000.000000, sqrt = 31622.776602, diff = -0.000000, err = -0.000000% 
for itm 100000000.000000, sqrt = 10000.000000, diff = 0.000002, err = 0.000000% 
for itm 10000000.000000, sqrt = 3162.277660, diff = 0.000225, err = 0.000000% 
for itm 1000000.000000, sqrt = 1000.000000, diff = 0.000000, err = 0.000000% 
for itm 100000.000000, sqrt = 316.227766, diff = 0.000000, err = 0.000000% 
for itm 10000.000000, sqrt = 100.000000, diff = 0.000000, err = 0.000000% 
for itm 1000.000000, sqrt = 31.622777, diff = 0.000003, err = 0.000000% 
for itm 100.000000, sqrt = 10.000000, diff = 0.000000, err = 0.000000% 
for itm 10.000000, sqrt = 3.162278, diff = 0.000000, err = 0.000000% 
for itm 1.000000, sqrt = 1.000005, diff = 0.000011, err = 0.001058% 
for itm 0.100000, sqrt = 0.316420, diff = 0.000122, err = 0.121717% 
for itm 0.010000, sqrt = 0.101202, diff = 0.000242, err = 2.418820% 
for itm 0.001000, sqrt = 0.000100, diff = -0.001000, err = -99.999000% 
for itm 0.000100, sqrt = 0.000010, diff = -0.000100, err = -99.999900% 
for itm 0.000010, sqrt = 0.000001, diff = -0.000010, err = -99.999990% 
for itm 0.000001, sqrt = 0.000000, diff = -0.000001, err = -99.999999% 
the errors blow up as we go below 0.1 choosing the start to be 1/10

or does the following if we always start at 1
for itm 0.100000, sqrt = 0.316246, diff = 0.000011, err = 0.011256% 
for itm 0.010000, sqrt = 0.100326, diff = 0.000065, err = 0.652632% 
for itm 0.001000, sqrt = 0.041245, diff = 0.000701, err = 70.118517% 
for itm 0.000100, sqrt = 0.032308, diff = 0.000944, err = 943.835834% 
for itm 0.000010, sqrt = 0.031356, diff = 0.000973, err = 9732.294719% 
for itm 0.000001, sqrt = 0.031261, diff = 0.000976, err = 97622.858388%

so it's rather unstable.
"

