(ns solaire.components.datomic-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.datomic :as cdtm]
   [solaire.components.aero :as car]
   [solaire.test.mock :as tmck]))

(defn- datomic-system
  [m]
  (c/system-map
   :config
   (tmck/make-mock-config (:config m))

   :datomic-config
   (c/using
    (car/make-config-cursor (:datomic-config m))
    [:config])

   :datomic
   (c/using
    (cdtm/make-datomic)
    {:config :datomic-config})))

(deftest datomic-component
  (testing "datomic lifecycle"
    (let [started (-> {:config
                       {:config {:datomic {:uri "datomic:mem://solaire-tests"}}}

                       :datomic-config
                       {:config-key :datomic}}
                      (datomic-system)
                      (c/start))
          stopped (c/stop started)]
      (is (instance? datomic.Connection (get-in started [:datomic :conn])))
      (is (nil? (get-in stopped [:datomic :conn]))))))
