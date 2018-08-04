(ns solaire.components.sente
  (:require
   [clojure.core.async :as a]
   [com.stuartsierra.component :as c]
   [taoensso.sente :refer [make-channel-socket!]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; websocket server
;; ===============================================================

(defrecord WebsocketServer [web-server option sente-map]
  c/Lifecycle
  (start [{:keys [web-server option sente-map] :as this}]
    (if (some? sente-map)
      this
      (let [server-adapter (cprt/server-adapter web-server)]
        (assoc this :sente-map (make-channel-socket! server-adapter option)))))
  (stop [{:keys [option sente-map] :as this}]
    (if (nil? sente-map)
      this
      (do (a/close! (:ch-recv sente-map))
          (assoc this :sente-map nil))))

  cprt/ISource
  (source-chan [this]
    (get-in this [:sente-map :ch-recv])))

(defn make-websocket-server
  [{:keys [option]}]
  (c/using
   (map->WebsocketServer {:option option})
   [:web-server]))
