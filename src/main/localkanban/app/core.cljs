(ns localkanban.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

;;; State

(def initial-cards {1 {:id 1 :text "This is an example of a list to show you what the app looks like with data"}
                    2 {:id 2 :text "Create your own list using the \"Add another list\" button in the navbar"}
                    3 {:id 3 :text "Delete this list by clicking on the list title and choosing the \"Delete list\" option"}})

(defonce cards (r/atom initial-cards))

(defonce cards-counter (r/atom 1))

(defn add-card [text]
  (let [id (swap! cards-counter inc)
        new-card {:id id :text text}]
    (swap! cards assoc id new-card)))

(def initial-view-state {:show-list-modal false :show-card-modal false})

(defonce view-state (r/atom initial-view-state))

(defn toggle-show-list-modal [] (swap! view-state update :show-list-modal not))

(defn toggle-show-card-modal [] (swap! view-state update :show-card-modal not))

;;; Views

(defn navbar-component []
  [:nav.navbar {:role "navigation" :aria-label "navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "/"} [:span "Localkanban"]]]
   [:div.navbar-end
    [:div.navbar-item
     [:div.buttons
      [:a.button.is-primary "Add another list"]]]]])

(defn card-component [card]
  [:div.card
   [:div.card-content
    [:div.content (card :text)]]])

(defn cards-component []
  [:div.cards
   (for [card (vals @cards)]
     ^{:key (card :id)} [card-component card])])

(defn list-component []
  [:div.list
   [:a.list-title "Getting started"]
   [cards-component]
   [:div.list-footer
    [:a "Add card"]]])

(defn lists-component []
  [:div.wrapper
   [:div.columns.is-mobile.is-vcentered
    [:div.column [list-component]]]])

(defn list-modal-component []
  [:div.modal {:class (if (@view-state :show-list-modal) "is-active" "")}
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
  [:div.modal {:class (if (@view-state :show-card-modal) "is-active" "")}
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
