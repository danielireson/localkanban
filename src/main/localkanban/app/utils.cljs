(ns localkanban.app.utils)

(defn get-storage-item [key]
  (.getItem js/localStorage (str "localkanban:" key)))

(defn set-storage-item [key value]
  (.setItem js/localStorage (str "localkanban:" key) value))

(defn stringify-json [x]
  (.stringify js/JSON (clj->js x)))

(defn parse-json [x]
  (js->clj (.parse js/JSON x)))

(defn enter-key-event? [event]
  (= (.-key event) "Enter"))

(defn escape-key-event? [event]
  (or (= (.-key event) "Escape") (= (.-key event) "Esc")))
