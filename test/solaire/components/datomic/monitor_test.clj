(ns solaire.components.datomic.monitor-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.datomic.monitor :as cdtmmnt]
   [solaire.test.mock :as tmck]))

(defn- datomic-monitor-system
  [m]
  (c/system-map
   :datomic
   (tmck/make-mock-datomic)

   :datomic-monitor
   (c/using
    (cdtmmnt/make-datomic-monitor (:datomic-monitor m))
    [:datomic])))

(deftest datomic-monitor-component
  (testing "datomic monitor lifecycle"
    (let [started (-> {:datomic-monitor {:callback identity}}
                      (datomic-monitor-system)
                      (c/start))
          stopped (c/stop started)]
      (is (instance? clojure.lang.Atom
                     (get-in started [:datomic-monitor :active?_])))
      (is (nil? (get-in stopped [:datomic-monitor :active?_]))))))
