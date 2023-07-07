"
tablecloth examples
https://scicloj.github.io/tablecloth/index.html
"

(ns user
  (:require [tablecloth.api :as api]))

;; * Introduction

(def DS (api/dataset {:V1 (take 9 (cycle [1 2]))
                      :V2 (range 1 10)
                      :V3 (take 9 (cycle [0.5 1.0 1.5]))
                      :V4 (take 9 (cycle ["A" "B" "C"]))}))

DS
;; => _unnamed [9 4]:
| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |


;; * Dataset
;; ** Dataset creation
(api/dataset)
(api/dataset 777)
(api/dataset {:single-value 777})
(api/dataset {"single-value" 777})

(api/dataset 777 {:single-value-column-name "single-value"
                  :dataset-name "Single Value"}) ;;; => Single Value [1 1]:

| single-value |
|--------------|
|          777 |

(api/dataset [[:A 77]
              [:B 565]])
;; => _unnamed [1 2]:

| :A |  :B |
|----|-----|
| 77 | 565 |

(api/dataset {:A [1 2 3]
              :B "X"
              :C :b})
(api/dataset [[:A [1 2 3]]
              [:B "X"]
              [:C :b]])
;; => _unnamed [3 3]:

| :A | :B | :C |
|----|----|----|
|  1 |  X | :b |
|  2 |  X | :b |
|  3 |  X | :b |


(api/dataset {:A [[3 4 5] [:a :b]]
              :B "X"})
;; => _unnamed [2 2]:

|      :A | :B |
|---------|----|
| [3 4 5] |  X |
| [:a :b] |  X |


(api/dataset [{:a 1 :b 3}
              {:b 7 :a 77}])
;; => _unnamed [2 2]:

| :a | :b |
|----|----|
|  1 |  3 |
| 77 |  7 |
                                        ;but not
(api/dataset {:a 1 :b 3 :b 7 :a 77}) ;duplicate key error

(api/dataset [{:a nil :b 1}
              {:a 3 :b 4}
              {:a 11}])
;; => _unnamed [3 2]:

| :a | :b |
|----|----|
|    |  1 |
|  3 |  4 |
| 11 |    |


;; TODO these don't work may be because of version?
(-> (map int-array [[1 2] [3 4] [5 6]])
    (into-array)
    (api/dataset))

(-> (map int-array [[1 2] [3 4] [5 6]])
    (into-array)
    (api/dataset {:layout :as-rows
                  :column-names [:a :b]}))
;;ERR nth not supported on this type: Integer


;; Import from URL
(defonce cw (api/dataset "https://vega.github.io/vega-lite/examples/data/seattle-weather.csv"))
(api/head cw)


;; ** Saving
(api/dataset "resources/ZZ.txt")
(api/dataset "resources/ZZ.tsv")

(api/write-csv! cw "resources/cw.tsv.gz")
(api/dataset "resources/cw.tsv.gz")

;; these work now
(api/write-nippy! cw "resources/cw.nippy.gz")
(api/read-nippy "resources/cw.nippy.gz") ;;TODO
(api/dataset "resources/cw.nippy.gz") ;;TODO
(api/write-nippy! DS "resources/DS.nippy.gz")

;; these work so tablecloth issue may be a version 4.04 one
(tech.io/put-nippy! "resources/cw1.nippy.gz" cw)
(tech.io/get-nippy "resources/cw1.nippy.gz" cw)

;; ** dataset properties
(api/row-count cw)
;; => 1461
(api/column-count cw)
;; => 6
(api/shape cw)
;; => [1461 6]

(api/info cw)
(api/info cw :basic)
(api/info cw :columns)
(api/dataset-name cw)

(->> "seattle-weather"
     (api/set-dataset-name cw)
     (api/dataset-name))
;;TODO doesn't seem to actually set the ds name since the weblink shows up - just copies it immutably

;; ** columns and rows
(api/column-names cw)
;; => ("date" "precipitation" "temp_max" "temp_min" "wind" "weather")
(cw "wind")
(api/column cw "wind")

(take 2 (api/columns cw)) ;;shows first 2 columns

(keys (api/columns cw :as-map))
;; => ("date" "precipitation" "temp_max" "temp_min" "wind" "weather")

(take 2 (api/rows cw))
;; => ([#object[java.time.LocalDate 0x4ad5992 "2012-01-01"] 0.0 12.8 5.0 4.7 "drizzle"] [#object[java.time.LocalDate 0x32b228fa "2012-01-02"] 10.9 10.6 2.8 4.5 "rain"])

(-> cw
    (api/select-columns :type/numerical)
    (api/head)
    (api/rows :as-double-arrays))
(-> cw
    (api/select-columns :type/numerical)
    (api/head)
    (api/columns :as-double-arrays))


;; * Group-by
"Grouping by is an operation which splits dataset into subdatasets and pack it into new special type of… dataset."
:name
:group-id
:data
;; ** Grouping

(def DS (api/read-nippy "resources/DS.nippy.gz"))
DS_unnamed [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |


(api/group-by DS :V1)
;; => _unnamed [2 3]:

| :name | :group-id |           :data |
|-------|-----------|-----------------|
|     1 |         0 | Group: 1 [5 4]: |
|     2 |         1 | Group: 2 [4 4]: |


(api/print-dataset (api/group-by DS :V1) {:print-line-policy :markdown})
(api/print-dataset (api/group-by DS :V1) {:print-line-policy :repl})
(api/print-dataset (api/group-by DS :V1) {:print-line-policy :single})


(-> DS
    (api/group-by :V1)
    (api/column-names))
;; => (:V1 :V2 :V3 :V4)

(-> DS
    (api/group-by :V1)
    (api/as-regular-dataset)
    (api/column-names))
;; => (:name :group-id :data)


;; Content of the grouped dataset

(-> DS
    (api/group-by :V1)
    (api/columns :as-map))
;; =>
{
:group-id #tech.v3.dataset.column<int64>[2]
:group-id [0, 1],
:name #tech.v3.dataset.column<int64>[2]
:name [1, 2],
:data #tech.v3.dataset.column<dataset>[2]
:data
[Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
, Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
]}


;; Grouped dataset as map
(keys (api/group-by DS :V1 {:result-type :as-map}))
;; => (1 2)
(vals (api/group-by DS :V1 {:result-type :as-map}))
;; =>
(Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
 Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
)


(api/concat DS
            (-> DS
                (api/aggregate-columns [:V2] #(reduce + %))
                (api/add-columns {:V1 nil
                                  :V3 nil
                                  :V4 nil})))
;; => null [10 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |
|     |  45 |     |     |


(api/bind DS
          (-> DS (api/aggregate-columns [:V2] #(reduce + %))))
;; => null [10 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |
|     |  45 |     |     |

"so i guess a way to look at it is that the summation is a separate dataset and bind puts them together by column, whereas concat just sticks things together and hence require the blank areas to be filled up with nil."



(-> DS
    (api/group-by :V1))
;; => _unnamed [2 3]:

| :group-id | :name |           :data |
|----------:|------:|-----------------|
|         0 |     1 | Group: 1 [5 4]: |
|         1 |     2 | Group: 2 [4 4]: |


(-> DS
    (api/group-by :V1 {:result-type :as-map}))
;; => {1 Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
, 2 Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
}


(-> DS
    (api/group-by :V1)
    (api/aggregate-columns [:V2] #(reduce + %))
    )
;; => _unnamed [2 2]:

| :V2 | :$group-name |
|----:|-------------:|
|  25 |            1 |
|  20 |            2 |


(api/aggregate-columns (api/group-by DS :V1)
                       :V2
                       #(reduce + %))




(map #(api/aggregate-columns DS :V2 #(reduce + %)) (api/group-by DS :V1 {:result-type :as-seq}))


(api/bind DS
 (-> DS
     (api/group-by :V1)
     (api/aggregate-columns [:V2] #(reduce + %))
     ))
;; => null [11 5]:

| :V1 | :V2 | :V3 | :V4 | :$group-name |
|----:|----:|----:|-----|-------------:|
|   1 |   1 | 0.5 |   A |              |
|   2 |   2 | 1.0 |   B |              |
|   1 |   3 | 1.5 |   C |              |
|   2 |   4 | 0.5 |   A |              |
|   1 |   5 | 1.0 |   B |              |
|   2 |   6 | 1.5 |   C |              |
|   1 |   7 | 0.5 |   A |              |
|   2 |   8 | 1.0 |   B |              |
|   1 |   9 | 1.5 |   C |              |
|     |  25 |     |     |            1 |
|     |  20 |     |     |            2 |



(map (fn [x]
       (api/bind x
                 (api/aggregate-columns x [:V2] #(reduce + %))))
     (api/group-by DS :V1 {:result-type :as-seq}))
;; => (null [6 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
|     |  25 |     |     |
 null [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
|     |  20 |     |     |
)

(defn get-group-totals
  "from dataset produces totals of columns to sum given grouping columns"
  [ds group-by-cols cols-to-sum]
  (map (fn [x]
       (api/bind x
                 (api/aggregate-columns x
                                        cols-to-sum
                                        #(reduce + %))))
       (api/group-by ds
                     group-by-cols
                     {:result-type :as-seq})))

(get-group-totals DS :V1 [:V2])
;; => (null [6 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
|     |  25 |     |     |
 null [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
|     |  20 |     |     |
)


(get-group-totals DS [:V1 :V4] [:V2 :V3])
;; =>
(null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
|     |   8 | 1.0 |     |
 null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
|     |  10 | 2.0 |     |
 null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   3 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
|     |  12 | 3.0 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   4 | 0.5 |   A |
|     |   4 | 0.5 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   5 | 1.0 |   B |
|     |   5 | 1.0 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   6 | 1.5 |   C |
|     |   6 | 1.5 |     |
)


(defn get-group-tots
  "a cleaner version of get-group-totals"
  [ds group-by-cols cols-to-sum]
  (let [sumfn #(reduce + %)
        tfn (fn [x]
              (api/bind x
                        (api/aggregate-columns x
                                               cols-to-sum
                                               sumfn)))
        g-ds (api/group-by ds
                           group-by-cols
                           {:result-type :as-seq})]
    (map tfn g-ds)))



;; daniel slutsky solution
(require '[tech.v3.datatype.functional :as dtype-fun])

(-> DS
    (api/group-by :V1)
    (api/process-group-data (fn [x]
                              (api/bind x
                                        (api/aggregate-columns x [:V2] dtype-fun/sum))))
    api/ungroup)
;; => _unnamed [11 4]:

| :V1 |  :V2 | :V3 | :V4 |
|----:|-----:|----:|-----|
|   1 |  1.0 | 0.5 |   A |
|   1 |  3.0 | 1.5 |   C |
|   1 |  5.0 | 1.0 |   B |
|   1 |  7.0 | 0.5 |   A |
|   1 |  9.0 | 1.5 |   C |
|     | 25.0 |     |     |
|   2 |  2.0 | 1.0 |   B |
|   2 |  4.0 | 0.5 |   A |
|   2 |  6.0 | 1.5 |   C |
|   2 |  8.0 | 1.0 |   B |
|     | 20.0 |     |     |


(defn sum-group-totals
  "from dataset produces totals of columns to sum given grouping columns"
  [ds group-by-cols cols-to-sum]
  (-> ds
      (api/group-by group-by-cols)
      (api/process-group-data
       (fn [x]
         (api/bind x
                   (api/aggregate-columns x
                                          cols-to-sum
                                          dtype-fun/sum))))
      (api/ungroup)))

(sum-group-totals DS [:V1 :V4] [:V2 :V3])
;; => _unnamed [15 4]:

| :V1 |  :V2 | :V3 | :V4 |
|----:|-----:|----:|-----|
|   1 |  1.0 | 0.5 |   A |
|   1 |  7.0 | 0.5 |   A |
|     |  8.0 | 1.0 |     |
|   2 |  2.0 | 1.0 |   B |
|   2 |  8.0 | 1.0 |   B |
|     | 10.0 | 2.0 |     |
|   1 |  3.0 | 1.5 |   C |
|   1 |  9.0 | 1.5 |   C |
|     | 12.0 | 3.0 |     |
|   2 |  4.0 | 0.5 |   A |
|     |  4.0 | 0.5 |     |
|   1 |  5.0 | 1.0 |   B |
|     |  5.0 | 1.0 |     |
|   2 |  6.0 | 1.5 |   C |
|     |  6.0 | 1.5 |     |


;; get totals of totals
(-> (get-group-totals DS [:V1 :V4] [:V2 :V3]) ;pulls stuff out
    first
    api/rows
    last
    second)

(-> (get-group-totals DS [:V1 :V4] [:V2 :V3]) ;pulls stuff out
    first
    :V2
    last)


(defn get-group-tots
  "a cleaner version of get-group-totals"
  [ds group-by-cols cols-to-sum]
  (let [;;the summing function
        sumfn #(reduce + %)

        ;;the grouped ds
        g-ds (api/group-by ds
                           group-by-cols
                           {:result-type :as-seq})

        ;;the group totals for each group
        group-sums (for [g g-ds]
                     (api/aggregate-columns g
                                            cols-to-sum
                                            sumfn))

        ;;the group totals bound together into single dataset 
        summed-groups (apply api/bind group-sums)

        ;;the total of the summed-groups dataset
        group-totals (api/aggregate-columns summed-groups
                                            cols-to-sum
                                            sumfn)
        
        ;;the column sums attached to each group
        column-sums (map api/bind g-ds group-sums)]

    ;;vector containing grouped dataset with
    ;;column sums and the column sums total
    [group-totals column-sums]
    ))

(get-group-tots DS [:V1 :V4] [:V2 :V3])
;; =>
[
(null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
|     |   8 | 1.0 |     |
 null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
|     |  10 | 2.0 |     |
 null [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   3 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
|     |  12 | 3.0 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   4 | 0.5 |   A |
|     |   4 | 0.5 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   5 | 1.0 |   B |
|     |   5 | 1.0 |     |
 null [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   6 | 1.5 |   C |
|     |   6 | 1.5 |     |
)


_unnamed [1 2]:

| :V2 | :V3 |
|----:|----:|
|  45 | 9.0 |
]



;; Group dataset as map of indexes (row ids)
(api/group-by DS :V1 {:result-type :as-indexes})
;; => {1 [0 2 4 6 8], 2 [1 3 5 7]}

;; Grouped datasets are printed as follows by default.
(api/group-by DS :V1)
;; => _unnamed [2 3]:

| :name | :group-id |           :data |
|-------|-----------|-----------------|
|     1 |         0 | Group: 1 [5 4]: |
|     2 |         1 | Group: 2 [4 4]: |


;; there are many ways to get info!
(def gDS (api/group-by DS :V1))
(api/grouped? DS);; => nil
(api/grouped? gDS);; => true

(gDS :data) ;similar to Grouped dataset as map vals
;; => #tech.v3.dataset.column<dataset>[2]
:data
[Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
, Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
]


;; gDS as a seq
(api/groups->seq gDS)
;; =>
(Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
 Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
)

;; gDS as a map
(api/groups->map gDS)
;; =>
{
1 Group: 1 [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
,

2 Group: 2 [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
}

;; grouping by multiple columns
(api/group-by DS [:V1 :V3] {:result-type :as-seq})
;; =>
(Group: {:V3 0.5, :V1 1} [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
 Group: {:V3 1.0, :V1 2} [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
 Group: {:V3 1.5, :V1 1} [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   3 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
 Group: {:V3 0.5, :V1 2} [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   4 | 0.5 |   A |
 Group: {:V3 1.0, :V1 1} [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   5 | 1.0 |   B |
 Group: {:V3 1.5, :V1 2} [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   6 | 1.5 |   C |
)


;; grouping by row indexes
(api/group-by DS
              {"group-a" [1 2 1 2]
               "group-b" [5 5 5 1]}
              {:result-type :as-seq})
;; =>
(Group: group-a [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
 Group: group-b [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   6 | 1.5 |   C |
|   2 |   6 | 1.5 |   C |
|   2 |   6 | 1.5 |   C |
|   2 |   2 | 1.0 |   B |
)

;; group by function
(api/group-by DS
              (fn [row] (* (:V1 row)
                           (:V3 row)))
              {:result-type :as-seq})

;; =>
(Group: 1.0 [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
 Group: 2.0 [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
 Group: 0.5 [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
 Group: 3.0 [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   6 | 1.5 |   C |
 Group: 1.5 [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   3 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
)

;; predicate on column
(api/group-by DS
              (comp #(< % 1.0) :V3)
              {:result-type :as-seq})
;; => (Group: true [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   4 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
 Group: false [6 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |
)


;; juxt
(api/group-by DS
              (juxt :V1 :V3)
              {:result-type :as-seq})
;; =>
(Group: [1 0.5] [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
 Group: [2 1.0] [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
 Group: [1 1.5] [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   3 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
 Group: [2 0.5] [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   4 | 0.5 |   A |
 Group: [1 1.0] [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   1 |   5 | 1.0 |   B |
 Group: [2 1.5] [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|----:|----:|----:|-----|
|   2 |   6 | 1.5 |   C |
)


;; limiting columns
(api/group-by DS
              identity
              {:result-type :as-seq
               :select-keys [:V1]})
;; =>
(Group: {:V1 1} [5 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   1 |   3 | 1.5 |   C |
|   1 |   5 | 1.0 |   B |
|   1 |   7 | 0.5 |   A |
|   1 |   9 | 1.5 |   C |
 Group: {:V1 2} [4 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   2 |   4 | 0.5 |   A |
|   2 |   6 | 1.5 |   C |
|   2 |   8 | 1.0 |   B |
)

;; ** Ungrouping
(-> DS
    (api/group-by :V3)
    (api/ungroup {:order? true
                   :dataset-name "ordered by V3"}))
;; => ordered by V3 [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   4 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   5 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   6 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |


(-> DS
    (api/group-by :V3)
    (api/ungroup {:order? :desc
                  :dataset-name "Ordered by V3 descending"}))
;; => Ordered by V3 descending [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   3 | 1.5 |   C |
|   2 |   6 | 1.5 |   C |
|   1 |   9 | 1.5 |   C |
|   2 |   2 | 1.0 |   B |
|   1 |   5 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |
|   1 |   1 | 0.5 |   A |
|   2 |   4 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |


(-> DS
    (api/group-by (comp #(< % 4) :V2))
    (api/ungroup {:add-group-as-column true
                  :add-group-id-as-column true}))
;; => _unnamed [9 6]:

| :$group-name | :$group-id | :V1 | :V2 | :V3 | :V4 |
|--------------|------------|-----|-----|-----|-----|
|        false |          0 |   2 |   4 | 0.5 |   A |
|        false |          0 |   1 |   5 | 1.0 |   B |
|        false |          0 |   2 |   6 | 1.5 |   C |
|        false |          0 |   1 |   7 | 0.5 |   A |
|        false |          0 |   2 |   8 | 1.0 |   B |
|        false |          0 |   1 |   9 | 1.5 |   C |
|         true |          1 |   1 |   1 | 0.5 |   A |
|         true |          1 |   2 |   2 | 1.0 |   B |
|         true |          1 |   1 |   3 | 1.5 |   C |

(-> DS
    (api/group-by (comp #(< % 4) :V2))
    (api/ungroup {:add-group-as-column "Is V2 less than 4?"
                  :add-group-id-as-column "group id"}))
;; => _unnamed [9 6]:

| Is V2 less than 4? | group id | :V1 | :V2 | :V3 | :V4 |
|--------------------|----------|-----|-----|-----|-----|
|              false |        0 |   2 |   4 | 0.5 |   A |
|              false |        0 |   1 |   5 | 1.0 |   B |
|              false |        0 |   2 |   6 | 1.5 |   C |
|              false |        0 |   1 |   7 | 0.5 |   A |
|              false |        0 |   2 |   8 | 1.0 |   B |
|              false |        0 |   1 |   9 | 1.5 |   C |
|               true |        1 |   1 |   1 | 0.5 |   A |
|               true |        1 |   2 |   2 | 1.0 |   B |
|               true |        1 |   1 |   3 | 1.5 |   C |


;; new columns
(-> DS
    (api/group-by (fn [row] {"V1 and V3 multiplied" (* (:V1 row)
                                                       (:V3 row))
                             "V4 as lowercase" (clojure.string/lower-case (:V4 row))}))
    (api/ungroup {:add-group-as-column true}))
;; => _unnamed [9 6]:

| V1 and V3 multiplied | V4 as lowercase | :V1 | :V2 | :V3 | :V4 |
|----------------------|-----------------|-----|-----|-----|-----|
|                  1.0 |               a |   2 |   4 | 0.5 |   A |
|                  0.5 |               a |   1 |   1 | 0.5 |   A |
|                  0.5 |               a |   1 |   7 | 0.5 |   A |
|                  1.0 |               b |   1 |   5 | 1.0 |   B |
|                  2.0 |               b |   2 |   2 | 1.0 |   B |
|                  2.0 |               b |   2 |   8 | 1.0 |   B |
|                  3.0 |               c |   2 |   6 | 1.5 |   C |
|                  1.5 |               c |   1 |   3 | 1.5 |   C |
|                  1.5 |               c |   1 |   9 | 1.5 |   C |


;; group names without separation
(-> DS
    (api/group-by (fn [row] {"V1 and V3 multiplied" (* (:V1 row)
                                                      (:V3 row))
                            "V4 as lowercase" (clojure.string/lower-case (:V4 row))}))
    (api/ungroup {:add-group-as-column "just map"
                  :separate? false}))
;; => _unnamed [9 5]:

|                                            just map | :V1 | :V2 | :V3 | :V4 |
|-----------------------------------------------------|-----|-----|-----|-----|
| {"V1 and V3 multiplied" 1.0, "V4 as lowercase" "a"} |   2 |   4 | 0.5 |   A |
| {"V1 and V3 multiplied" 0.5, "V4 as lowercase" "a"} |   1 |   1 | 0.5 |   A |
| {"V1 and V3 multiplied" 0.5, "V4 as lowercase" "a"} |   1 |   7 | 0.5 |   A |
| {"V1 and V3 multiplied" 1.0, "V4 as lowercase" "b"} |   1 |   5 | 1.0 |   B |
| {"V1 and V3 multiplied" 2.0, "V4 as lowercase" "b"} |   2 |   2 | 1.0 |   B |
| {"V1 and V3 multiplied" 2.0, "V4 as lowercase" "b"} |   2 |   8 | 1.0 |   B |
| {"V1 and V3 multiplied" 3.0, "V4 as lowercase" "c"} |   2 |   6 | 1.5 |   C |
| {"V1 and V3 multiplied" 1.5, "V4 as lowercase" "c"} |   1 |   3 | 1.5 |   C |
| {"V1 and V3 multiplied" 1.5, "V4 as lowercase" "c"} |   1 |   9 | 1.5 |   C |


(-> DS
    (api/group-by (juxt :V1 :V3))
    (api/ungroup {:add-group-as-column "abc"}))
;; => _unnamed [9 6]:

| :abc-0 | :abc-1 | :V1 | :V2 | :V3 | :V4 |
|--------|--------|-----|-----|-----|-----|
|      1 |    1.0 |   1 |   5 | 1.0 |   B |
|      1 |    0.5 |   1 |   1 | 0.5 |   A |
|      1 |    0.5 |   1 |   7 | 0.5 |   A |
|      2 |    1.5 |   2 |   6 | 1.5 |   C |
|      1 |    1.5 |   1 |   3 | 1.5 |   C |
|      1 |    1.5 |   1 |   9 | 1.5 |   C |
|      2 |    0.5 |   2 |   4 | 0.5 |   A |
|      2 |    1.0 |   2 |   2 | 1.0 |   B |
|      2 |    1.0 |   2 |   8 | 1.0 |   B |


(-> DS
    (api/group-by (juxt :V1 :V3))
    (api/ungroup {:add-group-as-column ["v1" "v3"]}))
;; => _unnamed [9 6]:

| v1 |  v3 | :V1 | :V2 | :V3 | :V4 |
|----|-----|-----|-----|-----|-----|
|  1 | 1.0 |   1 |   5 | 1.0 |   B |
|  1 | 0.5 |   1 |   1 | 0.5 |   A |
|  1 | 0.5 |   1 |   7 | 0.5 |   A |
|  2 | 1.5 |   2 |   6 | 1.5 |   C |
|  1 | 1.5 |   1 |   3 | 1.5 |   C |
|  1 | 1.5 |   1 |   9 | 1.5 |   C |
|  2 | 0.5 |   2 |   4 | 0.5 |   A |
|  2 | 1.0 |   2 |   2 | 1.0 |   B |
|  2 | 1.0 |   2 |   8 | 1.0 |   B |



;; suppress separation
(-> DS
    (api/group-by (juxt :V1 :V3))
    (api/ungroup {:separate? false
                  :add-group-as-column true}))
;; => _unnamed [9 5]:

| :$group-name | :V1 | :V2 | :V3 | :V4 |
|--------------|-----|-----|-----|-----|
|      [1 1.0] |   1 |   5 | 1.0 |   B |
|      [1 0.5] |   1 |   1 | 0.5 |   A |
|      [1 0.5] |   1 |   7 | 0.5 |   A |
|      [2 1.5] |   2 |   6 | 1.5 |   C |
|      [1 1.5] |   1 |   3 | 1.5 |   C |
|      [1 1.5] |   1 |   9 | 1.5 |   C |
|      [2 0.5] |   2 |   4 | 0.5 |   A |
|      [2 1.0] |   2 |   2 | 1.0 |   B |
|      [2 1.0] |   2 |   8 | 1.0 |   B |



;; ** Other functions
(api/grouped? (api/group-by DS :V1));; => true

(-> DS
    (api/group-by :V1)
    (api/as-regular-dataset)
    (api/grouped?));; => nil

(-> DS
    (api/group-by :V1)
    (api/process-group-data #(str "Shape: " (vector (api/row-count %) (api/column-count %))))
    (api/as-regular-dataset))
;; => _unnamed [2 3]:

| :name | :group-id |        :data |
|-------|-----------|--------------|
|     1 |         0 | Shape: [5 4] |
|     2 |         1 | Shape: [4 4] |


;; * Columns
(api/column-names DS #".*[23]") ;; => (:V2 :V3)

(api/column-names DS #"^:int.*" :datatype) ;; => (:V1 :V2)
(api/column-names DS :type/integer);; => (:V1 :V2)

(api/column-names DS #{:float64} :datatype);; => (:V3)
(api/column-names DS :float64 :datatype);; => (:V3)
(api/column-names DS :type/float64);; => (:V3)

;;complements
(api/column-names DS (complement #{:V1}));; => (:V2 :V3 :V4)
(api/column-names DS (complement #{:float64}) :datatype);; => (:V1 :V2 :V4)
(api/column-names DS :!type/float64);; => (:V1 :V2 :V4)


;;all metadata at once
(api/column-names DS (fn [meta]
                       (and (= :int64 (:datatype meta))
                            (clojure.string/ends-with? (:name meta) "1"))) :all)


;; ** select

(api/select-columns DS #(= :float64 %) :datatype)
(api/select-columns DS :type/float64)
;; => _unnamed [9 1]:

| :V3 |
|-----|
| 0.5 |
| 1.0 |
| 1.5 |
| 0.5 |
| 1.0 |
| 1.5 |
| 0.5 |
| 1.0 |
| 1.5 |

(api/select-columns DS :!type/float64)
;; => _unnamed [9 3]:

| :V1 | :V2 | :V4 |
|-----|-----|-----|
|   1 |   1 |   A |
|   2 |   2 |   B |
|   1 |   3 |   C |
|   2 |   4 |   A |
|   1 |   5 |   B |
|   2 |   6 |   C |
|   1 |   7 |   A |
|   2 |   8 |   B |
|   1 |   9 |   C |


;;column selection to groups
(-> DS
    (api/group-by :V1)
    (api/select-columns [:V2 :V3])
    (api/groups->map))
;; => {1 Group: 1 [5 2]:

| :V2 | :V3 |
|-----|-----|
|   1 | 0.5 |
|   3 | 1.5 |
|   5 | 1.0 |
|   7 | 0.5 |
|   9 | 1.5 |
, 2 Group: 2 [4 2]:

| :V2 | :V3 |
|-----|-----|
|   2 | 1.0 |
|   4 | 0.5 |
|   6 | 1.5 |
|   8 | 1.0 |



;; ** drop

(api/drop-columns DS #(= :float64 %) :datatype)
(api/drop-columns DS :type/float64)
;; => _unnamed [9 3]:

| :V1 | :V2 | :V4 |
|-----|-----|-----|
|   1 |   1 |   A |
|   2 |   2 |   B |
|   1 |   3 |   C |
|   2 |   4 |   A |
|   1 |   5 |   B |
|   2 |   6 |   C |
|   1 |   7 |   A |
|   2 |   8 |   B |
|   1 |   9 |   C |


(api/drop-columns DS (complement #{:V1 :V2}))
;; => _unnamed [9 2]:

| :V1 | :V2 |
|-----|-----|
|   1 |   1 |
|   2 |   2 |
|   1 |   3 |
|   2 |   4 |
|   1 |   5 |
|   2 |   6 |
|   1 |   7 |
|   2 |   8 |
|   1 |   9 |


;;column drop to groups
(-> DS
    (api/group-by :V1)
    (api/drop-columns [:V2 :V3])
    (api/groups->map))
;; => {1 Group: 1 [5 2]:

| :V1 | :V4 |
|-----|-----|
|   1 |   A |
|   1 |   C |
|   1 |   B |
|   1 |   A |
|   1 |   C |
, 2 Group: 2 [4 2]:

| :V1 | :V4 |
|-----|-----|
|   2 |   B |
|   2 |   A |
|   2 |   C |
|   2 |   B |




;; ** rename

(api/rename-columns DS {:V1 "v1"
                        :V2 "v2"
                        :V3 [1 2 3]
                        :V4 (Object.)});; => _unnamed [9 4]:

| v1 | v2 | [1 2 3] | java.lang.Object@78425808 |
|----|----|---------|---------------------------|
|  1 |  1 |     0.5 |                         A |
|  2 |  2 |     1.0 |                         B |
|  1 |  3 |     1.5 |                         C |
|  2 |  4 |     0.5 |                         A |
|  1 |  5 |     1.0 |                         B |
|  2 |  6 |     1.5 |                         C |
|  1 |  7 |     0.5 |                         A |
|  2 |  8 |     1.0 |                         B |
|  1 |  9 |     1.5 |                         C |


(map name (api/column-names DS));; => ("V1" "V2" "V3" "V4")
(api/rename-columns DS (comp str second name))
;; => _unnamed [9 4]:

| 1 | 2 |   3 | 4 |
|---|---|-----|---|
| 1 | 1 | 0.5 | A |
| 2 | 2 | 1.0 | B |
| 1 | 3 | 1.5 | C |
| 2 | 4 | 0.5 | A |
| 1 | 5 | 1.0 | B |
| 2 | 6 | 1.5 | C |
| 1 | 7 | 0.5 | A |
| 2 | 8 | 1.0 | B |
| 1 | 9 | 1.5 | C |


(name :V3);; => "V3"
(name 'V3);; => "V3"
(name "V3");; => "V3"


;;rename columns in groups
(-> DS
    (api/group-by :V1)
    (api/rename-columns {:V1 "v1"
                         :V2 "v2"
                         :V3 [1 2 3]
                         :V4 (Object.)})
    (api/groups->map));; => {1 Group: 1 [5 4]:

| v1 | v2 | [1 2 3] | java.lang.Object@17fb0b72 |
|----|----|---------|---------------------------|
|  1 |  1 |     0.5 |                         A |
|  1 |  3 |     1.5 |                         C |
|  1 |  5 |     1.0 |                         B |
|  1 |  7 |     0.5 |                         A |
|  1 |  9 |     1.5 |                         C |
, 2 Group: 2 [4 4]:

| v1 | v2 | [1 2 3] | java.lang.Object@17fb0b72 |
|----|----|---------|---------------------------|
|  2 |  2 |     1.0 |                         B |
|  2 |  4 |     0.5 |                         A |
|  2 |  6 |     1.5 |                         C |
|  2 |  8 |     1.0 |                         B |



;; ** add or update

(api/add-or-replace-column DS :V5 "X")
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |   X |
|   2 |   2 | 1.0 |   B |   X |
|   1 |   3 | 1.5 |   C |   X |
|   2 |   4 | 0.5 |   A |   X |
|   1 |   5 | 1.0 |   B |   X |
|   2 |   6 | 1.5 |   C |   X |
|   1 |   7 | 0.5 |   A |   X |
|   2 |   8 | 1.0 |   B |   X |
|   1 |   9 | 1.5 |   C |   X |


(api/add-or-replace-column DS :V1 (repeatedly rand))
;; => _unnamed [9 4]:

|      :V1 | :V2 | :V3 | :V4 |
|----------|-----|-----|-----|
|   0.9758 |   1 | 0.5 |   A |
|   0.8607 |   2 | 1.0 |   B |
| 0.006428 |   3 | 1.5 |   C |
|   0.1177 |   4 | 0.5 |   A |
|   0.7187 |   5 | 1.0 |   B |
|   0.7313 |   6 | 1.5 |   C |
|   0.5778 |   7 | 0.5 |   A |
|   0.1048 |   8 | 1.0 |   B |
|   0.7003 |   9 | 1.5 |   C |


(api/add-or-replace-column DS :V5 (DS :V1))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |   1 |
|   2 |   2 | 1.0 |   B |   2 |
|   1 |   3 | 1.5 |   C |   1 |
|   2 |   4 | 0.5 |   A |   2 |
|   1 |   5 | 1.0 |   B |   1 |
|   2 |   6 | 1.5 |   C |   2 |
|   1 |   7 | 0.5 |   A |   1 |
|   2 |   8 | 1.0 |   B |   2 |
|   1 |   9 | 1.5 |   C |   1 |


(api/add-or-replace-column DS :row-count api/row-count) 
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :row-count |
|-----|-----|-----|-----|------------|
|   1 |   1 | 0.5 |   A |          9 |
|   2 |   2 | 1.0 |   B |          9 |
|   1 |   3 | 1.5 |   C |          9 |
|   2 |   4 | 0.5 |   A |          9 |
|   1 |   5 | 1.0 |   B |          9 |
|   2 |   6 | 1.5 |   C |          9 |
|   1 |   7 | 0.5 |   A |          9 |
|   2 |   8 | 1.0 |   B |          9 |
|   1 |   9 | 1.5 |   C |          9 |


;;TODO multiply all numeric columns
(api/add-or-replace-column DS
                           :product
                           (tech.v2.datatype.functional/* (DS :V1) (DS :V2) (DS :V3)))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :product |
|-----|-----|-----|-----|----------|
|   1 |   1 | 0.5 |   A |      0.5 |
|   2 |   2 | 1.0 |   B |      4.0 |
|   1 |   3 | 1.5 |   C |      4.5 |
|   2 |   4 | 0.5 |   A |      4.0 |
|   1 |   5 | 1.0 |   B |      5.0 |
|   2 |   6 | 1.5 |   C |     18.0 |
|   1 |   7 | 0.5 |   A |      3.5 |
|   2 |   8 | 1.0 |   B |     16.0 |
|   1 |   9 | 1.5 |   C |     13.5 |

(api/add-or-replace-columns DS {:product #(map * (% :V1) (% :V2) (% :V3))})
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :product |
|-----|-----|-----|-----|----------|
|   1 |   1 | 0.5 |   A |   0.5000 |
|   2 |   2 | 1.0 |   B |    4.000 |
|   1 |   3 | 1.5 |   C |    4.500 |
|   2 |   4 | 0.5 |   A |    4.000 |
|   1 |   5 | 1.0 |   B |    5.000 |
|   2 |   6 | 1.5 |   C |    18.00 |
|   1 |   7 | 0.5 |   A |    3.500 |
|   2 |   8 | 1.0 |   B |    16.00 |
|   1 |   9 | 1.5 |   C |    13.50 |


;;TODO above but more simply - can we multiply columns?
;; see under map section!!
(api/select-columns DS :!type/string)
(api/add-or-replace-column DS
                           :product
                           (tech.v2.datatype.functional/* (api/select-columns DS :!type/integer)))
(tech.v2.datatype.functional/* DS [:V1 :V2])
;;


(-> DS
    (api/group-by :V1)
    (api/add-or-replace-column :row-count api/row-count)
    (api/ungroup))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :row-count |
|-----|-----|-----|-----|------------|
|   1 |   1 | 0.5 |   A |          5 |
|   1 |   3 | 1.5 |   C |          5 |
|   1 |   5 | 1.0 |   B |          5 |
|   1 |   7 | 0.5 |   A |          5 |
|   1 |   9 | 1.5 |   C |          5 |
|   2 |   2 | 1.0 |   B |          4 |
|   2 |   4 | 0.5 |   A |          4 |
|   2 |   6 | 1.5 |   C |          4 |
|   2 |   8 | 1.0 |   B |          4 |


;;item recycling (or trimming) or missing appendations in column
(api/add-or-replace-column DS :V5 [:r :b])
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |  :r |
|   2 |   2 | 1.0 |   B |  :b |
|   1 |   3 | 1.5 |   C |  :r |
|   2 |   4 | 0.5 |   A |  :b |
|   1 |   5 | 1.0 |   B |  :r |
|   2 |   6 | 1.5 |   C |  :b |
|   1 |   7 | 0.5 |   A |  :r |
|   2 |   8 | 1.0 |   B |  :b |
|   1 |   9 | 1.5 |   C |  :r |


(api/add-or-replace-column DS :V5 [:r :b] :na)
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |  :r |
|   2 |   2 | 1.0 |   B |  :b |
|   1 |   3 | 1.5 |   C |     |
|   2 |   4 | 0.5 |   A |     |
|   1 |   5 | 1.0 |   B |     |
|   2 |   6 | 1.5 |   C |     |
|   1 |   7 | 0.5 |   A |     |
|   2 |   8 | 1.0 |   B |     |
|   1 |   9 | 1.5 |   C |     |

(-> DS
    (api/group-by :V3)
    (api/add-or-replace-column :V5 [:r :b] :na)
    (api/ungroup))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |  :r |
|   1 |   5 | 1.0 |   B |  :b |
|   2 |   8 | 1.0 |   B |     |
|   1 |   1 | 0.5 |   A |  :r |
|   2 |   4 | 0.5 |   A |  :b |
|   1 |   7 | 0.5 |   A |     |
|   1 |   3 | 1.5 |   C |  :r |
|   2 |   6 | 1.5 |   C |  :b |
|   1 |   9 | 1.5 |   C |     |


(try
  (api/add-or-replace-column DS :V5 [:r :b] :strict)
  (catch Exception e (str "Exception caught: "(ex-message e))))
;; => "Exception caught: Column size (2) should be exactly the same as dataset row count (9)"


;;use other column to fill group
(-> DS
    (api/group-by :V3)
    (api/add-or-replace-column :V5 (DS :V2))
    (api/ungroup))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :V5 |
|-----|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |   1 |
|   1 |   5 | 1.0 |   B |   2 |
|   2 |   8 | 1.0 |   B |   3 |
|   1 |   1 | 0.5 |   A |   1 |
|   2 |   4 | 0.5 |   A |   2 |
|   1 |   7 | 0.5 |   A |   3 |
|   1 |   3 | 1.5 |   C |   1 |
|   2 |   6 | 1.5 |   C |   2 |
|   1 |   9 | 1.5 |   C |   3 |
;;TODO figure out why there is the 1,2,3 cycle


;;update several columns
(api/add-or-replace-columns DS {:V1 #(map inc (% :V1))
                                :V5 #(map (comp keyword str) (% :V4))
                                :V6 11})
;; => _unnamed [9 6]:

| :V1 | :V2 | :V3 | :V4 | :V5 | :V6 |
|-----|-----|-----|-----|-----|-----|
|   2 |   1 | 0.5 |   A |  :A |  11 |
|   3 |   2 | 1.0 |   B |  :B |  11 |
|   2 |   3 | 1.5 |   C |  :C |  11 |
|   3 |   4 | 0.5 |   A |  :A |  11 |
|   2 |   5 | 1.0 |   B |  :B |  11 |
|   3 |   6 | 1.5 |   C |  :C |  11 |
|   2 |   7 | 0.5 |   A |  :A |  11 |
|   3 |   8 | 1.0 |   B |  :B |  11 |
|   2 |   9 | 1.5 |   C |  :C |  11 |



;; ** update
DS
;; => _unnamed [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   3 | 1.5 |   C |
|   2 |   4 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   9 | 1.5 |   C |

(api/update-columns DS :all reverse)
;; => _unnamed [9 4]:

| :V1 | :V2 |    :V3 | :V4 |
|-----|-----|--------|-----|
|   1 |   9 |  1.500 |   C |
|   2 |   8 |  1.000 |   B |
|   1 |   7 | 0.5000 |   A |
|   2 |   6 |  1.500 |   C |
|   1 |   5 |  1.000 |   B |
|   2 |   4 | 0.5000 |   A |
|   1 |   3 |  1.500 |   C |
|   2 |   2 |  1.000 |   B |
|   1 |   1 | 0.5000 |   A |

(api/update-columns DS :V2 reverse)
;; => _unnamed [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   9 | 0.5 |   A |
|   2 |   8 | 1.0 |   B |
|   1 |   7 | 1.5 |   C |
|   2 |   6 | 0.5 |   A |
|   1 |   5 | 1.0 |   B |
|   2 |   4 | 1.5 |   C |
|   1 |   3 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   1 | 1.5 |   C |


(api/update-columns DS :type/numerical [(partial map dec)
                                        (partial map inc)])
;; => _unnamed [9 4]:

| :V1 | :V2 |     :V3 | :V4 |
|-----|-----|---------|-----|
|   0 |   2 | -0.5000 |   A |
|   1 |   3 |   0.000 |   B |
|   0 |   4 |  0.5000 |   C |
|   1 |   5 | -0.5000 |   A |
|   0 |   6 |   0.000 |   B |
|   1 |   7 |  0.5000 |   C |
|   0 |   8 | -0.5000 |   A |
|   1 |   9 |   0.000 |   B |
|   0 |  10 |  0.5000 |   C |

(api/update-columns DS :type/numerical [(partial map dec)])
;; => _unnamed [9 4]:

| :V1 | :V2 |     :V3 | :V4 |
|-----|-----|---------|-----|
|   0 |   0 | -0.5000 |   A |
|   1 |   1 |   0.000 |   B |
|   0 |   2 |  0.5000 |   C |
|   1 |   3 | -0.5000 |   A |
|   0 |   4 |   0.000 |   B |
|   1 |   5 |  0.5000 |   C |
|   0 |   6 | -0.5000 |   A |
|   1 |   7 |   0.000 |   B |
|   0 |   8 |  0.5000 |   C |

(api/update-columns DS :type/numerical [(partial map dec)
                                        (partial map inc)
                                        (partial map #(* % %))])
;; => _unnamed [9 4]:

| :V1 | :V2 |    :V3 | :V4 |
|-----|-----|--------|-----|
|   0 |   2 | 0.2500 |   A |
|   1 |   3 |  1.000 |   B |
|   0 |   4 |  2.250 |   C |
|   1 |   5 | 0.2500 |   A |
|   0 |   6 |  1.000 |   B |
|   1 |   7 |  2.250 |   C |
|   0 |   8 | 0.2500 |   A |
|   1 |   9 |  1.000 |   B |
|   0 |  10 |  2.250 |   C |

(api/update-columns DS {:V1 (partial map dec)
                        :V2 (partial map inc)
                        :V3 (partial map #(* % %))})
;; => _unnamed [9 4]:

| :V1 | :V2 |    :V3 | :V4 |
|-----|-----|--------|-----|
|   0 |   2 | 0.2500 |   A |
|   1 |   3 |  1.000 |   B |
|   0 |   4 |  2.250 |   C |
|   1 |   5 | 0.2500 |   A |
|   0 |   6 |  1.000 |   B |
|   1 |   7 |  2.250 |   C |
|   0 |   8 | 0.2500 |   A |
|   1 |   9 |  1.000 |   B |
|   0 |  10 |  2.250 |   C |


(api/update-columns DS {:V1 reverse ;TODO why can't we put inc here?
                        :V2 (comp shuffle seq)})
;; => _unnamed [9 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   4 | 0.5 |   A |
|   2 |   6 | 1.0 |   B |
|   1 |   5 | 1.5 |   C |
|   2 |   3 | 0.5 |   A |
|   1 |   8 | 1.0 |   B |
|   2 |   9 | 1.5 |   C |
|   1 |   7 | 0.5 |   A |
|   2 |   2 | 1.0 |   B |
|   1 |   1 | 1.5 |   C |


;; ** map
(api/map-columns DS ;dataset
                 :sum-of-numbers ;column-name
                 (api/column-names DS  #{:int64 :float64} :datatype) ;column-selector
                 (fn [& rows] ; mapping function
                   (reduce + rows)))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :sum-of-numbers |
|-----|-----|-----|-----|-----------------|
|   1 |   1 | 0.5 |   A |             2.5 |
|   2 |   2 | 1.0 |   B |             5.0 |
|   1 |   3 | 1.5 |   C |             5.5 |
|   2 |   4 | 0.5 |   A |             6.5 |
|   1 |   5 | 1.0 |   B |             7.0 |
|   2 |   6 | 1.5 |   C |             9.5 |
|   1 |   7 | 0.5 |   A |             8.5 |
|   2 |   8 | 1.0 |   B |            11.0 |
|   1 |   9 | 1.5 |   C |            11.5 |


(api/map-columns DS
                 :product
                 (api/column-names DS :type/numerical)
                 (fn [& rows]
                   (reduce * rows)))
;; => _unnamed [9 5]:

| :V1 | :V2 | :V3 | :V4 | :product |
|-----|-----|-----|-----|----------|
|   1 |   1 | 0.5 |   A |      0.5 |
|   2 |   2 | 1.0 |   B |      4.0 |
|   1 |   3 | 1.5 |   C |      4.5 |
|   2 |   4 | 0.5 |   A |      4.0 |
|   1 |   5 | 1.0 |   B |      5.0 |
|   2 |   6 | 1.5 |   C |     18.0 |
|   1 |   7 | 0.5 |   A |      3.5 |
|   2 |   8 | 1.0 |   B |     16.0 |
|   1 |   9 | 1.5 |   C |     13.5 |

;; works because of all this below
(apply (fn [& rows] (reduce * rows)) [2 3 4]);; => 24
(apply map (fn [& rows] (reduce * rows)) [[2 3 4] [4 5 6] [7 8 9]]);; => (56 120 216)
(map (fn [& rows] (reduce * rows)) [2 3 4] [4 5 6] [7 8 9]);; => (56 120 216)
(map #(apply (fn [& rows] (reduce * rows)) %) [[2 3 4] [4 5 6] [7 8 9]]);; => (24 120 504)






;; * Rows
(api/select-rows DS 4)
;; => _unnamed [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   5 | 1.0 |   B |


(api/select-rows DS [1 4 5])
;; => _unnamed [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   1 |   5 | 1.0 |   B |
|   2 |   6 | 1.5 |   C |


(api/select-rows DS [true nil nil true])
;; => _unnamed [2 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   4 | 0.5 |   A |


(api/select-rows DS (comp #(< % 1) :V3))
;; => _unnamed [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   1 |   1 | 0.5 |   A |
|   2 |   4 | 0.5 |   A |
|   1 |   7 | 0.5 |   A |


(api/select-rows DS (comp #(= % 4) :V2))
;; => _unnamed [1 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   4 | 0.5 |   A |


(api/select-rows DS (comp #(= % "B") :V4))
;; => _unnamed [3 4]:

| :V1 | :V2 | :V3 | :V4 |
|-----|-----|-----|-----|
|   2 |   2 | 1.0 |   B |
|   1 |   5 | 1.0 |   B |
|   2 |   8 | 1.0 |   B |


;; * Aggregate
;; * Order
;; * Unique
;; * Missing
;; * Join/Separate
;; * Fold/Unroll
;; * Reshape
;; * Join/Concat
;; * Split
;; * Pipeline
;; * Functions
;; * Other examples
;; * zotes
;; ** map or techascent functional operator
(tc/add-or-replace-column ds
                          :diff
                          (map #(- % %2) (ds :l) (ds :c)))
(tc/add-or-replace-column ds
                          :diff
                          (tech.v3.datatype.functional/- (ds :c) (ds :l)))
either work, though there may be more ready-made tools in tech.

;; ** ds size consistency
(tc/add-column ds
               :dif
               (map #(- % %2)
                    (tc/tail (ds :c) 2229)
                    (tc/head (ds :c) 2229))
               :na)
require :na|:cycle to maintain same size as ds


