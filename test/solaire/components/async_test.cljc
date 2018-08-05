(ns solaire.components.async-test
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.async :as casnc]
   [solaire.components.aero :as car]
   [solaire.test.mock :as tmck]
   #?@(:clj  [[clojure.test :refer [deftest testing is]]]
       :cljs [[cljs.test :refer [deftest testing is]]])))

(defn- item-dispatcher-system
  [m]
  (c/system-map
   :config
   (tmck/make-mock-config (:config m))

   :item-dispatcher-config
   (car/make-config-cursor (:item-dispatcher-config m))

   :item-dispatcher
   (c/using
    (casnc/make-item-dispatcher)
    {:config :item-dispatcher-config})))

(deftest item-dispatcher-component
  (testing "item dispatcher lifecycle"
    (let [started (-> {:config                 {:config {}}
                       :item-dispatcher-config {:config-key :item-dispatcher}}
                      (item-dispatcher-system)
                      (c/start))
          stopped (c/stop started)]
      (is true))))
