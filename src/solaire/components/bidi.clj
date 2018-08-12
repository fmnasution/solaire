(ns solaire.components.bidi
  (:require
   [com.stuartsierra.component :as c]
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; ring router
;; ===============================================================

(defrecord RingRouter [routes middleware handler]
  c/Lifecycle
  (start [this]
    (if (some? handler)
      this
      (let [wrapper (if (nil? middleware)
                      identity
                      (cprt/wrapper middleware))]
        (assoc this :handler (wrapper (make-handler routes))))))
  (stop [this]
    (if (nil? handler)
      this
      (assoc this :routes nil :handler nil)))

  cprt/IRequestHandler
  (request-handler [this]
    (:handler this))

  b/RouteProvider
  (routes [this]
    (:routes this)))

(defn make-ring-router
  [option]
  (-> option
      (select-keys [:routes])
      (map->RingRouter)))
