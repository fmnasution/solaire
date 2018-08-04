(ns solaire.components.bidi
  (:require
   [com.stuartsierra.component :as c]
   [bidi.bidi :as b]
   [bidi.router :refer [start-router!]]))

;; ===============================================================
;; html router
;; ===============================================================

(defrecord HtmlRouter [routes callback default-location router]
  c/Lifecycle
  (start [{:keys [routes callback default-location router] :as this}]
    (if (some? router)
      this
      (let [option {:on-navigate      callback
                    :default-location default-location}]
        (assoc this :router (start-router! routes option)))))
  (stop [{:keys [router] :as this}]
    (if (nil? router)
      this
      (assoc this :router nil)))

  b/RouteProvider
  (routes [this]
    (:routes this)))

(defn make-html-router
  [{:keys [routes callback default-location]}]
  (map->HtmlRouter {:routes           routes
                    :callback         callback
                    :default-location default-location}))
