"
enlive and hiccup explorations
templating - use hiccup to create the html template and enlive to fill it
scraping
"

(ns user
  (:require [net.cgrand.enlive-html :as html]
            [hiccup.core :as hc]
            [clojure.java.io :as io]))

;; * swannodette An Introduction to Enlive scraping
https://github.com/swannodette/enlive-tutorial

;; ** scrape1
(def ^:dynamic *base-url* "https://news.ycombinator.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn hn-headlines []
  (map html/text (html/select (fetch-url *base-url*) [:td.title :a])))

(defn hn-points []
  (map html/text (html/select (fetch-url *base-url*) [:td.subtext html/first-child])))

(defn print-headlines-and-points []
  (doseq [line (map #(str %1 " (" %2 ")") (hn-headlines) (hn-points))]
    (println line)))

;; ** scrape2 - add on
(defn hn-headlines-and-points []
  (map html/text
       (html/select (fetch-url *base-url*)
                    #{[:td.title :a] [:td.subtext html/first-child]})))

(defn print-headlines-and-points []
  (doseq [line (map (fn [[h s]] (str h " (" s ")"))
                    (partition 2 (hn-headlines-and-points)))]
    (println line)))

;; ** scrape3 - doesn't seem to work anymore
(def ^:dynamic *base-url* "https://nytimes.com/")

(def ^:dynamic *story-selector*
     [[:article.story
       (html/but :.advertisement)
       (html/but :.autosStory)
       (html/but :.adCreative)]])

(def ^:dynamic *headline-selector*
     #{[html/root :> :h2 :a],
       [html/root :> :h3 :a]
       [html/root :> :h5 :a]})

(def ^:dynamic *byline-selector* [html/root :> :.byline])

(def ^:dynamic *summary-selector* [html/root :> :.summary])

(defn split-on-space [word]
  "Splits a string on words"
  (clojure.string/split word #"\s+"))

(defn squish [line]
  (s/triml (s/join " "
     (split-on-space (s/replace line #"\n" " ")))))

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn stories []
  (html/select (fetch-url *base-url*) *story-selector*))

(defn extract [node]
  (let [headline (first (html/select [node] *headline-selector*))
        byline   (first (html/select [node] *byline-selector*))
        summary  (first (html/select [node] *summary-selector*))
        result   (map html/text [headline byline summary])]
    (zipmap [:headline :byline :summary] (map squish result))))

(defn empty-story? [node]
  (every? (fn [[k v]] (= v "")) node))

(defn check [story key default]
  (let [v (key story)]
   (if (not= v "") v default)))

(defn print-story [story]
  (println)
  (println (check story :headline "No headline"))
  (println "\t" (check story :byline "No byline"))
  (println "\t" (check story :summary "No summary")))

(defn print-stories []
  (doseq [story (remove empty-story? (map extract (stories)))]
    (print-story story)))

;; * HTML generation in Clojure with Enlive
https://kaal-daari.medium.com/html-generation-in-clojure-with-enlive-18952cf45d15

(hc/html [:div [:h1]])
;; => "<div><h1></h1></div>"
(hc/html [:div [:h1 "heading"]])
;; => "<div><h1>heading</h1></div>"
(hc/html [:div [:h1 {:class "url"} "Heading" ]])
;; => "<div><h1 class=\"url\">Heading</h1></div>"

;; ** Adding element attributes
(def snip1 (hc/html [:div]))
(html/sniptest snip1 (html/add-class "abc"))
;; => "<div class=\"abc\"></div>"
(html/sniptest snip1 (html/content "dev"))
;; => "<div>dev</div>"

;; ** Changing the url in a link
(def snip2 (hc/html [:div {:class "url"}
                     [:a {:href "http://clojure.org"} "Clojure - home"]] ))
(html/sniptest snip2
               [:div :a]
               (html/set-attr :href "http://en.wikipedia.org/wiki/Clojure"))
;; => "<div class=\"url\"><a href=\"http://en.wikipedia.org/wiki/Clojure\">Clojure - home</a></div>"

;; ** Changing content of lists
(def snip3 (hc/html [:body [:h1 "heading"]
                     [:ul
                      [:li "one"]
                      [:li "two"]]]))
(html/sniptest snip3
               [:body :ul :li] ;; just [:li] works too
               (html/content "abcd"))
;; => "<body><h1>heading</h1><ul><li>abcd</li><li>abcd</li></ul></body>"

(html/sniptest snip3
               [[:li html/first-child]]
               (html/content "abcd"))
;; => "<body><h1>heading</h1><ul><li>abcd</li><li>two</li></ul></body>"

(html/sniptest snip3
               [[html/first-of-type :li]]
               (html/content "abcd"))
;; => "<body><h1>heading</h1><ul><li>abcd</li><li>two</li></ul></body>"

;; ** Replacing a list in a template with our own data.
(def data ["Omkara","Blue Umbrella","Kaminey","Haider"])

(html/sniptest snip3
               ;;match the first list index
               [[html/first-of-type :li]]
               ;;clone the first list index, and for each element in data, add a new list index
               (html/clone-for [item data]
                               [:li] (html/content item)))
;; => "<body><h1>heading</h1><ul><li>Omkara</li><li>Blue Umbrella</li><li>Kaminey</li><li>Haider</li><li>two</li></ul></body>"

;; ** Replacing nested trees
(def data2 [{:title "Designation", :data [{:text "New Hire ", :href "www.orgname.org/newhire"}
                                          {:text "Manager ", :href "www.orgname.org/manager"} ]}
            {:title "Group", :data [{:text "IT ", :href "www.orgname.org/IT"}
                                    {:text "Customer Advocacy ", :href "www.orgname.org/advocacy"}]}])

(def snip4 (hc/html
            [:body
             [:h1 {:class "title"} "heading"]
             [:ul {:id "grouplevel"}
              [:li [:a {:href "cnn.com"} "content"]]]]))

(html/sniptest snip4
               ;;select the tags from the <h1> tag till the <ul class="grouplevel"> tag
               {[:h1] [[html/first-of-type :ul#grouplevel]]}
               ;;for each list item in data2
               (html/clone-for [{:keys [title data]} data2]
                               ;;set the content for the <h1> tag
                               [:h1] (html/content title)
                               ;;for the <li> items
                               [:ul#grouplevel [:li html/first-of-type]]
                               ;;for all the items in the :data section create a <li><a> tag
                               (html/clone-for [{:keys [text href]} data]
                                               [:li :a] (html/set-attr :href href)
                                               [:li :a] (html/content text))))

;; * Choosing a templating language in clojure
http://radar.oreilly.com/2014/03/choosing-a-templating-language-in-clojure.html

;; ** hiccup
(hc/html [:a
          {:href "http://www.rkn.io/application-architecture"}
          "Application Architecture for Developers"])
;; => "<a href=\"http://www.rkn.io/application-architecture\">Application Architecture for Developers</a>"

(hc/html [:ul {:class "groceries"}
          [:li "Apples"]
          [:li "Bananas"]
          [:li "Pears"]])
;; => "<ul class=\"groceries\"><li>Apples</li><li>Bananas</li><li>Pears</li></ul>"

(defn grocery-list
  "Transform a list of grocery items into a unordered list"
  [items]
  [:ul {:class "groceries"}
    (for [item items]
      [:li item])])
(hc/html(grocery-list ["cheese" "eggs"]))
;; => "<ul class=\"groceries\"><li>cheese</li><li>eggs</li></ul>"


;; ** enlive

;; Define the template
(html/deftemplate post-page "post.html"
  [post]
  [:title]         (html/content (:title post))
  [:h1]            (html/content (:title post))
  [:span.author]   (html/content (:author post))
  [:div.post-body] (html/content (:body post)))

;; Some sample data
(def sample-post {:author "Luke VanderHart"
                  :title "Why Clojure Rocks"
                  :body "Functional programming!"})

(reduce str (post-page sample-post))
"
<html>
  <head><title>Why Clojure Rocks</title></head>
  <body>
    <h1>Why Clojure Rocks</h1>
    <h3>By <span class=\"author\">Luke VanderHart</span></h3>
    <div class=\"post-body\">Functional programming!</div>
  </body>
</html>
"

(def sample-post-list
  [{:author "Luke VanderHart"
    :title "Why Clojure Rocks"
    :body "Functional programming!"}
   {:author "Ryan Neufeld"
    :title "Clojure Community Management"
    :body "Programmers are like..."}
   {:author "Rich Hickey"
    :title "Programming"
    :body "You're doing it completely wrong."}])

(html/defsnippet post-snippet "post.html"
  {[:h1] [[:div.post-body (html/nth-of-type 1)]]}
  [post]
  [:h1] (html/content (:title post))
  [:span.author] (html/content (:author post))
  [:div.post-body] (html/content (:body post)))

(html/deftemplate all-posts-page "post.html"
  [post-list]
  [:title] (html/content "All Posts")
  [:body] (html/content (map post-snippet post-list)))

(reduce str (all-posts-page sample-post-list))
"
<html>
  <head><title>All Posts</title></head>
  <body>
    <h1>Why Clojure Rocks</h1>
    <h3>By <span class=\"author\">Luke VanderHart</span></h3>
    <div class=\"post-body\">Functional programming!</div>
    <h1>Clojure Community Management</h1>
    <h3>By <span class=\"author\">Ryan Neufeld</span></h3>
    <div class=\"post-body\">Programmers are like...</div>
    <h1>Programming</h1>
    <h3>By <span class=\"author\">Rich Hickey</span></h3>
    <div class=\"post-body\">You're doing it completely wrong.</div>
  </body>
</html>
"

;; ** selmer


;; * Building my Static Website with Clojure
https://nickgeorge.net/programming/building-my-static-clojure-website/
;; * Building a Database-Backed Clojure Web Application
https://devcenter.heroku.com/articles/clojure-web-application
;; * stockscraper example
(html/select t [:p :span ])
({:tag :span, :attrs {:data-reactid "21"}, :content ("Sector(s)")}
 {:tag :span,
  :attrs {:class "Fw(600)", :data-reactid "23"},
  :content ("Basic Materials")}
 {:tag :span, :attrs {:data-reactid "25"}, :content ("Industry")}
 {:tag :span,
  :attrs {:class "Fw(600)", :data-reactid "27"},
  :content ("Gold")}
 {:tag :span,
  :attrs {:data-reactid "29"},
  :content ("Full Time Employees")}
 {:tag :span,
  :attrs {:class "Fw(600)", :data-reactid "31"},
  :content
  ({:tag :span, :attrs {:data-reactid "32"}, :content ("9")})}
 {:tag :span, :attrs {:data-reactid "32"}, :content ("9")}
 {:tag :span,
  :attrs {:data-reactid "162"},
  :content
  ("Almaden Minerals Ltd.’s ISS Governance QualityScore as of N/A is N/A.")}
 {:tag :span,
  :attrs {:data-reactid "164"},
  :content
  ("The pillar scores are Audit: N/A; Board: N/A; Shareholder Rights: N/A; Compensation: N/A.")})

(html/select t [:p (html/attr= :data-reactid "164")])
({:tag :span,
  :attrs {:data-reactid "164"},
  :content
  ("The pillar scores are Audit: N/A; Board: N/A; Shareholder Rights: N/A; Compensation: N/A.")})

(html/text (first (html/select t [:p (html/attr= :data-reactid "164")])))
"The pillar scores are Audit: N/A; Board: N/A; Shareholder Rights: N/A; Compensation: N/A."

(html/text (first (html/select t [:p (html/attr= :data-reactid "162")])))
"Almaden Minerals Ltd.’s ISS Governance QualityScore as of N/A is N/A."


the key here is to recognize that the attrs are on the same level as :p so there should be no :p in the selector.

{:tag :p,
  :attrs {:class "Mt(15px) Lh(1.6)", :data-reactid "156"},
  :content
  ("Almaden Minerals Ltd., an exploration stage company, engages in the acquisition, exploration, and development of mineral properties in Canada and Mexico. The company primarily explores for gold, silver, and copper deposits. It holds a 100% interest in its principal property, the Ixtaca (Tuligtic) project that covers an area of approximately 7,200 hectares located in Puebla State, Mexico. The company was incorporated in 1980 and is headquartered in Vancouver, Canada.")}

(html/text (first (html/select t [(html/attr-contains :data-reactid "156")])))
"Almaden Minerals Ltd., an exploration stage company, engages in the acquisition, exploration, and development of mineral properties in Canada and Mexico. The company primarily explores for gold, silver, and copper deposits. It holds a 100% interest in its principal property, the Ixtaca (Tuligtic) project that covers an area of approximately 7,200 hectares located in Puebla State, Mexico. The company was incorporated in 1980 and is headquartered in Vancouver, Canada."

(html/select t [(html/attr-contains :class "Mt(15px) Lh(1.6)")]) works too!

