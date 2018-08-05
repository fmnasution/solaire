(ns solaire.components.aero-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.aero :as car]
   [solaire.test.mock :as tmck]))

(defn- config-system
  [m]
  (c/system-map
   :config
   (car/make-config (:config m))))

(deftest config-component
  (testing "config lifecycle"
    (let [started (-> {:config {:source "private/solaire/config.edn"
                                :option {}}}
                      (config-system)
                      (c/start))
          stopped (c/stop started)]
      (is (map? (get-in started [:config :value])))
      (is (nil? (get-in stopped [:config :value]))))))

(defn- config-cursor-system
  [m]
  (c/system-map
   :config
   (tmck/make-mock-config (:config m))

   :config-cursor
   (car/make-config-cursor (:config-cursor m))))

(deftest config-cursor-component
  (testing "config cursor lifecycle"
    (let [started (-> {:config        {:foo :bar}
                       :config-cursor {:config-key :foo}}
                      (config-cursor-system)
                      (c/start))
          stopped (c/stop started)]
      (is (= (:config-cursor started) (:config-cursor stopped))))))
