(ns solaire.components.middleware
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; middleware
;; ===============================================================

(defn- substitute
  [component entry]
  (if (vector? entry)
    (replace {:component component} entry)
    entry))

(defn- as-middleware
  [entry]
  (if (vector? entry)
    #(apply (first entry) % (rest entry))
    entry))

(defn- compose
  [component entries]
  (apply comp (into []
                    (comp
                     (map #(substitute component %))
                     (map as-middleware))
                    entries)))

(defrecord Middleware [entries]
  c/Lifecycle
  (start [this] this)
  (stop [this] this)

  cprt/IMiddleware
  (wrapper [{:keys [entries] :as this}]
    (compose this entries)))

(defn make-middleware
  [{:keys [entries]}]
  (map->Middleware {:entries entries}))
