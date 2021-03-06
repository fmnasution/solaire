(ns solaire.components.datomic.monitor
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datomic monitor
;; ===============================================================

(defrecord DatomicMonitor [datomic callback active?_]
  c/Lifecycle
  (start [this]
    (if (some? active?_)
      this
      (let [active?_ (atom true)
            queue    (cprt/tx-report-queue datomic)]
        (future
          (while @active?_
            (callback (.take queue))))
        (assoc this :active?_ active?_))))
  (stop [this]
    (if (nil? active?_)
      this
      (do (reset! active?_ false)
          (assoc this :active?_ nil)))))

(defn make-datomic-monitor
  [option]
  (-> option
      (select-keys [:callback])
      (map->DatomicMonitor)))
