(ns solaire.components.datomic-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.datomic :as cdtm]))

(defn- datomic-system
  [m]
  (c/system-map
   :datomic
   (cdtm/make-datomic (:datomic m))))

(deftest datomic-component
  (testing "datomic lifecycle"
    (let [started (-> {:datomic {:config {:uri "datomic:mem://solaire-tests"}}}
                      (datomic-system)
                      (c/start))
          stopped (c/stop started)]
      (is (instance? datomic.Connection (get-in started [:datomic :conn])))
      (is (nil? (get-in stopped [:datomic :conn]))))))
