(ns solaire.components.datomic
  (:require
   [com.stuartsierra.component :as c]
   [datomic.api :as dtm]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datomic
;; ===============================================================

(defn- create-conn!
  [uri]
  (dtm/create-database uri)
  (dtm/connect uri))

(defrecord Datomic [config conn]
  c/Lifecycle
  (start [{:keys [config conn] :as this}]
    (if (some? conn)
      this
      (let [uri  (:uri (cprt/fetch-config config))
            conn (create-conn! uri)]
        (assoc this :conn conn))))
  (stop [{:keys [conn] :as this}]
    (if (nil? conn)
      this
      (do (dtm/release conn)
          (assoc this :conn nil))))

  cprt/IDatomic
  (tx-report-queue [this]
    (dtm/tx-report-queue (:conn this))))

(defn make-datomic
  []
  (c/using
   (map->Datomic {})
   [:config]))
