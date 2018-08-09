(ns solaire.components.bidi
  (:require
   [com.stuartsierra.component :as c]
   [bidi.bidi :as b]
   [bidi.ring :refer [make-handler]]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; ring router
;; ===============================================================

(defn- compute-routes
  [{:keys [prefix] :as component}]
  (if-let [routes (not-empty (into {}
                                   (comp
                                    (map val)
                                    (filter #(satisfies? b/RouteProvider %))
                                    (map b/routes))
                                   component))]
    [prefix routes]
    (throw (ex-info "No endpoint found" {}))))

(defrecord RingRouter [prefix routes middleware handler]
  c/Lifecycle
  (start [{:keys [middleware handler] :as this}]
    (if (some? handler)
      this
      (let [wrapper (if (nil? middleware)
                      identity
                      (cprt/wrapper middleware))
            routes  (compute-routes this)]
        (assoc this
               :routes  routes
               :handler (wrapper (make-handler routes))))))
  (stop [{:keys [handler] :as this}]
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
  [{:keys [prefix]}]
  (map->RingRouter {:prefix prefix}))

;; ===============================================================
;; endpoint
;; ===============================================================

(defrecord RingEndpoint [routes]
  c/Lifecycle
  (start [this] this)
  (stop [this] this)

  b/RouteProvider
  (routes [this]
    (:routes this)))

(defn make-ring-endpoint
  [{:keys [routes]}]
  (map->RingEndpoint {:routes routes}))
