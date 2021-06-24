(ns localkanban.app.core
  (:require [reagent.dom :as rdom]
            [localkanban.app.views :as views]))

(defn render []
  (rdom/render [views/app-component] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
