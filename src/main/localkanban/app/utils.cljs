(ns localkanban.app.utils)

(defn get-storage-item [key]
  (try (.getItem js/localStorage (str "localkanban:" key)) (catch js/Error _)))

(defn set-storage-item [key value]
  (try (.setItem js/localStorage (str "localkanban:" key) value) (catch js/Error _)))

(defn stringify-json [x]
  (try (.stringify js/JSON (clj->js x)) (catch js/Error _)))

(defn parse-json [x]
  (try (js->clj (.parse js/JSON x)) (catch js/Error _)))

(defn enter-key-event? [event]
  (= (.-key event) "Enter"))

(defn escape-key-event? [event]
  (or (= (.-key event) "Escape") (= (.-key event) "Esc")))
