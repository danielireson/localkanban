(ns localkanban.app.views
  (:require [reagent.core :as r]
            [localkanban.app.state :as state]
            [localkanban.app.hooks :as hooks]
            [localkanban.app.utils :as utils]))

(defn- navbar-component []
  [:nav.navbar {:role "navigation"
                :aria-label "navigation"}
   [:div.navbar-brand
    [:span.navbar-item "Localkanban"]]
   [:div.navbar-end
    [:div.navbar-item
     [:div.buttons
      [:button.button.is-primary {:on-click state/toggle-add-list-modal!} "Add list"]]]]])

(defn- card-component [list-id kanban-card]
  (let [handle-card #(state/toggle-edit-card-modal! list-id (kanban-card "id"))]
    [:div.card {:on-click handle-card}
     [:div.card-content
      [:div.content (kanban-card "description")]]]))

(defn- cards-component [kanban-list]
  [:div.cards
   (for [card (vals (kanban-list "cards"))]
     ^{:key (card "id")} [card-component (kanban-list "id") card])])

(defn- list-component [kanban-list]
  (let [handle-list-title #(state/toggle-edit-list-modal! (kanban-list "id"))
        handle-add-card #(state/toggle-add-card-modal! (kanban-list "id"))]
    [:div.list
     [:a.list-title {:on-click handle-list-title} (kanban-list "title")]
     [cards-component kanban-list]
     [:div.list-footer
      [:a {:on-click handle-add-card} "Add card"]]]))

(defn- lists-component []
  [:div.wrapper
   [:div.columns.is-mobile.is-vcentered
    (for [kanban-list (state/kanban-lists)]
      ^{:key (kanban-list "id")} [:div.column [list-component kanban-list]])]])

(defn- add-list-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (state/toggle-add-list-modal!) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (state/add-kanban-list @value) (reset-modal))
        handle-key-down #(cond
                           (utils/enter-key-event? %) (handle-save)
                           (utils/escape-key-event? %) (reset-modal))]
    (fn []
      (hooks/use-autofocus-modal (state/show-add-list-modal))
      [:div.modal {:class (when (state/show-add-list-modal) "is-active")}
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

(defn- edit-list-modal-component []
  (let [value (r/atom nil)
        default-value #(when (some? %) (% "title"))
        reset-modal #(do (state/toggle-edit-list-modal! (state/active-list-id)) (reset! value nil))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (when (string? @value) (state/update-kanban-list! (state/active-list-id) @value)) (reset-modal))
        handle-delete #(do (state/delete-kanban-list! (state/active-list-id)) (reset-modal))
        handle-key-down #(cond
                           (utils/enter-key-event? %) (handle-save)
                           (utils/escape-key-event? %) (reset-modal))]
    (fn [active-list]
      (hooks/use-autofocus-modal (state/show-edit-list-modal))
      [:div.modal {:class (when (state/show-edit-list-modal) "is-active")}
       [:div.modal-background {:on-click reset-modal}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Edit list"]
         [:button.delete {:on-click reset-modal
                          :aria-label "close"}]]
        [:section.modal-card-body
         [:p
          [:input.input {:type "text"
                         :value (if (string? @value) @value (default-value active-list))
                         :placeholder "Enter list name"
                         :on-change handle-change
                         :on-key-down handle-key-down}]]]
        [:footer.modal-card-foot
         [:button.button.is-primary {:on-click handle-save} "Save"]
         [:button.button.is-danger {:on-click handle-delete} "Delete"]]]])))

(defn- add-card-modal-component []
  (let [value (r/atom "")
        reset-modal #(do (state/toggle-add-card-modal! (state/active-list-id)) (reset! value ""))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (state/add-kanban-card! (state/active-list-id) @value) (reset-modal))
        handle-key-down #(cond
                           (utils/enter-key-event? %) (handle-save)
                           (utils/escape-key-event? %) (reset-modal))]
    (fn []
      (hooks/use-autofocus-modal (state/show-add-card-modal))
      [:div.modal {:class (when (state/show-add-card-modal) "is-active")}
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

(defn- edit-card-modal-component []
  (let [value (r/atom nil)
        default-value #(when (some? %) (% "description"))
        reset-modal #(do (state/toggle-edit-card-modal! (state/active-list-id) (state/active-card-id)) (reset! value nil))
        handle-change #(reset! value (-> % .-target .-value))
        handle-save #(do (when (string? @value) (state/update-kanban-card! (state/active-list-id) (state/active-card-id) @value)) (reset-modal))
        handle-delete #(do (state/delete-kanban-card! (state/active-list-id) (state/active-card-id)) (reset-modal))
        handle-key-down #(cond
                           (utils/enter-key-event? %) (handle-save)
                           (utils/escape-key-event? %) (reset-modal))]
    (fn [active-card]
      (hooks/use-autofocus-modal (state/show-edit-card-modal))
      [:div.modal {:class (when (state/show-edit-card-modal) "is-active")}
       [:div.modal-background {:on-click state/toggle-edit-card-modal!}]
       [:div.modal-card
        [:header.modal-card-head
         [:p.modal-card-title "Edit card"]
         [:button.delete {:on-click reset-modal
                          :aria-label "close"}]]
        [:section.modal-card-body
         [:p
          [:textarea.textarea {:value (if (string? @value) @value (default-value active-card))
                               :placeholder "Enter card description"
                               :on-change handle-change
                               :on-key-down handle-key-down}]]]
        [:footer.modal-card-foot
         [:button.button.is-primary {:on-click handle-save} "Save"]
         [:button.button.is-danger {:on-click handle-delete} "Delete"]]]])))

(defn app-component []
  [:div.application
   [navbar-component]
   [lists-component]
   [:f> add-list-modal-component]
   [:f> edit-list-modal-component (state/active-list)]
   [:f> add-card-modal-component]
   [:f> edit-card-modal-component (state/active-card)]])
