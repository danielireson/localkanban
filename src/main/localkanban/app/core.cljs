(ns localkanban.app.core
  (:require [reagent.dom :as rdom]))

;;; Views

(defn app []
  [:h1 "Create Reagent App"])

;;; Render

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
