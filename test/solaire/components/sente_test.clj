(ns solaire.components.sente-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.sente :as csnt]
   [solaire.test.mock :as tmck]))

(defn- websocket-server-system
  [m]
  (c/system-map
   :web-server
   (tmck/make-mock-web-server (:web-server m))

   :websocket-server
   (c/using
    (csnt/make-websocket-server (:websocket-server m))
    [:web-server])))

(deftest websocket-server-component
  (testing "websocket server lifecycle"
    (let [started (-> {:web-server       {:server identity}
                       :websocket-server {:option {}}}
                      (websocket-server-system)
                      (c/start))
          stopped (c/stop started)]
      (is (map? (get-in started [:websocket-server :sente-map])))
      (is (nil? (get-in stopped [:websocket-server :sente-map]))))))
