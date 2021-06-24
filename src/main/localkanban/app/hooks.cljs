(ns localkanban.app.hooks
  (:require [react :as react]))

(defn autofocus-modal []
  (.focus (.querySelector js/document ".modal.is-active input, .modal.is-active textarea")))

(defn use-autofocus-modal [should-autofocus]
  (react/useEffect #(when should-autofocus (autofocus-modal)) (clj->js [should-autofocus])))
