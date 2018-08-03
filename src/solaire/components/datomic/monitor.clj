(ns solaire.components.datomic.monitor
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datomic monitor
;; ===============================================================

(defrecord DatomicMonitor [datomic callback active?_]
  c/Lifecycle
  (start [{:keys [datomic callback active?_] :as this}]
    (if (some? active?_)
      this
      (let [active?_ (atom true)
            queue    (cprt/tx-report-queue datomic)]
        (future
          (while @active?_
            (callback (.take queue))))
        (assoc this :active?_ active?_))))
  (stop [{:keys [active?_] :as this}]
    (if (nil? active?_)
      this
      (do (reset! active?_ false)
          (assoc this :active?_ nil)))))

(defn make-datomic-monitor
  [{:keys [callback]}]
  (c/using
   (map->DatomicMonitor {:callback callback})
   [:datomic]))
