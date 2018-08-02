(ns solaire.components.datomic
  (:require
   [com.stuartsierra.component :as c]
   [datomic.api :as dtm]))

;; ===============================================================
;; datomic
;; ===============================================================

(defrecord Datomic [config conn])
