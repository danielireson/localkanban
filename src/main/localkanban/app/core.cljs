(ns localkanban.app.core
  (:require [react :as react]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;;; State

(def initial-lists {1 {:id 1
                       :title "Getting started"
                       :cards {1 {:id 1
                                  :text "This is a sample list to show you what the kanban board looks like with cards"}
                               2 {:id 2
                                  :text "Create your own list using the \"Add list\" button in the top-right hand corner"}
                               3 {:id 3
                                  :text "Delete this list by clicking on \"Getting started\" and choosing \"Delete\""}}}})

(defonce lists (r/atom initial-lists))

(defonce lists-counter (r/atom (count initial-lists)))

(defonce cards-counter (r/atom (count (get-in initial-lists [1 :cards]))))

(defn add-list [title]
  (let [list-id  (swap! lists-counter inc)
        new-list {:id list-id
                  :title title}]
    (swap! lists assoc list-id new-list)))

(defn add-card [text]
  (let [card-id  (swap! cards-counter inc)
        new-card {:id card-id
                  :text text}]
    (swap! lists assoc card-id new-card)))

(def initial-view-state {:show-add-list-modal false
                         :show-edit-list-modal false
                         :show-add-card-modal false
                         :show-edit-card-modal false})

(defonce view-state (r/atom initial-view-state))

(defn toggle-view-state [key]
  (swap! view-state update key not))




(defn toggle-add-list-modal []
  (toggle-view-state :show-add-list-modal))

(defn toggle-edit-list-modal [list-id]
  (toggle-view-state :show-edit-list-modal))

(defn toggle-add-card-modal []
  (toggle-view-state :show-add-card-modal))

(defn toggle-edit-card-modal [list-id card-id]
  (toggle-view-state :show-edit-card-modal))

;;; Helpers

(defn is-enter-key-event [e]
  (= (.-key e) "Enter"))

(defn is-escape-key-event [e]
  (or (= (.-key e) "Escape") (= (.-key e) "Esc")))

(defn autofocus-modal []
  (.focus (.querySelector js/document ".modal.is-active input, .modal.is-active textarea")))

;;; Views

(defn navbar-component []
  [:nav.navbar {:role "navigation"
                :aria-label "navigation"}
   [:div.navbar-brand
    [:span.navbar-item "Localkanban"]]
   [:div.navbar-end
    [:div.navbar-item
     [:div.buttons
      [:button.button.is-primary {:on-click toggle-add-list-modal} "Add list"]]]]])

(defn card-component [card]
  [:div.card {:on-click toggle-edit-card-modal}
   [:div.card-content
    [:div.content (card :text)]]])

(defn cards-component [cards]
  [:div.cards
   (for [card (vals cards)]
     ^{:key (card :id)} [card-component card])])

(defn list-component [list]
  [:div.list
   [:a.list-title {:on-click toggle-edit-list-modal} (list :title)]
   [cards-component (list :cards)]
   [:div.list-footer
    [:a {:on-click toggle-add-card-modal} "Add card"]]])

(defn lists-component []
  [:div.wrapper
   [:div.columns.is-mobile.is-vcentered
    (for [list (vals @lists)]
      ^{:key (list :id)} [:div.column [list-component list]])]])

(defn add-list-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (toggle-add-list-modal) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (add-list @value) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      (react/useEffect #(when (@view-state :show-add-list-modal) (autofocus-modal)))
      [:div.modal {:class (when (@view-state :show-add-list-modal) "is-active")}
       [:div.modal-background {:on-click reset-modal}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Add list"]
         [:button.delete {:on-click reset-modal
                          :aria-label "close"}]]
        [:section.modal-card-body
         [:p
          [:input.input {:type "text"
                         :value @value
                         :placeholder "Enter list name"
                         :on-change handle-change
                         :on-key-down handle-key-down}]]]
        [:footer.modal-card-foot
         [:button.button.is-primary {:on-click handle-save} "Save"]]]])))

(defn edit-list-modal-component []
  [:div.modal {:class (when (@view-state :show-edit-list-modal) "is-active")}
   [:div.modal-background {:on-click toggle-edit-list-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Edit list"]
     [:button.delete {:on-click toggle-edit-list-modal
                      :aria-label "close"}]]
    [:section.modal-card-body
     [:p
      [:input.input {:type "text"
                     :placeholder "Enter list name"}]]]
    [:footer.modal-card-foot
     [:button.button.is-primary "Save"]
     [:button.button.is-danger "Delete"]]]])

(defn add-card-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (toggle-add-card-modal) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (add-card @value) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      (react/useEffect #(when (@view-state :show-add-card-modal) (autofocus-modal)))
      [:div.modal {:class (when (@view-state :show-add-card-modal) "is-active")}
       [:div.modal-background {:on-click reset-modal}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Add card"]
         [:button.delete {:on-click reset-modal
                          :aria-label "close"}]]
        [:section.modal-card-body
         [:p
          [:textarea.textarea {:value @value
                               :placeholder "Enter card description"
                               :on-change handle-change
                               :on-key-down handle-key-down}]]]
        [:footer.modal-card-foot
         [:button.button.is-primary {:on-click handle-save} "Save"]]]])))

(defn edit-card-modal-component []
  [:div.modal {:class (when (@view-state :show-edit-card-modal) "is-active")}
   [:div.modal-background {:on-click toggle-edit-card-modal}]
   [:div.modal-card
    [:header.modal-card-head
     [:p.modal-card-title "Edit card"]
     [:button.delete {:on-click toggle-edit-card-modal
                      :aria-label "close"}]]
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
   [:f> add-list-modal-component]
   [:f> edit-list-modal-component]
   [:f> add-card-modal-component]
   [:f> edit-card-modal-component]])

;;; Render

(defn render []
  (rdom/render [app] (.getElementById js/document "root")))

(defn ^:export main []
  (render))

(defn ^:dev/after-load reload! []
  (render))
