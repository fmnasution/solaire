(ns solaire.components.http-client
  (:require
   [com.stuartsierra.component :as c]
   [bidi.bidi :as b]
   [ajax.core :as jx]))

;; ===============================================================
;; http client
;; ===============================================================

(defn- request!
  [routes handler route-params request-method option]
  (let [uri        (apply b/path-for
                          routes
                          handler
                          (flatten (seq route-params)))
        request-fn (case request-method
                     :get    jx/GET
                     :post   jx/POST
                     :put    jx/PUT
                     :delete jx/DELETE)]
    (request-fn uri option)))

(defn- make-caller
  [{:keys [routes option-fn]
    :or   {option-fn (fn [_ option] option)}
    :as   component}]
  (fn [{:keys [handler route-params request-method option]
       :or   {route-params   {}
              request-method :get}}]
    (request! routes
              handler
              route-params
              request-method
              (option-fn component option))))

(defrecord HttpClient [routes option-fn caller]
  c/Lifecycle
  (start [{:keys [routes option-fn caller] :as this}]
    (if (some? caller)
      this
      (assoc this :caller (make-caller this))))
  (stop [{:keys [caller] :as this}]
    (if (nil? caller)
      this
      (assoc this :caller nil)))

  b/RouteProvider
  (routes [this]
    (:routes this)))

(defn make-http-client
  [{:keys [routes option-fn]}]
  (map->HttpClient {:routes    routes
                    :option-fn option-fn}))
