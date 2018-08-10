(ns solaire.components.http-kit
  (:require
   [com.stuartsierra.component :as c]
   [org.httpkit.server :refer [run-server]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; web server
;; ===============================================================

(defrecord WebServer [config handler middleware server]
  c/Lifecycle
  (start [this]
    (if (some? server)
      this
      (let [wrapper (if (nil? middleware)
                      identity
                      (cprt/wrapper middleware))
            handler (cprt/request-handler handler)]
        (assoc this :server (run-server (wrapper handler)
                                        (cprt/fetch-config config))))))
  (stop [this]
    (if (nil? server)
      this
      (do (server :timeout 100)
          (assoc this :server nil)))))

(defn make-web-server
  []
  (c/using
   (map->WebServer {})
   [:config :handler]))
