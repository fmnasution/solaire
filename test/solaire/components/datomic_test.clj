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

(defn- temp-datomic-system
  [m]
  (c/system-map
   :datomic
   (cdtm/make-temp-datomic (:datomic m))))

(defn- datomic-uri
  []
  (str "datomic:mem://solaire-" (gensym)))

(deftest datomic-component
  (testing "datomic lifecycle"
    (testing "permanent datomic"
      (let [started (-> {:datomic {:config {:uri (datomic-uri)}}}
                        (datomic-system)
                        (c/start))
            stopped (c/stop started)]
        (is (instance? datomic.Connection (get-in started [:datomic :conn])))
        (is (nil? (get-in stopped [:datomic :conn])))))
    (testing "temporary datomic"
      (let [started (-> {:datomic {:config {:uri (datomic-uri)}}}
                        (temp-datomic-system)
                        (c/start))
            stopped (c/stop started)]
        (is (instance? datomic.Connection (get-in started [:datomic :conn])))
        (is (nil? (get-in stopped [:datomic :conn])))))))
