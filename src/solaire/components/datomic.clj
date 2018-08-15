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
  [{:keys [uri]}]
  (dtm/create-database uri)
  (dtm/connect uri))

(defn- ensure-conforms!
  [conn {:keys [norm-path]}]
  (when-let [norm-map (some-> norm-path (jio/resource) (slurp) (read-string))]
    (dtmcnf/ensure-conforms conn norm-map)))

(defrecord Datomic [config temporary? conn]
  c/Lifecycle
  (start [this]
    (if (some? conn)
      this
      (let [conn (create-conn! config)]
        (ensure-conforms! conn config)
        (assoc this :conn conn))))
  (stop [this]
    (if (nil? conn)
      this
      (do (dtm/release conn)
          (when temporary?
            (dtm/delete-database (:uri config)))
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
  [option]
  (-> option
      (select-keys [:config])
      (assoc :temporary? false)
      (map->Datomic)))

(defn make-temp-datomic
  [option]
  (-> option
      (select-keys [:config])
      (assoc :temporary? true)
      (map->Datomic)))
