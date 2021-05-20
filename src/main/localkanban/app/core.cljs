(ns localkanban.app.core
  (:require [reagent.dom :as rdom]))

;;; Views

(defn navbar-component []
  [:nav.navbar {:role "navigation" :aria-label "navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "/"} [:span "LocalKanban"]]]
   [:div.navbar-end
    [:div.navbar-item
     [:div.buttons
      [:a.button.is-primary "Add another list"]]]]])

(defn list-modal-component []
  [:div.modal
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Add another list"]
     [:button.delete {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:input.input {:type "text" :placeholder "Enter list name"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Done"]]]])

(defn card-modal-component []
  [:div.modal
   [:div.modal-background]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Add card"]
     [:button.delete {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:textarea.textarea {:placeholder "Enter card description"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Done"]]]])

(defn card-component []
  [:div.card
   [:div.card-content
    [:div.content "Lorem ipsum leo risus, porta ac consectetur ac, vestibulum at eros. Donec id elit non mi porta gravida at eget metus."]]])

(defn cards-component []
  [:div.cards
   [card-component]
   [card-component]
   [card-component]
   [card-component]])

(defn list-component []
  [:div.list
   [:h1.list-title "List title"]
   [cards-component]
   [:div.list-footer
    [:a "Add card"]]])

(defn lists-component []
  [:div.columns.is-mobile.is-vcentered
   [:div.column [list-component]]])

(defn app []
  [:div.application
   [navbar-component]
   [lists-component]
   [list-modal-component]
   [card-modal-component]])

;;; Render

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
