(ns localkanban.app.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

;;; State

(def initial-cards {1 {:id 1 :text "This is an example of a list to show you what the app looks like with data"}
                    2 {:id 2 :text "Create your own list using the \"Add list\" button in the navbar"}
                    3 {:id 3 :text "Delete this list by clicking on the list title and choosing the \"Delete list\" option"}})

(defonce cards (r/atom initial-cards))

(defonce cards-counter (r/atom 1))

(defn add-card [text]
  (let [id (swap! cards-counter inc)
        new-card {:id id :text text}]
    (swap! cards assoc id new-card)))

(def initial-view-state {:show-add-list-modal false
                         :show-edit-list-modal false
                         :show-add-card-modal false
                         :show-edit-card-modal false})

(defonce view-state (r/atom initial-view-state))

(defn toggle-add-list-modal [] (swap! view-state update :show-add-list-modal not))

(defn toggle-edit-list-modal [] (swap! view-state update :show-edit-list-modal not))

(defn toggle-add-card-modal [] (swap! view-state update :show-add-card-modal not))

(defn toggle-edit-card-modal [] (swap! view-state update :show-edit-card-modal not))

;;; Views

(defn navbar-component []
  [:nav.navbar {:role "navigation" :aria-label "navigation"}
   [:div.navbar-brand
    [:a.navbar-item {:href "/"} [:span "Localkanban"]]]
   [:div.navbar-end
    [:div.navbar-item
     [:div.buttons
      [:button.button.is-primary {:on-click toggle-add-list-modal} "Add list"]]]]])

(defn card-component [card]
  [:div.card {:on-click toggle-edit-card-modal}
   [:div.card-content
    [:div.content (card :text)]]])

(defn cards-component []
  [:div.cards
   (for [card (vals @cards)]
     ^{:key (card :id)} [card-component card])])

(defn list-component []
  [:div.list
   [:a.list-title {:on-click toggle-edit-list-modal} "Getting started"]
   [cards-component]
   [:div.list-footer
    [:a {:on-click toggle-add-card-modal} "Add card"]]])

(defn lists-component []
  [:div.wrapper
   [:div.columns.is-mobile.is-vcentered
    [:div.column [list-component]]]])

(defn add-list-modal-component []
  [:div.modal {:class (if (@view-state :show-add-list-modal) "is-active" "")}
   [:div.modal-background {:on-click toggle-add-list-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Add list"]
     [:button.delete {:on-click toggle-add-list-modal} {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:input.input {:type "text" :placeholder "Enter list name"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Save"]]]])

(defn edit-list-modal-component []
  [:div.modal {:class (if (@view-state :show-edit-list-modal) "is-active" "")}
   [:div.modal-background {:on-click toggle-edit-list-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Edit list"]
     [:button.delete {:on-click toggle-edit-list-modal} {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:input.input {:type "text" :placeholder "Enter list name"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Save"]
     [:button.button.is-danger "Delete"]]]])

(defn add-card-modal-component []
  [:div.modal {:class (if (@view-state :show-add-card-modal) "is-active" "")}
   [:div.modal-background {:on-click toggle-add-card-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Add card"]
     [:button.delete {:on-click toggle-add-card-modal} {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:textarea.textarea {:placeholder "Enter card description"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Save"]]]])

(defn edit-card-modal-component []
  [:div.modal {:class (if (@view-state :show-edit-card-modal) "is-active" "")}
   [:div.modal-background {:on-click toggle-edit-card-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Edit card"]
     [:button.delete {:on-click toggle-edit-card-modal} {:aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:textarea.textarea {:placeholder "Enter card description"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Save"]
     [:button.button.is-danger "Delete"]]]])

(defn app []
  [:div.application
   [navbar-component]
   [lists-component]
   [add-list-modal-component]
   [edit-list-modal-component]
   [add-card-modal-component]
   [edit-card-modal-component]])

;;; Render

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
