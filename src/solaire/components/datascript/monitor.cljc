(ns solaire.components.datascript.monitor
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.protocols :as cprt]))

;; ===============================================================
;; datascript monitor
;; ===============================================================

(defrecord DatascriptMonitor [datascript callback stopper]
  c/Lifecycle
  (start [{:keys [datascript callback stopper] :as this}]
    (if (some? stopper)
      this
      (assoc this :stopper (cprt/tx-listener datascript callback))))
  (stop [{:keys [stopper] :as this}]
    (if (nil? stopper)
      this
      (do (stopper)
          (assoc this :stopper nil)))))

(defn make-datascript-monitor
  [{:keys [callback]}]
  (c/using
   (map->DatascriptMonitor {:callback callback})
   [:datascript]))
