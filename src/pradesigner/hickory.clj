"
hickory
https://github.com/davidsantiago/hickory
http://davidsantiago.github.io/hickory/
"

(ns user
  (:require [hickory.core :as hk]
            [hickory.zip :as hkz]
            [hickory.render :as hkr]
            [hickory.select :as hks]
            [hiccup.core :as hc]
            [clojure.zip :as zip]
            [clj-http.client :as client]
            [clojure.string :as str]))


(def tf (slurp "http://towardsfreedom.com"))
(def ptf (hk/parse tf)) ;jsouped


;; * parsing examples
(def peg (hk/parse "<a href=\"foo\">foo</a>"))
peg
;; => #object[org.jsoup.nodes.Document 0x3986d3ae "<html>\n <head></head>\n <body>\n  <a href=\"foo\">foo</a>\n </body>\n</html>"]
(hk/as-hiccup peg)
;; => ([:html {} [:head {}] [:body {} [:a {:href "foo"} "foo"]]])
(hk/as-hickory peg)
;; => {:type :document, :content [{:type :element, :attrs nil, :tag :html, :content [{:type :element, :attrs nil, :tag :head, :content nil} {:type :element, :attrs nil, :tag :body, :content [{:type :element, :attrs {:href "foo"}, :tag :a, :content ["foo"]}]}]}]}

(def pfeg (hk/parse-fragment "<a href=\"foo\">foo</a>"))
pfeg
;; => [#object[org.jsoup.nodes.Element 0x7c5524b2 "<a href=\"foo\">foo</a>"]]
(hk/as-hiccup pfeg) ;;error
(map hk/as-hiccup pfeg)
;; => ([:a {:href "foo"} "foo"])
(hk/as-hickory pfeg) ;;error
(map hk/as-hickory pfeg)
;; => ({:type :element, :attrs {:href "foo"}, :tag :a, :content ["foo"]})



;; * zipping examples
(def h "<a href=foo>bar<br></a>")

(-> (hk/parse h))
(-> (hk/parse h)
    (hk/as-hiccup));; => ([:html {} [:head {}] [:body {} [:a {:href "foo"} "bar" [:br {}]]]])
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip));; => [([:html {} [:head {}] [:body {} [:a {:href "foo"} "bar" [:br {}]]]]) nil]
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/node));; => ([:html {} [:head {}] [:body {} [:a {:href "foo"} "bar" [:br {}]]]])
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/next)
    (zip/node));; => [:html {} [:head {}] [:body {} [:a {:href "foo"} "bar" [:br {}]]]]
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/next)
    (zip/next)
    (zip/node));; => [:head {}]
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/node));; => [:body {} [:a {:href "foo"} "bar" [:br {}]]]
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/node));; => [:a {:href "foo"} "bar" [:br {}]]
(-> (hk/parse h)
    (hk/as-hiccup)
    (hkz/hiccup-zip)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/next)
    (zip/node));; => "bar"

(-> (hk/parse h)
    hk/as-hiccup
    hkz/hiccup-zip
    zip/next
    zip/next
    (zip/replace [:head {:id "a"}])
    zip/node)
;; => [:head {:id "a"}]

(-> (hk/parse h)
    hk/as-hiccup
    hkz/hiccup-zip
    zip/next
    zip/next
    (zip/replace [:head {:id "a"}])
    zip/root)
;; => ([:html {} [:head {:id "a"}] [:body {} [:a {:href "foo"} "bar" [:br {}]]]])

(-> (hk/parse h)
    hk/as-hickory
    hkz/hickory-zip
    zip/next
    zip/next
    (zip/replace {:type :element :tag :head :attrs {:id "a"} :content nil})
    zip/root)
;; => {:type :document, :content [{:type :element, :attrs nil, :tag :html, :content [{:type :element, :tag :head, :attrs {:id "a"}, :content nil} {:type :element, :attrs nil, :tag :body, :content [{:type :element, :attrs {:href "foo"}, :tag :a, :content ["bar" {:type :element, :attrs nil, :tag :br, :content nil}]}]}]}]}

(hkr/hickory-to-html *1)
;; => "&lt;html&gt;&lt;head id=&quot;a&quot;&gt;&lt;/head&gt;&lt;body&gt;&lt;a href=&quot;foo&quot;&gt;bar&lt;br&gt;&lt;/a&gt;&lt;/body&gt;&lt;/html&gt;"
;;"<html><head id=\"a\"></head><body><a href=\"foo\">bar<br></a></body></html>"

;; * selectors

(def site-htree (-> (client/get "http://formula1.com/default.html")
                    :body
                    hk/parse
                    hk/as-hickory))

(def ptf (-> (client/get "http://towardsfreedom.com")
             :body
             hk/parse
             hk/as-hickory))

(-> (hks/select (hks/tag :i)
                ptf)
    first :content first)
;; => "... with you on your journey"



(hks/select (hks/node-type :element) ptf)

(hks/select (hks/tag :p) ptf)

(hks/select (hks/class "whern") ptf)

(hks/select (hks/attr :class) ptf)
(hks/select (hks/attr :class #(.equals % "whern")) ptf)
(hks/select (hks/attr :class #(.startsWith % "sec")) ptf)
(hks/select (hks/attr :class #(.contains % "itm")) ptf)


(def pfone (-> (client/get "http://formula1.com/default.html")
                :body
                hk/parse
                hk/as-hickory))

(hks/select (hks/id :raceDates) pfone)

(re-find #"id..............." (str pfone))


(def pBYND (-> (client/get "https://finance.yahoo.com/quote/BYND/profile")
               :body
               hk/parse
               hk/as-hickory))

(hks/select (hks/and (hks/tag :p)
                     (hks/attr :class #(.contains "Mt(15px) Lh(1.6)")))
            pBYND);;

(hks/select (hks/attr :class #(.equals % "Mt(15px) Lh(1.6)")) pBYND)

(-> (hks/select (hks/attr :class #(.equals % "Mt(15px) Lh(1.6)")) pBYND)
    first :content first)

(-> (hks/select (hks/attr :data-reactid #(.equals % "221")) pBYND)
    first :content first)

(def text (comp first :content first))
(-> (hks/select (hks/and (hks/attr :data-reactid #(.equals % "221"))
                         (hks/attr :class #(.equals % "Mt(15px) Lh(1.6)")))
                pBYND)
    text)




;; TODO get hks/and to work and pull only contents efficiently

(nth (hks/select (hks/tag :p) pBYND) 2)
;; => {:type :element, :attrs {:class "Mt(15px) Lh(1.6)", :data-reactid "221"}, :tag :p, :content ["Beyond Meat, Inc., a food company, manufactures, markets, and sells plant-based meat products in the United States and internationally. It operates under the Beyond Meat, Beyond Burger, Beyond Beef, Beyond Sausage, Beyond Breakfast Sausage, Beyond Chicken, Beyond Fried Chicken, Beyond Meatball, the Caped Steer Logo, GO BEYOND, Eat What You Love, The Cookout Classic, The Future of Protein, and The Future of Protein Beyond Meat trademarks. The company sells its products through grocery, mass merchandiser, club and convenience store, natural retailer channels, direct to consumer, restaurants, foodservice outlets, and schools. The company was formerly known as Savage River, Inc. and changed its name to Beyond Meat, Inc. in September 2018. Beyond Meat, Inc. was founded in 2009 and is headquartered in El Segundo, California."]}

(hks/select (hks/tag :p) pBYND)
;; => [{:type :element, :attrs {:class "D(ib) W(47.727%) Pend(40px)", :data-reactid "8"}, :tag :p, :content [{:type :comment, :content [" react-text: 9 "]} "119 Standard Street" {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:data-reactid "10"}, :tag :br, :content nil} {:type :comment, :content [" react-text: 11 "]} "El Segundo, CA 90245" {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:data-reactid "12"}, :tag :br, :content nil} {:type :comment, :content [" react-text: 13 "]} "United States" {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:data-reactid "14"}, :tag :br, :content nil} {:type :element, :attrs {:href "tel:8667564112", :class "C($linkColor)", :data-reactid "15"}, :tag :a, :content ["866 756 4112"]} {:type :element, :attrs {:data-reactid "16"}, :tag :br, :content nil} {:type :element, :attrs {:href "http://www.beyondmeat.com", :rel "noopener noreferrer", :target "_blank", :class "C($linkColor)", :title "", :data-reactid "17"}, :tag :a, :content ["http://www.beyondmeat.com"]}]} {:type :element, :attrs {:class "D(ib) Va(t)", :data-reactid "18"}, :tag :p, :content [{:type :element, :attrs {:data-reactid "19"}, :tag :span, :content ["Sector(s)"]} {:type :comment, :content [" react-text: 20 "]} ": " {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:class "Fw(600)", :data-reactid "21"}, :tag :span, :content ["Consumer Defensive"]} {:type :element, :attrs {:data-reactid "22"}, :tag :br, :content nil} {:type :element, :attrs {:data-reactid "23"}, :tag :span, :content ["Industry"]} {:type :comment, :content [" react-text: 24 "]} ": " {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:class "Fw(600)", :data-reactid "25"}, :tag :span, :content ["Packaged Foods"]} {:type :element, :attrs {:data-reactid "26"}, :tag :br, :content nil} {:type :element, :attrs {:data-reactid "27"}, :tag :span, :content ["Full Time Employees"]} {:type :comment, :content [" react-text: 28 "]} ": " {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:class "Fw(600)", :data-reactid "29"}, :tag :span, :content [{:type :element, :attrs {:data-reactid "30"}, :tag :span, :content ["472"]}]}]} {:type :element, :attrs {:class "Mt(15px) Lh(1.6)", :data-reactid "221"}, :tag :p, :content ["Beyond Meat, Inc., a food company, manufactures, markets, and sells plant-based meat products in the United States and internationally. It operates under the Beyond Meat, Beyond Burger, Beyond Beef, Beyond Sausage, Beyond Breakfast Sausage, Beyond Chicken, Beyond Fried Chicken, Beyond Meatball, the Caped Steer Logo, GO BEYOND, Eat What You Love, The Cookout Classic, The Future of Protein, and The Future of Protein Beyond Meat trademarks. The company sells its products through grocery, mass merchandiser, club and convenience store, natural retailer channels, direct to consumer, restaurants, foodservice outlets, and schools. The company was formerly known as Savage River, Inc. and changed its name to Beyond Meat, Inc. in September 2018. Beyond Meat, Inc. was founded in 2009 and is headquartered in El Segundo, California."]} {:type :element, :attrs {:class "Fz(s)", :data-reactid "226"}, :tag :p, :content [{:type :element, :attrs {:data-reactid "227"}, :tag :span, :content [{:type :comment, :content [" react-text: 228 "]} "Beyond Meat, Inc.’s ISS Governance QualityScore as of " {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:data-reactid "229"}, :tag :span, :content ["December 2, 2020"]} {:type :comment, :content [" react-text: 230 "]} " is 9." {:type :comment, :content [" /react-text "]}]} {:type :comment, :content [" react-text: 231 "]} "  " {:type :comment, :content [" /react-text "]} {:type :element, :attrs {:data-reactid "232"}, :tag :span, :content ["The pillar scores are Audit: 1; Board: 8; Shareholder Rights: 8; Compensation: 10."]}]}]




