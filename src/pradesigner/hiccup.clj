"
hiccup
"

(ns user
  (:require [hiccup.core :as hc]
            [clojure.java.io :as io]))


;; * herka Hiccup Lightning Tutorial
        (hc/htmml
 [:div
  [:button#counter-btn
   {:class "btn active"
    :style {:padding 5}}]])

(hc/html [:div>div>h1 [:span]])

