(ns solaire.components.datomic
  (:require
   [clojure.java.io :as jio]
   [com.stuartsierra.component :as c]
   [datomic.api :as dtm]
   [io.rkn.conformity :as dtmcnf]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datomic
;; ===============================================================

(defn- create-conn!
  [uri]
  (dtm/create-database uri)
  (dtm/connect uri))

(defn- ensure-conforms!
  [conn path]
  (when-let [norm-map (some-> path (jio/resource) (slurp) (read-string))]
    (dtmcnf/ensure-conforms conn norm-map)))

(defrecord Datomic [config conn]
  c/Lifecycle
  (start [this]
    (if (some? conn)
      this
      (let [{:keys [uri norm-path]} (cprt/fetch-config config)
            conn                    (create-conn! uri)]
        (ensure-conforms! conn norm-path)
        (assoc this :conn conn))))
  (stop [this]
    (if (nil? conn)
      this
      (do (dtm/release conn)
          (assoc this :conn nil))))

  cprt/IDatomic
  (tx-report-queue [this]
    (dtm/tx-report-queue (:conn this)))

  cprt/IDatomStore
  (transact [{:keys [conn]} tx-data tx-meta]
    (dtm/transact conn tx-data))
  (transact [{:keys [conn]} tx-data]
    (dtm/transact conn tx-data)))

(defn make-datomic
  []
  (c/using
   (map->Datomic {})
   [:config]))
