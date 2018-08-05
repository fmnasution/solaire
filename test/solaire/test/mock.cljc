(ns solaire.test.mock
  (:require
   [com.stuartsierra.component :as c]
   [datascript.core :as dts]
   [taoensso.sente.server-adapters.http-kit :refer [get-sch-adapter]]
   [solaire.components.protocols :as cprt]
   #?@(:clj [[datomic.api :as dtm]]))
  #?(:clj
     (:import
      [java.util.concurrent LinkedBlockingQueue])))

;; ===============================================================
;; mock config
;; ===============================================================

(defrecord MockConfig [config]
  c/Lifecycle
  (start [this] this)
  (stop [this] this)

  cprt/IConfig
  (fetch-config [this]
    (:config this)))

(defn make-mock-config
  [{:keys [config]}]
  (map->MockConfig {:config config}))

;; ===============================================================
;; mock handler
;; ===============================================================

#?(:clj (defrecord MockHandler [handler]
          c/Lifecycle
          (start [this] this)
          (stop [this] this)

          cprt/IRequestHandler
          (request-handler [this]
            (:handler this))))

#?(:clj (defn make-mock-handler
          [{:keys [handler]}]
          (map->MockHandler {:handler handler})))

;; ===============================================================
;; mock web server
;; ===============================================================

#?(:clj (defrecord MockWebServer [server]
          c/Lifecycle
          (start [this] this)
          (stop [this] this)

          cprt/IWebsocketAdapter
          (server-adapter [this]
            (get-sch-adapter))))

#?(:clj (defn make-mock-web-server
          [{:keys [server]}]
          (map->MockWebServer {:server server})))

;; ===============================================================
;; mock datomic
;; ===============================================================

#?(:clj (defrecord MockDatomic [db_ queue_ uri]
          c/Lifecycle
          (start [{:keys [uri] :as this}]
            (if (some? uri)
              this
              (let [uri      (str "datomic:mem://solaire-" (gensym))
                    created? (dtm/create-database uri)
                    conn     (dtm/connect uri)]
                (assoc this
                       :db_    (atom (dtm/db conn))
                       :queue_ (LinkedBlockingQueue.)
                       :uri    uri))))
          (stop [{:keys [uri] :as this}]
            (if (nil? uri)
              this
              (do (dtm/delete-database uri)
                  (assoc this
                         :db_    nil
                         :queue_ nil
                         :uri    nil))))

          cprt/IDatomic
          (tx-report-queue [this]
            (:queue_ this))

          cprt/IDatomStore
          (transact [{:keys [db_ queue_]} tx-data tx-meta]
            (future
              (let [tx-report (loop []
                                (let [old-db    @db_
                                      tx-report (dtm/with old-db tx-data)
                                      new-db    (:db-after tx-report)]
                                  (if (compare-and-set! db_ old-db new-db)
                                    tx-report
                                    (recur))))]
                (.put queue_ tx-report)
                tx-report)))
          (transact [this tx-data]
            (cprt/transact this tx-data {}))))

#?(:clj (defn make-mock-datomic
          []
          (map->MockDatomic {})))

;; ===============================================================
;; mock datascript
;; ===============================================================

(defrecord MockDatascript [schema callback db_]
  c/Lifecycle
  (start [{:keys [schema conn] :as this}]
    (if (some? conn)
      this
      (let [schema (if (map? schema) schema {})
            conn   (dts/create-conn schema)]
        (assoc this :db_ (atom @conn)))))
  (stop [{:keys [db_] :as this}]
    (if (nil? db_)
      this
      (assoc this :db_ nil)))

  cprt/IDatascript
  (tx-listener [this callback]
    (assoc this :callback callback)
    #(dissoc this :callback))

  cprt/IDatomStore
  (transact [{:keys [callback db_]} tx-data tx-meta]
    (future
      (loop [tx-report (loop []
                         (let [old-db    @db_
                               tx-report (dts/with old-db tx-data tx-meta)
                               new-db    (:db-after tx-report)]
                           (if (compare-and-set! db_ old-db new-db)
                             tx-report
                             (recur))))]
        (callback tx-report)
        tx-report)))
  (transact [this tx-data]
    (cprt/transact this tx-data {})))

(defn make-mock-datascript
  [{:keys [schema]}]
  (map->MockDatascript {:schema schema}))
