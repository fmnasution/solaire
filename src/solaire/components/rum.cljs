(ns solaire.components.rum
  (:require
   [goog.dom :as gdom]
   [com.stuartsierra.component :as c]
   [rum.core :as r]))

;; ===============================================================
;; html element
;; ===============================================================

(defrecord HtmlElement [id constructor node]
  c/Lifecycle
  (start [this]
    (if (some? node)
      this
      (let [node (gdom/getRequiredElement id)]
        (r/mount (constructor this) node)
        (assoc this :node node))))
  (stop [this]
    (if (nil? node)
      this
      (do (r/unmount node)
          (assoc this :node nil)))))

(defn make-html-element
  [option]
  (-> option
      (select-keys [:id :constructor])
      (map->HtmlElement)))
