(ns solaire.components.http-kit-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.http-kit :as chkit]
   [solaire.components.aero :as car]
   [solaire.test.mock :as tmck]))

(defn- web-server-system
  [m]
  (c/system-map
   :config
   (tmck/make-mock-config (:config m))

   :handler
   (tmck/make-mock-handler (:handler m))

   :web-server-config
   (car/make-config-cursor (:web-server-config m))

   :web-server
   (c/using
    (chkit/make-web-server)
    {:config :web-server-config})))

(deftest web-server-component
  (testing "web server lifecycle"
    (let [started (-> {:config            {:web-server {:port 8080}}
                       :handler           identity
                       :web-server-config {:config-key :web-server}}
                      (web-server-system)
                      (c/start))
          stopped (c/stop started)]
      (is (some? (get-in started [:web-server :server])))
      (is (nil? (get-in stopped [:web-server :server]))))))
