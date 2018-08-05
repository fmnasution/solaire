(ns solaire.components.aero
  (:require
   [clojure.java.io :as io]
   [com.stuartsierra.component :as c]
   [aero.core :refer [read-config]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; config
;; ===============================================================

(defrecord Config [source option value]
  c/Lifecycle
  (start [{:keys [source option value] :as this}]
    (if (some? value)
      this
      (assoc this :value (read-config (io/resource source) option))))
  (stop [{:keys [value] :as this}]
    (if (nil? value)
      this
      (assoc this :value nil)))

  cprt/IConfig
  (fetch-config [this]
    (:value this)))

(defn make-config
  [{:keys [source option]}]
  (map->Config {:source source
                :option option}))

;; ===============================================================
;; config cursor
;; ===============================================================

(defrecord ConfigCursor [config config-key]
  c/Lifecycle
  (start [this] this)
  (stop [this] this)

  cprt/IConfig
  (fetch-config [{:keys [config config-key]}]
    (get (cprt/fetch-config config) config-key )))

(defn make-config-cursor
  [{:keys [config-key]}]
  (c/using
   (map->ConfigCursor {:config-key config-key})
   [:config]))
