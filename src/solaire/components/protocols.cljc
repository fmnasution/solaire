(ns solaire.components.protocols)

#?(:clj (defprotocol IDatomic
          (tx-report-queue [this])))

(defprotocol IConfig
  (fetch-config [this]))

#?(:clj (defprotocol IRequestHandler
          (request-handler [this])))

(defprotocol IMiddleware
  (wrapper [this]))

#?(:clj (defprotocol IWebsocketAdapter
          (server-adapter [this])))
