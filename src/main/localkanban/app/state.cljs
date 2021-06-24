(ns localkanban.app.state
  (:require [reagent.core :as r]
            [localkanban.app.utils :as utils]))

(def default-kanban-board {"1" {"id" "1"
                                "title" "Getting started"
                                "cards" {"1" {"id" "1"
                                              "description" "Create a new list using the \"Add list\" button in the top-right hand corner"}
                                         "2" {"id" "2"
                                              "description" "Delete this list by clicking on \"Getting started\" and choosing \"Delete\""}
                                         "3" {"id" "3"
                                              "description" "Any changes you make to the kanban board are saved to local storage"}}}})

(defonce kanban-board
  (let [saved-json (utils/get-storage-item "state")
        parsed-board (if (some? saved-json) (utils/parse-json saved-json) default-kanban-board)]
    (r/atom (if (some? parsed-board) parsed-board {}))))

(defn kanban-lists []
  (vals @kanban-board))

(defonce kanban-lists-counter
  (r/atom (count @kanban-board)))

(defn next-kanban-list-id! []
  (str (swap! kanban-lists-counter inc)))

(defonce kanban-cards-counter
  (r/atom (reduce
           (fn [counter kanban-list] (+ counter (count (kanban-list "cards"))))
           0
           (vals @kanban-board))))

(defn next-kanban-card-id! []
  (str (swap! kanban-cards-counter inc)))

(defn save-kanban-board []
  (utils/set-storage-item "state" (utils/stringify-json @kanban-board)))

(defn add-kanban-list [title]
  (let [list-id (next-kanban-list-id!)
        new-list {"id" list-id
                  "title" title
                  "cards" {}}]
    (swap! kanban-board assoc list-id new-list)
    (save-kanban-board)))

(defn update-kanban-list! [list-id title]
  (swap! kanban-board assoc-in [list-id "title"] title)
  (save-kanban-board))

(defn delete-kanban-list! [list-id]
  (swap! kanban-board dissoc list-id)
  (save-kanban-board))

(defn add-kanban-card! [list-id description]
  (let [card-id  (next-kanban-card-id!)
        new-card {"id" card-id
                  "description" description}]
    (swap! kanban-board assoc-in [list-id "cards" card-id] new-card)
    (save-kanban-board)))

(defn update-kanban-card! [list-id card-id description]
  (swap! kanban-board assoc-in [list-id "cards" card-id "description"] description)
  (save-kanban-board))

(defn delete-kanban-card! [list-id card-id]
  (swap! kanban-board update-in [list-id "cards"] dissoc card-id)
  (save-kanban-board))

(def initial-view-state {:active-list-id nil
                         :active-card-id nil
                         :show-add-list-modal false
                         :show-edit-list-modal false
                         :show-add-card-modal false
                         :show-edit-card-modal false})

(defonce view-state (r/atom initial-view-state))

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

(defn set-active-list! [list-id]
  (swap! view-state assoc :active-list-id list-id))

(defn set-active-card! [list-id card-id]
  (swap! view-state assoc :active-list-id list-id :active-card-id card-id))

(defn reset-active-ids! []
  (swap! view-state assoc :active-list-id nil :active-card-id nil))

(defn- toggle-view-state! [key]
  (swap! view-state update key not))

(defn toggle-add-list-modal! []
  (toggle-view-state! :show-add-list-modal))

(defn toggle-edit-list-modal! [list-id]
  (if (show-edit-list-modal) (reset-active-ids!) (set-active-list! list-id))
  (toggle-view-state! :show-edit-list-modal))

(defn toggle-add-card-modal! [list-id]
  (if (show-add-card-modal) (reset-active-ids!) (set-active-list! list-id))
  (toggle-view-state! :show-add-card-modal))

(defn toggle-edit-card-modal! [list-id card-id]
  (if (show-edit-card-modal) (reset-active-ids!) (set-active-card! list-id card-id))
  (toggle-view-state! :show-edit-card-modal))
