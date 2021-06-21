(ns localkanban.app.core
  (:require [react :as react]
            [reagent.core :as r]
            [reagent.dom :as rdom]))

;;; State

(def initial-kanban-board {1 {:id 1
                              :title "Getting started"
                              :cards {1 {:id 1
                                         :text "This is a sample list to show you what the kanban board looks like with cards"}
                                      2 {:id 2
                                         :text "Create a new list using the \"Add list\" button in the top-right hand corner"}
                                      3 {:id 3
                                         :text "Delete this list by clicking on \"Getting started\" and using the \"Delete\" button"}}}})

(defonce kanban-board (r/atom initial-kanban-board))

(defonce kanban-lists-counter (r/atom (count initial-kanban-board)))

(defonce kanban-cards-counter (r/atom (count (get-in initial-kanban-board [1 :cards]))))

(def initial-view-state {:active-list-id nil
                         :active-card-id nil
                         :show-add-list-modal false
                         :show-edit-list-modal false
                         :show-add-card-modal false
                         :show-edit-card-modal false})

(defonce view-state (r/atom initial-view-state))

;;; Helpers

(defn active-list-id []
  (@view-state :active-list-id))

(defn active-card-id []
  (@view-state :active-card-id))

(defn show-add-list-modal []
  (@view-state :show-add-list-modal))

(defn show-edit-list-modal []
  (@view-state :show-edit-list-modal))

(defn show-add-card-modal []
  (@view-state :show-add-card-modal))

(defn show-edit-card-modal []
  (@view-state :show-edit-card-modal))

(defn toggle-view-state [key]
  (swap! view-state update key not))

(defn set-active-list [list-id]
  (swap! view-state assoc :active-list-id list-id))

(defn set-active-card [list-id card-id]
  (swap! view-state assoc :active-list-id list-id :active-card-id card-id))

(defn reset-active-ids []
  (swap! view-state assoc :active-list-id nil :active-card-id nil))

(defn toggle-add-list-modal []
  (toggle-view-state :show-add-list-modal))

(defn toggle-edit-list-modal [list-id]
  (if (show-edit-list-modal) (reset-active-ids) (set-active-list list-id))
  (toggle-view-state :show-edit-list-modal))

(defn toggle-add-card-modal [list-id]
  (if (show-add-card-modal) (reset-active-ids) (set-active-list list-id))
  (toggle-view-state :show-add-card-modal))

(defn toggle-edit-card-modal [list-id card-id]
  (if (show-edit-card-modal) (reset-active-ids) (set-active-card list-id card-id))
  (toggle-view-state :show-edit-card-modal))

(defn is-enter-key-event [e]
  (= (.-key e) "Enter"))

(defn is-escape-key-event [e]
  (or (= (.-key e) "Escape") (= (.-key e) "Esc")))

(defn autofocus-modal []
  (.focus (.querySelector js/document ".modal.is-active input, .modal.is-active textarea")))

(defn add-kanban-list [title]
  (let [list-id  (swap! kanban-lists-counter inc)
        new-list {:id list-id
                  :title title}]
    (swap! kanban-board assoc list-id new-list)))

(defn update-kanban-list [list-id title]
  (swap! kanban-board assoc-in [list-id :title] title))

(defn delete-kanban-list [list-id]
  (swap! kanban-board dissoc list-id))

(defn add-kanban-card [list-id text]
  (let [card-id  (swap! kanban-cards-counter inc)
        new-card {:id card-id
                  :text text}]
    (swap! kanban-board assoc-in [list-id :cards card-id] new-card)))

(defn update-kanban-card [list-id card-id text]
  (swap! kanban-board assoc-in [list-id :cards card-id :text] text))

(defn delete-kanban-card [list-id card-id]
  (swap! kanban-board update-in [list-id :cards] dissoc card-id))

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

(defn card-component [list-id kanban-card]
  (let [handle-card #(toggle-edit-card-modal list-id (kanban-card :id))]
    [:div.card {:on-click handle-card}
     [:div.card-content
      [:div.content (kanban-card :text)]]]))

(defn cards-component [kanban-list]
  [:div.cards
   (for [card (vals (kanban-list :cards))]
     ^{:key (card :id)} [card-component (kanban-list :id) card])])

(defn list-component [kanban-list]
  (let [handle-list-title #(toggle-edit-list-modal (kanban-list :id))
        handle-add-card #(toggle-add-card-modal (kanban-list :id))]
    [:div.list
     [:a.list-title {:on-click handle-list-title} (kanban-list :title)]
     [cards-component kanban-list]
     [:div.list-footer
      [:a {:on-click handle-add-card} "Add card"]]]))

(defn lists-component []
  [:div.wrapper
   [:div.columns.is-mobile.is-vcentered
    (for [kanban-list (vals @kanban-board)]
      ^{:key (kanban-list :id)} [:div.column [list-component kanban-list]])]])

(defn add-list-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (toggle-add-list-modal) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (add-kanban-list @value) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      (react/useEffect #(when (show-add-list-modal) (autofocus-modal)))
      [:div.modal {:class (when (show-add-list-modal) "is-active")}
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
  (let [value (r/atom "")
        reset-modal #(do (toggle-edit-list-modal (active-list-id)) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (update-kanban-list (active-list-id) @value) (reset-modal))
        handle-delete #(do (delete-kanban-list (active-list-id)) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      (react/useEffect #(when (show-edit-list-modal) (autofocus-modal)))
      [:div.modal {:class (when (show-edit-list-modal) "is-active")}
       [:div.modal-background {:on-click reset-modal}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Edit list"]
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
         [:button.button.is-primary {:on-click handle-save} "Save"]
         [:button.button.is-danger {:on-click handle-delete} "Delete"]]]])))

(defn add-card-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (toggle-add-card-modal (active-list-id)) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (add-kanban-card (active-list-id) @value) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      (react/useEffect #(when (show-add-card-modal) (autofocus-modal)))
      [:div.modal {:class (when (show-add-card-modal) "is-active")}
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
  (let [value (r/atom "")
        reset-modal #(do (toggle-edit-card-modal (active-list-id) (active-card-id)) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (update-kanban-card (active-list-id) (active-card-id) @value) (reset-modal))
        handle-delete #(do (delete-kanban-card (active-list-id) (active-card-id)) (reset-modal))
        handle-key-down #(cond
                           (is-enter-key-event %) (handle-save)
                           (is-escape-key-event %) (reset-modal))]
    (fn []
      [:div.modal {:class (when (show-edit-card-modal) "is-active")}
       [:div.modal-background {:on-click toggle-edit-card-modal}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Edit card"]
         [:button.delete {:on-click reset-modal
                          :aria-label "close"}]]
        [:section.modal-card-body
         [:p
          [:textarea.textarea {:value @value
                               :placeholder "Enter card description"
                               :on-change handle-change
                               :on-key-down handle-key-down}]]]
        [:footer.modal-card-foot
         [:button.button.is-primary {:on-click handle-save} "Save"]
         [:button.button.is-danger {:on-click handle-delete} "Delete"]]]])))

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
