(ns solaire.components.sente
  (:require
   [com.stuartsierra.component :as c]
   [taoensso.sente :refer [make-channel-socket!]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; websocket client
;; ===============================================================

(defrecord WebsocketClient [uri option sente-map]
  c/Lifecycle
  (start [this]
    (if (some? sente-map)
      this
      (assoc this :sente-map (make-channel-socket! uri option))))
  (stop [this]
    (if (nil? sente-map)
      this
      (do (sente/chsk-disconnect! (:chsk sente-map))
          (a/close! (:recv-chan sente-map))
          (assoc this :sente-map nil))))

  cprt/ISource
  (source-chan [this]
    (:ch-recv sente-map)))

(defn make-websocket-client
  [{:keys [uri option]}]
  (map->WebsocketClient {:uri    uri
                         :option option}))
