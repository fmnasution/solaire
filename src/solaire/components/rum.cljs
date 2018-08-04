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
  (start [{:keys [id constructor node] :as this}]
    (if (some? node)
      this
      (let [node (gdom/getRequiredElement id)]
        (rum/mount (constructor this) node)
        (assoc this :node node))))
  (stop [{:keys [node] :as this}]
    (if (nil? node)
      this
      (do (r/unmount node)
          (assoc this :node nil)))))

(defn make-html-element
  [{:keys [id constructor]}]
  (map->HtmlElement {:id          id
                     :constructor constructor}))
