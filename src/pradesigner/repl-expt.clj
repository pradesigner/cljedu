;; Using the REPL to experiment from joc

;; * Experimenting with seqs
;; Say someone suggests to you that coloring every pixel of a canvas
;; with the xor of its xand  y  coordinates  might  produce  an  interesting  image.

(find-doc "xor") gives
-------------------------
clojure.core/bit-xor
([x y] [x y & more])
  Bitwise exclusive or

(bit-xor 1 2) ;; => 3
(Integer/toBinaryString (bit-xor 1 2)) ;; => "11"

(for [x (range 2)
      y (range 2)]
  [x y (bit-xor x y)])
;; => ([0 0 0] [0 1 1] [1 0 1] [1 1 0])

(defn xors
  "gives xor of x y"
  [max-x max-y]
  (for [x (range max-x)
        y (range max-y)]
    [x y (bit-xor x y)]))

;; * Experimenting with graphics
