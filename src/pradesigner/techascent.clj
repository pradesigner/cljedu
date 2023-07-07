"
techascent
"

(ns user
  (:require [clojure.string :as s]
            [tech.ml.dataset :as ds]
            [tech.ml.dataset.column :as ds-col]
            [tech.v2.datatype :as dtype]))

;; * mini-walkthrough examples

;; We support many file formats

(def csv-data (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv"))
(ds/head csv-data)
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv [5 3]:

| symbol |       date | price |
|--------|------------|-------|
|   MSFT | 2000-01-01 | 39.81 |
|   MSFT | 2000-02-01 | 36.35 |
|   MSFT | 2000-03-01 | 43.22 |
|   MSFT | 2000-04-01 | 28.37 |
|   MSFT | 2000-05-01 | 25.45 |

(ds/select csv-data ["date" "price"] (range 4))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv [4 2]:

|       date | price |
|------------|-------|
| 2000-01-01 | 39.81 |
| 2000-02-01 | 36.35 |
| 2000-03-01 | 43.22 |
| 2000-04-01 | 28.37 |


(def xls-data (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLS_1000.xls"))
(ds/head xls-data);; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLS_1000.xls [5 8]:

| column-0 | First Name | Last Name | Gender |       Country |  Age |       Date |     Id |
|----------|------------|-----------|--------|---------------|------|------------|--------|
|      1.0 |      Dulce |     Abril | Female | United States | 32.0 | 15/10/2017 | 1562.0 |
|      2.0 |       Mara | Hashimoto | Female | Great Britain | 25.0 | 16/08/2016 | 1582.0 |
|      3.0 |     Philip |      Gent |   Male |        France | 36.0 | 21/05/2015 | 2587.0 |
|      4.0 |   Kathleen |    Hanner | Female | United States | 25.0 | 15/10/2017 | 3549.0 |
|      5.0 |    Nereida |   Magwood | Female | United States | 58.0 | 16/08/2016 | 2468.0 |


;; And you can have fine grained control over parsing
(ds/head (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLS_1000.xls"
                             {:parser-fn {"Date" [:local-date "dd/MM/yyyy"]}}))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLS_1000.xls [5 8]:

| column-0 | First Name | Last Name | Gender |       Country |  Age |       Date |     Id |
|----------|------------|-----------|--------|---------------|------|------------|--------|
|      1.0 |      Dulce |     Abril | Female | United States | 32.0 | 2017-10-15 | 1562.0 |
|      2.0 |       Mara | Hashimoto | Female | Great Britain | 25.0 | 2016-08-16 | 1582.0 |
|      3.0 |     Philip |      Gent |   Male |        France | 36.0 | 2015-05-21 | 2587.0 |
|      4.0 |   Kathleen |    Hanner | Female | United States | 25.0 | 2017-10-15 | 3549.0 |
|      5.0 |    Nereida |   Magwood | Female | United States | 58.0 | 2016-08-16 | 2468.0 |


;; Loading from the web is no problem
(def airports (ds/->dataset "https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat"
                            {:header-row? false}))
(ds/head airports)
;; => https://raw.githubusercontent.com/jpatokal/openflights/master/data/airports.dat [5 14]:

| column-0 |                                    column-1 |     column-2 |         column-3 | column-4 | column-5 |    column-6 |     column-7 | column-8 | column-9 | column-10 |            column-11 | column-12 |   column-13 |
|----------|---------------------------------------------|--------------|------------------|----------|----------|-------------|--------------|----------|----------|-----------|----------------------|-----------|-------------|
|        1 |                              Goroka Airport |       Goroka | Papua New Guinea |      GKA |     AYGA | -6.08168983 | 145.39199829 |     5282 |     10.0 |         U | Pacific/Port_Moresby |   airport | OurAirports |
|        2 |                              Madang Airport |       Madang | Papua New Guinea |      MAG |     AYMD | -5.20707989 | 145.78900147 |       20 |     10.0 |         U | Pacific/Port_Moresby |   airport | OurAirports |
|        3 |                Mount Hagen Kagamuga Airport |  Mount Hagen | Papua New Guinea |      HGU |     AYMH | -5.82678986 | 144.29600525 |     5388 |     10.0 |         U | Pacific/Port_Moresby |   airport | OurAirports |
|        4 |                              Nadzab Airport |       Nadzab | Papua New Guinea |      LAE |     AYNZ | -6.56980300 | 146.72597700 |      239 |     10.0 |         U | Pacific/Port_Moresby |   airport | OurAirports |
|        5 | Port Moresby Jacksons International Airport | Port Moresby | Papua New Guinea |      POM |     AYPY | -9.44338036 | 147.22000122 |      146 |     10.0 |         U | Pacific/Port_Moresby |   airport | OurAirports |


;; At any point you can get a sequence of maps back
(take 2 (ds/mapseq-reader csv-data))
;; => ({"date" #object[java.time.LocalDate 0x44c8ad3b "2000-01-01"], "symbol" "MSFT", "price" 39.81} {"date" #object[java.time.LocalDate 0x4fb349ee "2000-02-01"], "symbol" "MSFT", "price" 36.35})


;;Data is stored in primitive arrays (even most datetimes!) and strings are stored
;;in string tables.  You can load really large datasets with this thing!
;;Datasets are sequence of columns.
;;Columns themselves are sequences of their entries.
(csv-data "symbol")
;; => #tech.ml.dataset.column<string>[560]
symbol
[MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, MSFT, ...]

(xls-data "Gender")
;; => #tech.ml.dataset.column<string>[1000]
Gender
[Female, Female, Male, Female, Female, Male, Female, Female, Female, Female, Female, Male, Female, Male, Female, Female, Female, Female, Female, Female, ...]

(take 5 (xls-data "Gender"))
;; => ("Female" "Female" "Male" "Female" "Female")


;;datasets and columns implement the clojure metadata interfaces (`meta`, `withMeta`).
(->> csv-data
     (map (fn [column]
            (meta column))))
({:categorical? true, :name "symbol", :size 560, :datatype :string}
 {:name "date", :size 560, :datatype :packed-local-date}
 {:name "price", :size 560, :datatype :float32})


;;We can get a brief description of the dataset:
(def brief-csvdata(ds/brief csv-data))
brief-csvdata
;; => ({:min #object[java.time.LocalDate 0x668c5645 "2000-01-01"], :n-missing 0, :col-name "date", :mean #object[java.time.LocalDate 0x162684d1 "2005-05-12"], :datatype :packed-local-date, :skew -0.13894433927538588, :standard-deviation 9.250270466130963E10, :quartile-3 #object[java.time.LocalDate 0x49ae0e6a "2007-11-23"], :n-valid 560, :quartile-1 #object[java.time.LocalDate 0x275b0379 "2002-11-08"], :median #object[java.time.LocalDate 0x23e147a9 "2005-07-16"], :max #object[java.time.LocalDate 0x1435f08 "2010-03-01"]} {:min 5.97, :n-missing 0, :col-name "price", :mean 100.7342857142857, :datatype :float64, :skew 2.4130946430619233, :standard-deviation 132.55477114107083, :quartile-3 100.88, :n-valid 560, :quartile-1 24.169999999999998, :median 57.255, :max 707.0} {:mode "MSFT", :values ["MSFT" "AMZN" "IBM" "AAPL" "GOOG"], :n-values 5, :n-valid 560, :col-name "symbol", :n-missing 0, :datatype :string, :histogram (["MSFT" 123] ["AMZN" 123] ["IBM" 123] ["AAPL" 123] ["GOOG" 68])})
(:mean (second brief-csvdata))
;; => 100.7342857142857

(ds/descriptive-stats csv-data)
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |       :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|-------------|
|      date | :packed-local-date |      560 |          0 | 2000-01-01 | 2005-05-12 |       | 2010-03-01 |      9.25027047E+10 | -0.13894434 |
|     price |           :float64 |      560 |          0 |      5.970 |      100.7 |       |      707.0 |      1.32554771E+02 |  2.41309464 |
|    symbol |            :string |      560 |          0 |            |            |  MSFT |            |                     |             |



;; * tech.ml.dataset Walkthrough
;; ** Dataset Creation

(ds/->dataset [{:a 1 :b 2} {:a 2 :c 3}])
;; => _unnamed [2 3]:

| :a | :b | :c |
|----|----|----|
|  1 |  2 |    |
|  2 |    |  3 |


(ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz"
              {:column-whitelist ["SalePrice" "1stFlrSF" "2ndFlrSF"]
               :n-records 5
               :parser-fn :float32})
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 3]:

| SalePrice | 1stFlrSF | 2ndFlrSF |
|-----------|----------|----------|
|  208500.0 |    856.0 |    854.0 |
|  181500.0 |   1262.0 |      0.0 |
|  223500.0 |    920.0 |    866.0 |
|  140000.0 |    961.0 |    756.0 |
|  250000.0 |   1145.0 |   1053.0 |

(ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz"
              {:column-whitelist ["SalePrice" "1stFlrSF" "2ndFlrSF"]
               :n-records 5
               :parser-fn {"SalePrice" :float32}});; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 3]:

| SalePrice | 1stFlrSF | 2ndFlrSF |
|-----------|----------|----------|
|  208500.0 |      856 |      854 |
|  181500.0 |     1262 |        0 |
|  223500.0 |      920 |      866 |
|  140000.0 |      961 |      756 |
|  250000.0 |     1145 |     1053 |


(def data-noparse
  (ds/head
   (ds/->dataset
    "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLSX_1000.xlsx")))

data-noparse
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLSX_1000.xlsx [5 8]:

| column-0 | First Name | Last Name | Gender |       Country |  Age |       Date |     Id |
|----------|------------|-----------|--------|---------------|------|------------|--------|
|      1.0 |      Dulce |     Abril | Female | United States | 32.0 | 15/10/2017 | 1562.0 |
|      2.0 |       Mara | Hashimoto | Female | Great Britain | 25.0 | 16/08/2016 | 1582.0 |
|      3.0 |     Philip |      Gent |   Male |        France | 36.0 | 21/05/2015 | 2587.0 |
|      4.0 |   Kathleen |    Hanner | Female | United States | 25.0 | 15/10/2017 | 3549.0 |
|      5.0 |    Nereida |   Magwood | Female | United States | 58.0 | 16/08/2016 | 2468.0 |

;; date doesn't parse out

(dtype/get-datatype (data "Date")) ;;TODO what's this doing? do we even need it?

(def data-parse
  (ds/head
   (ds/->dataset "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLSX_1000.xlsx"
                 {:parser-fn {"Date" [:local-date "dd/MM/yyyy"]}})))

data-parse
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLSX_1000.xlsx [5 8]:

| column-0 | First Name | Last Name | Gender |       Country |  Age |       Date |     Id |
|----------|------------|-----------|--------|---------------|------|------------|--------|
|      1.0 |      Dulce |     Abril | Female | United States | 32.0 | 2017-10-15 | 1562.0 |
|      2.0 |       Mara | Hashimoto | Female | Great Britain | 25.0 | 2016-08-16 | 1582.0 |
|      3.0 |     Philip |      Gent |   Male |        France | 36.0 | 2015-05-21 | 2587.0 |
|      4.0 |   Kathleen |    Hanner | Female | United States | 25.0 | 2017-10-15 | 3549.0 |
|      5.0 |    Nereida |   Magwood | Female | United States | 58.0 | 2016-08-16 | 2468.0 |


;; parse-test:
;; https://github.com/techascent/tech.ml.dataset/blob/4.04/test/tech/ml/dataset/parse_test.clj

(ds/head
 (ds/->dataset data-noparse
               {:parser-fn {"Date" [:local-date "dd/MM/yyyy"]}}))

;; above doesn't parse date
;; DONE how to do the above with what's in data?? see xls, xlsr below


;; more efficient way to do create dataset than item by item (can use just ->dataset)
(ds/name-values-seq->dataset {:age [1 2 3 4 5 6]
                              :name ["a" "b" "c" "d" "e" "f"]})
;; => _unnamed [6 2]:

| :age | :name |
|------|-------|
|    1 |     a |
|    2 |     b |
|    3 |     c |
|    4 |     d |
|    5 |     e |
|    6 |     f |

(def new-ds
  (ds/name-values-seq->dataset
   {:a [1 2 3]
    :b [4 5 6]
    :c [7 8 nil]}))
new-ds
;; _unnamed [3 3]:

;; | :a | :b | :c |
;; |----|----|----|
;; |  1 |  4 |  7 |
;; |  2 |  5 |  8 |
;; |  3 |  6 |    |

;; ** Printing
(require '[tech.v2.tensor :as dtt])

(def test-tens
  (dtt/->tensor (partition 3 (range 9))))

(ds/->dataset [{:a 1 :b test-tens}
               {:a 2 :b test-tens}])
;; => _unnamed [2 2]:

| :a |                            :b |
|----|-------------------------------|
|  1 | #tech.v2.tensor<float64>[3 3] |
|    | [[0.000 1.000 2.000]          |
|    |  [3.000 4.000 5.000]          |
|    |  [6.000 7.000 8.000]]         |
|  2 | #tech.v2.tensor<float64>[3 3] |
|    | [[0.000 1.000 2.000]          |
|    |  [3.000 4.000 5.000]          |
|    |  [6.000 7.000 8.000]]         |


;; multiplying internals by 5
(map
 (fn [x]
   (map #(* 5 %) x))
 test-tens)
;; => ((0.0 5.0 10.0) (15.0 20.0 25.0) (30.0 35.0 40.0))


;; control printing with metadata
(def tens-ds *1) ;can pullout old results?!
(with-meta tens-ds
  (assoc (meta tens-ds)
         :print-line-policy :single))
;; => _unnamed [2 2]:

| :a |                            :b |
|----|-------------------------------|
|  1 | #tech.v2.tensor<float64>[3 3] |
|  2 | #tech.v2.tensor<float64>[3 3] |


(require '[tech.io :as io])

(def events-ds
  (-> (io/get-json "https://api.github.com/events"
                   :key-fn keyword)
      (ds/->dataset)))
;;output of events-ds truncated to 1M

(ds/head
 (with-meta events-ds
   (assoc (meta events-ds)
          :print-line-policy :single
          :print-column-max-width 6))) 
;; => _unnamed [5 8]:

;; |    :id |  :type | :actor |  :repo | :payload | :public | :created_at |   :org |
;; |--------|--------|--------|--------|----------|---------|-------------|--------|
;; | 146516 | PushEv | {:id 6 | {:id 3 |   {:push |    true |      2020-1 |        |
;; | 146516 | PushEv | {:id 4 | {:id 2 |   {:push |    true |      2020-1 | {:id 3 |
;; | 146516 | PushEv | {:id 6 | {:id 3 |   {:push |    true |      2020-1 |        |
;; | 146516 | PushEv | {:id 3 | {:id 9 |   {:push |    true |      2020-1 | {:id 1 |
;; | 146516 | Create | {:id 4 | {:id 3 |   {:ref  |    true |      2020-1 |        |

;; perhaps this provides a quick look at what's there?
;; full list of options at
;;https://github.com/techascent/tech.ml.dataset/blob/0ec98572dae64355ca1ab69b9209db17a810cad8/src/tech/ml/dataset/print.clj#L78


;; ** basic dataset manipulation

(first new-ds)
;; =>
[:a #tech.ml.dataset.column<int64>[3]
:a
[1, 2, 3, ]]

(:a new-ds)
;; => #tech.ml.dataset.column<int64>[3]
:a
[1, 2, 3, ]

(ds-col/missing
 (new-ds :c))
;; => #{2}

;; ** access to column values
(ds/->dataset
 {:age [1 2 3 4 5 6]
  :name ["a" "b" "c" "d" "e" "f"]})

(def nameage *1)

(nth (nameage :age) 0) ;; => 1
(nth (nameage :name) 5) ;; => "f"

(nameage :age)
;; => #tech.ml.dataset.column<int64>[6]
:age
[1, 2, 3, 4, 5, 6, ]

(require '[tech.v2.datatype :as dtype])

(dtype/->reader (nameage :age))
;; => [1 2 3 4 5 6]

;; TODO unclear what these accomplish
(dtype/->array-copy (nameage :age))
(type *1)

(ds/value-reader nameage)
;; => [[1 "a"] [2 "b"] [3 "c"] [4 "d"] [5 "e"] [6 "f"]]
(ds/mapseq-reader nameage)
;; => [{:name "a", :age 1} {:name "b", :age 2} {:name "c", :age 3} {:name "d", :age 4} {:name "e", :age 5} {:name "f", :age 6}]


;; ** subrect selection
(def ames-ds
  (ds/->dataset
   "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz"))

(ds/column-names ames-ds)
;; => ("Id" "MSSubClass" "MSZoning" "LotFrontage" "LotArea" "Street" "Alley" "LotShape" "LandContour" "Utilities" "LotConfig" "LandSlope" "Neighborhood" "Condition1" "Condition2" "BldgType" "HouseStyle" "OverallQual" "OverallCond" "YearBuilt" "YearRemodAdd" "RoofStyle" "RoofMatl" "Exterior1st" "Exterior2nd" "MasVnrType" "MasVnrArea" "ExterQual" "ExterCond" "Foundation" "BsmtQual" "BsmtCond" "BsmtExposure" "BsmtFinType1" "BsmtFinSF1" "BsmtFinType2" "BsmtFinSF2" "BsmtUnfSF" "TotalBsmtSF" "Heating" "HeatingQC" "CentralAir" "Electrical" "1stFlrSF" "2ndFlrSF" "LowQualFinSF" "GrLivArea" "BsmtFullBath" "BsmtHalfBath" "FullBath" "HalfBath" "BedroomAbvGr" "KitchenAbvGr" "KitchenQual" "TotRmsAbvGrd" "Functional" "Fireplaces" "FireplaceQu" "GarageType" "GarageYrBlt" "GarageFinish" "GarageCars" "GarageArea" "GarageQual" "GarageCond" "PavedDrive" "WoodDeckSF" "OpenPorchSF" "EnclosedPorch" "3SsnPorch" "ScreenPorch" "PoolArea" "PoolQC" "Fence" "MiscFeature" "MiscVal" "MoSold" "YrSold" "SaleType" "SaleCondition" "SalePrice")

(ames-ds "KitchenQual")
;; => #tech.ml.dataset.column<string>[1460]
KitchenQual
[Gd, TA, Gd, Gd, Gd, TA, Gd, TA, TA, TA, TA, Ex, TA, Gd, TA, TA, TA, TA, Gd, TA, ...]

(ames-ds "SalePrice")
;; => #tech.ml.dataset.column<int32>[1460]
SalePrice
[208500, 181500, 223500, 140000, 250000, 143000, 307000, 200000, 129900, 118000, 129500, 345000, 144000, 279500, 157000, 132000, 149000, 90000, 159000, 139000, ...]

(ds/select ames-ds
           ["KitchenQual" "SalePrice"]
           [1 3 5 7])
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [4 2]:

| KitchenQual | SalePrice |
|-------------|-----------|
|          TA |    181500 |
|          Gd |    140000 |
|          TA |    143000 |
|          TA |    200000 |

(ds/select-columns ames-ds
                   ["KitchenQual" "SalePrice"])


;; ** add, remove, update
(require '[tech.v2.datatype.functional :as dfn])

(def small-ames
  (ds/head
   (ds/select-columns ames-ds
                      ["KitchenQual" "SalePrice"])))

(assoc small-ames "SalePriceLog"
       (dfn/log (small-ames "SalePrice")))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 3]:

| KitchenQual | SalePrice | SalePriceLog |
|-------------|-----------|--------------|
|          Gd |    208500 |  12.24769432 |
|          TA |    181500 |  12.10901093 |
|          Gd |    223500 |  12.31716669 |
|          Gd |    140000 |  11.84939770 |
|          Gd |    250000 |  12.42921620 |

(assoc small-ames "Range" (range) "Constant-Col" :a)
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 4]:

| KitchenQual | SalePrice | Range | Constant-Col |
|-------------|-----------|-------|--------------|
|          Gd |    208500 |     0 |           :a |
|          TA |    181500 |     1 |           :a |
|          Gd |    223500 |     2 |           :a |
|          Gd |    140000 |     3 |           :a |
|          Gd |    250000 |     4 |           :a |

(dissoc small-ames "KitchenQual")
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 1]:

| SalePrice |
|-----------|
|    208500 |
|    181500 |
|    223500 |
|    140000 |
|    250000 |


;; ** sort-by, filter, group-by


(->
 (ds/filter #(< 30000 (get % "SalePrice")) ames-ds)
 (ds/select ["SalePrice" "KitchenQual"] (range 5)))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|    208500 |          Gd |
|    181500 |          TA |
|    223500 |          Gd |
|    140000 |          Gd |
|    250000 |          Gd |

(ds/select (ds/filter #(< 30000 (get % "SalePrice")) ames-ds)
           ["SalePrice" "KitchenQual"]
           (range 5))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|    208500 |          Gd |
|    181500 |          TA |
|    223500 |          Gd |
|    140000 |          Gd |
|    250000 |          Gd |

(->
 (ds/sort-by-column "SalePrice" ames-ds)
 (ds/select ["SalePrice" "KitchenQual"]
            (range 5)))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|     34900 |          TA |
|     35311 |          TA |
|     37900 |          TA |
|     39300 |          Fa |
|     40000 |          TA |

(ds/select
 (ds/sort-by-column "SalePrice" ames-ds)
 ["SalePrice" "KitchenQual"]
 (range 5))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|     34900 |          TA |
|     35311 |          TA |
|     37900 |          TA |
|     39300 |          Fa |
|     40000 |          TA |

;; changing the order gives different result because it's only working from first 5
(ds/sort-by-column "SalePrice"
                   (ds/select ames-ds
                              ["SalePrice" "KitchenQual"]
                              (range 5)))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|    140000 |          Gd |
|    181500 |          TA |
|    208500 |          Gd |
|    223500 |          Gd |
|    250000 |          Gd |


(def group-map (->> (ds/select ames-ds ["SalePrice" "KitchenQual"] (range 20))
                    (ds/group-by #(get % "KitchenQual"))))
(keys group-map)
;; => ("Ex" "TA" "Gd")
(first group-map)
;; => ["Ex" https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [1 2]:

| SalePrice | KitchenQual |
|-----------|-------------|
|    345000 |          Ex |


(def group-by-col (->> (ds/select ames-ds ["SalePrice" "KitchenQual"] (range 20))
                       (ds/group-by-column "KitchenQual"))) ;; simpler code
(keys group-by-col)
;; => ("Ex" "TA" "Gd")

(->> group-by-col
     (map (fn [[k v-ds]]
            (-> (ds/descriptive-stats v-ds)
                (ds/set-dataset-name k)))))
;; =>
(
 Ex [2 10]:

|   :col-name | :datatype | :n-valid | :n-missing |     :min |    :mean | :mode |     :max | :standard-deviation | :skew |
|-------------|-----------|----------|------------|----------|----------|-------|----------|---------------------|-------|
| KitchenQual |   :string |        1 |          0 |          |          |    Ex |          |                     |       |
|   SalePrice |    :int32 |        1 |          0 | 345000.0 | 345000.0 |       | 345000.0 |                 0.0 |       |
 TA [2 10]:

|   :col-name | :datatype | :n-valid | :n-missing |    :min |           :mean | :mode |     :max | :standard-deviation |      :skew |
|-------------|-----------|----------|------------|---------|-----------------|-------|----------|---------------------|------------|
| KitchenQual |   :string |       12 |          0 |         |                 |    TA |          |                     |            |
|   SalePrice |    :int32 |       12 |          0 | 90000.0 | 142741.66666667 |       | 200000.0 |      28425.83518669 | 0.38710319 |
 Gd [2 10]:

|   :col-name | :datatype | :n-valid | :n-missing |     :min |           :mean | :mode |     :max | :standard-deviation |       :skew |
|-------------|-----------|----------|------------|----------|-----------------|-------|----------|---------------------|-------------|
| KitchenQual |   :string |        7 |          0 |          |                 |    Gd |          |                     |             |
|   SalePrice |    :int32 |        7 |          0 | 140000.0 | 223928.57142857 |       | 307000.0 |      60782.00704939 | -0.11012873 |
)

;; ** descriptive stats and groupby and datetime types
(def stocks
  (ds/->dataset
   "https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv"))
(ds/head stocks)
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv [5 3]:

| symbol |       date | price |
|--------|------------|-------|
|   MSFT | 2000-01-01 | 39.81 |
|   MSFT | 2000-02-01 | 36.35 |
|   MSFT | 2000-03-01 | 43.22 |
|   MSFT | 2000-04-01 | 28.37 |
|   MSFT | 2000-05-01 | 25.45 |

(set (stocks "symbol"))
;; => #{"GOOG" "AAPL" "AMZN" "MSFT" "IBM"}

(->> (ds/group-by-column "symbol" stocks)
     (map (fn [[k v]] (ds/descriptive-stats v))))
;; =>
(MSFT: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |      :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|------------|
|      date | :packed-local-date |      123 |          0 | 2000-01-01 | 2005-01-30 |       | 2010-03-01 |      9.37554538E+10 | 0.00025335 |
|     price |           :float64 |      123 |          0 |      15.81 |      24.74 |       |      43.22 |      4.30395786E+00 | 1.16559225 |
|    symbol |            :string |      123 |          0 |            |            |  MSFT |            |                     |            |
 GOOG: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |       :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|-------------|
|      date | :packed-local-date |       68 |          0 | 2004-08-01 | 2007-05-17 |       | 2010-03-01 |      5.20003989E+10 |  0.00094625 |
|     price |           :float64 |       68 |          0 |      102.4 |      415.9 |       |      707.0 |      1.35069851E+02 | -0.22776524 |
|    symbol |            :string |       68 |          0 |            |            |  GOOG |            |                     |             |
 AAPL: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |      :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|------------|
|      date | :packed-local-date |      123 |          0 | 2000-01-01 | 2005-01-30 |       | 2010-03-01 |      9.37554538E+10 | 0.00025335 |
|     price |           :float64 |      123 |          0 |      7.070 |      64.73 |       |      223.0 |      6.31237823E+01 | 0.93215285 |
|    symbol |            :string |      123 |          0 |            |            |  AAPL |            |                     |            |
 IBM: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |      :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|------------|
|      date | :packed-local-date |      123 |          0 | 2000-01-01 | 2005-01-30 |       | 2010-03-01 |      9.37554538E+10 | 0.00025335 |
|     price |           :float64 |      123 |          0 |      53.01 |      91.26 |       |      130.3 |      1.65133647E+01 | 0.44446266 |
|    symbol |            :string |      123 |          0 |            |            |   IBM |            |                     |            |
 AMZN: descriptive-stats [3 10]:

| :col-name |          :datatype | :n-valid | :n-missing |       :min |      :mean | :mode |       :max | :standard-deviation |      :skew |
|-----------|--------------------|----------|------------|------------|------------|-------|------------|---------------------|------------|
|      date | :packed-local-date |      123 |          0 | 2000-01-01 | 2005-01-30 |       | 2010-03-01 |      9.37554538E+10 | 0.00025335 |
|     price |           :float64 |      123 |          0 |      5.970 |      47.99 |       |      135.9 |      2.88913206E+01 | 0.98217538 |
|    symbol |            :string |      123 |          0 |            |            |  AMZN |            |                     |            |
)


;; ** elementwise operations
(def updated-ames
  (ds/add-or-update-column ames-ds
                           "TotalBath"
                           (dfn/+ (ames-ds "BsmtFullBath") ;;TODO can external fn be used here?
                                  (dfn/* 0.5 (ames-ds "BsmtHalfBath"))
                                  (ames-ds "FullBath")
                                  (dfn/* 0.5 (ames-ds "HalfBath")))))
(ds/head
 (ds/select-columns updated-ames
                    ["BsmtFullBath" "BsmtHalfBath" "FullBath" "HalfBath" "TotalBath"]))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 5]:

| BsmtFullBath | BsmtHalfBath | FullBath | HalfBath | TotalBath |
|--------------|--------------|----------|----------|-----------|
|            1 |            0 |        2 |        1 |       3.5 |
|            0 |            1 |        2 |        0 |       2.5 |
|            1 |            0 |        2 |        1 |       3.5 |
|            1 |            0 |        1 |        0 |       2.0 |
|            1 |            0 |        2 |        1 |       3.5 |


;; TODO need to understand object-reader
(def named-baths
  (assoc
   updated-ames
   "NamedBath"
   (let [total-baths (updated-ames "TotalBath")]
     (dtype/object-reader
      (count total-baths)
      (fn [idx]
        (let [tbaths (double (total-baths idx))]
          (cond
            (< tbaths 1.0) "almost none"
            (< tbaths 2.0) "somewhat doable"
            (< tbaths 3.0) "getting somewhere"
            :else "living in style")))
      :string))))
(ds/head
 (ds/select-columns named-baths
                    ["TotalBath" "NamedBath"]))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 2]:

| TotalBath |         NamedBath |
|-----------|-------------------|
|       3.5 |   living in style |
|       2.5 | getting somewhere |
|       3.5 |   living in style |
|       2.0 | getting somewhere |
|       3.5 |   living in style |


;;TODO rewrite using add-or-update


(def sorted-named-baths
  (ds/sort-by-column "TotalBath"
                     >
                     named-baths))
(ds/head
 (ds/select-columns sorted-named-baths
                    ["TotalBath" "NamedBath" "SalePrice"]))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/ames-train.csv.gz [5 3]:

| TotalBath |       NamedBath | SalePrice |
|-----------|-----------------|-----------|
|       6.0 | living in style |    179000 |
|       5.0 | living in style |    145900 |
|       4.5 | living in style |    160000 |
|       4.5 | living in style |    745000 |
|       4.5 | living in style |    250000 |



;; ** datetime types
;; https://github.com/techascent/tech.datatype/blob/master/docs/datetime.md

(ds/head stocks)
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv [5 3]:

| symbol |       date | price |
|--------|------------|-------|
|   MSFT | 2000-01-01 | 39.81 |
|   MSFT | 2000-02-01 | 36.35 |
|   MSFT | 2000-03-01 | 43.22 |
|   MSFT | 2000-04-01 | 28.37 |
|   MSFT | 2000-05-01 | 25.45 |


(dtype/get-datatype (stocks "date"))
;; => :packed-local-date

(require '[tech.v2.datatype.datetime.operations :as dtype-dt-ops])

(ds/head
 (ds/update-column stocks "date"
                   dtype-dt-ops/get-epoch-milliseconds))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/stocks.csv [5 3]:

| symbol |                 date | price |
|--------|----------------------|-------|
|   MSFT | 2000-01-01T00:00:00Z | 39.81 |
|   MSFT | 2000-02-01T00:00:00Z | 36.35 |
|   MSFT | 2000-03-01T00:00:00Z | 43.22 |
|   MSFT | 2000-04-01T00:00:00Z | 28.37 |
|   MSFT | 2000-05-01T00:00:00Z | 25.45 |


;;How about the yearly averages by symbol of the stocks
(->> (ds/add-or-update-column stocks "years"
                              (dtype-dt-ops/get-years (stocks "date")))
     (ds/group-by (juxt "symbol" "years"))
     (vals)
     ;;stream is a sequence of datasets at this point.
     (map (fn [ds]
            {"symbol" (first (ds "symbol"))
             "years" (first (ds "years"))
             "avg-price" (dfn/mean (ds "price"))}))
     (sort-by (juxt "symbol" "years"))
     (ds/->>dataset)
     (ds/head 10))
;; above gave error
;; class java.lang.String cannot be cast to class clojure.lang.IFn
;; because the "columns" need to be :columns
;; as done in def nstocks

(def nstocks
  (ds/rename-columns stocks {"symbol" :symbol "date" :date "price" :price }))

(->> (ds/add-or-update-column nstocks :years
                              (dtype-dt-ops/get-years (nstocks :date)))
     (ds/group-by (juxt :symbol :years))
     (vals)
     ;;stream is a sequence of datasets at this point.
     (map (fn [ds]
            {:symbol (first (ds :symbol))
             :years (first (ds :years))
             :avg-price (dfn/mean (ds :price))}))
     (sort-by (juxt :symbol :years))
     (ds/->>dataset)
     (ds/head 24))
;; => _unnamed [24 3]:

| :symbol | :years |   :avg-price |
|---------|--------|--------------|
|    AAPL |   2000 |  21.74833333 |
|    AAPL |   2001 |  10.17583333 |
|    AAPL |   2002 |   9.40833333 |
|    AAPL |   2003 |   9.34750000 |
|    AAPL |   2004 |  18.72333333 |
|    AAPL |   2005 |  48.17166667 |
|    AAPL |   2006 |  72.04333333 |
|    AAPL |   2007 | 133.35333333 |
|    AAPL |   2008 | 138.48083333 |
|    AAPL |   2009 | 150.39333333 |
|    AAPL |   2010 | 206.56666667 |
|    AMZN |   2000 |  43.93083333 |
|    AMZN |   2001 |  11.73916667 |
|    AMZN |   2002 |  16.72333333 |
|    AMZN |   2003 |  39.01666667 |
|    AMZN |   2004 |  43.26750000 |
|    AMZN |   2005 |  40.18750000 |
|    AMZN |   2006 |  36.25166667 |
|    AMZN |   2007 |  69.95250000 |
|    AMZN |   2008 |  69.01500000 |
|    AMZN |   2009 |  90.73083333 |
|    AMZN |   2010 | 124.21000000 |
|    GOOG |   2004 | 159.47600000 |
|    GOOG |   2005 | 286.47250000 |

;; so this is really good because we can add columns
;; and group things by some criteria


;; ** joins
;; defer

;; ** xls, xlsx files
;; not interested right now
;; except for discussion on parsing
(ds/head
 (ds/update-column xls-data "Date"
                   (partial ds-col/parse-column
                            [:local-date "dd/MM/yyyy"])))
;; => https://github.com/techascent/tech.ml.dataset/raw/master/test/data/file_example_XLS_1000.xls [5 8]:

| column-0 | First Name | Last Name | Gender |       Country |  Age |       Date |     Id |
|----------|------------|-----------|--------|---------------|------|------------|--------|
|      1.0 |      Dulce |     Abril | Female | United States | 32.0 | 2017-10-15 | 1562.0 |
|      2.0 |       Mara | Hashimoto | Female | Great Britain | 25.0 | 2016-08-16 | 1582.0 |
|      3.0 |     Philip |      Gent |   Male |        France | 36.0 | 2015-05-21 | 2587.0 |
|      4.0 |   Kathleen |    Hanner | Female | United States | 25.0 | 2017-10-15 | 3549.0 |
|      5.0 |    Nereida |   Magwood | Female | United States | 58.0 | 2016-08-16 | 2468.0 |

;; so if you have dataset already use ds-col/parse-column
;; instead of the parser-fn



;; * zulip discussions
;; ** column parsing vs file type?
                                        ;parser-fn works for json but not for csv FIXED

(-> "https://covidtracking.com/api/v1/us/daily.json"
    (ds/->dataset
     {:key-fn keyword
      :parser-fn {:dateChecked
                  [:string (fn[d] (-> d (s/split #"T") first))]}})
    (->> (ds/filter #(% :death) [:death]))
    (ds/select-columns [:death :dateChecked :date])
    (ds/descriptive-stats))
;; => https://covidtracking.com/api/v1/us/daily.json: descriptive-stats [3 10]:

|    :col-name | :datatype | :n-valid | :n-missing |           :min |          :mean |      :mode |           :max | :standard-deviation |       :skew |
|--------------|-----------|----------|------------|----------------|----------------|------------|----------------|---------------------|-------------|
|        :date |    :int64 |      305 |          0 | 2.02002260E+07 | 2.02007512E+07 |            | 2.02012260E+07 |        288.65458701 | -0.00063271 |
| :dateChecked |   :string |      305 |          0 |                |                | 2020-05-13 |                |                     |             |
|       :death |    :int64 |      305 |          0 | 2.00000000E+00 | 1.40956980E+05 |            | 3.23401000E+05 |      87768.82682252 | -0.02922670 |

;; => https://covidtracking.com/api/v1/us/daily.json: descriptive-stats [3 10]:

|    :col-name | :datatype | :n-valid | :n-missing |           :min |          :mean |      :mode |           :max | :standard-deviation |       :skew |
|--------------|-----------|----------|------------|----------------|----------------|------------|----------------|---------------------|-------------|
|        :date |    :int64 |      304 |          0 | 2.02002260E+07 | 2.02007496E+07 |            | 2.02012250E+07 |        287.83649329 |  0.00083181 |
| :dateChecked |   :string |      304 |          0 |                |                | 2020-05-13 |                |                     |             |
|       :death |    :int64 |      304 |          0 | 2.00000000E+00 | 1.40356836E+05 |            | 3.21992000E+05 |      87284.44913162 | -0.03938963 |


(-> "https://covidtracking.com/api/v1/us/daily.csv"
    (ds/->dataset
     {:key-fn keyword
      :parser-fn {:dateChecked
                  [:string (fn[d] (-> d (s/split #"T") first))]}})
    (->> (ds/filter #(% :death) [:death]))
    (ds/select-columns [:death :dateChecked :date])
    (ds/descriptive-stats))
;; => https://covidtracking.com/api/v1/us/daily.csv: descriptive-stats [3 10]:

|    :col-name | :datatype | :n-valid | :n-missing |           :min |          :mean |                :mode |           :max | :standard-deviation |       :skew |
|--------------|-----------|----------|------------|----------------|----------------|----------------------|----------------|---------------------|-------------|
|        :date |    :int32 |      304 |          0 | 2.02002260E+07 | 2.02007496E+07 |                      | 2.02012250E+07 |        287.83649329 |  0.00083181 |
| :dateChecked |   :string |      304 |          0 |                |                | 2020-10-23T24:00:00Z |                |                     |             |
|       :death |    :int32 |      304 |          0 | 2.00000000E+00 | 1.40356836E+05 |                      | 3.21992000E+05 |      87284.44913162 | -0.03938963 |

