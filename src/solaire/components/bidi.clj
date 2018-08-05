(ns solaire.components.bidi
  (:require
   [com.stuartsierra.component :as c]
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; ring router
;; ===============================================================

(defn- create-handler
  [component routes resources]
  (let [apply-closure (fn [closure]
                        (fn [handler]
                          (handler closure)))]
    (make-handler routes (comp (apply-closure component) resources))))

(defrecord RingRouter [routes resources middleware handler]
  c/Lifecycle
  (start [{:keys [routes resources handler middleware] :as this}]
    (if (some? handler)
      this
      (let [wrapper (if (nil? middleware)
                      identity
                      (cprt/wrapper middleware))
            handler (create-handler this routes resources)]
        (assoc this :handler (wrapper handler)))))
  (stop [{:keys [handler] :as this}]
    (if (nil? handler)
      this
      (assoc this :handler nil)))

  cprt/IRequestHandler
  (request-handler [this]
    (:handler this))

  b/RouteProvider
  (routes [this]
    (:routes this)))

(defn make-ring-router
  [{:keys [routes resources]}]
  (map->RingRouter {:routes    routes
                    :resources resources}))
