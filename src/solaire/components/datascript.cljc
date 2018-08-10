(ns solaire.components.datascript
  (:require
   [com.stuartsierra.component :as c]
   [datascript.core :as dts]
   [datascript-schema.core :as dtssch]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datascript
;; ===============================================================

(defrecord Datascript [schema conn]
  c/Lifecycle
  (start [this]
    (if (some? conn)
      this
      (let [schema (if (map? schema) schema {})
            conn   (dts/create-conn schema)]
        (assoc this :conn conn))))
  (stop [this]
    (if (nil? conn)
      this
      (assoc this :conn nil)))

  cprt/IDatascript
  (tx-listener [{:keys [conn]} callback]
    (dtssch/listen-on-schema-change! conn callback)
    #(dtssch/unlisten-schema-change! conn))

  cprt/IDatomStore
  (transact [{:keys [conn]} tx-data tx-meta]
    (dts/transact conn tx-data tx-meta))
  (transact [{:keys [conn]} tx-data]
    (dts/transact conn tx-data)))

(defn make-datascript
  [{:keys [schema]}]
  (map->Datascript {:schema schema}))
