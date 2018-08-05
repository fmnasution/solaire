(ns solaire.components.protocols)

(defprotocol IDatomStore
  (transact
    [this tx-data tx-meta]
    [thix tx-data]))

#?(:clj (defprotocol IDatomic
          (tx-report-queue [this])))

(defprotocol IDatascript
  (tx-listener [this callback]))

(defprotocol IConfig
  (fetch-config [this]))

#?(:clj (defprotocol IRequestHandler
          (request-handler [this])))

(defprotocol IMiddleware
  (wrapper [this]))

#?(:clj (defprotocol IWebsocketAdapter
          (server-adapter [this])))

(defprotocol ISource
  (source-chan [this]))

(defprotocol ISink
  (sink-chan [this]))
