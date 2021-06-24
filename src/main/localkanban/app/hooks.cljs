(ns localkanban.app.hooks
  (:require [react :as react]))

(defn- autofocus-modal []
  (let [element (.querySelector js/document ".modal.is-active input, .modal.is-active textarea")
        value (.-value element)]
    (.focus element)
    ;; push cursor to end by reseting value
    (set! (.-value element) "")
    (set! (.-value element) value)))

(defn use-autofocus-modal [should-autofocus]
  (react/useEffect #(when should-autofocus (autofocus-modal)) (clj->js [should-autofocus])))
