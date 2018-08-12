(ns solaire.components.http-kit-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.http-kit :as chkit]
   [solaire.test.mock :as tmck]))

(defn- web-server-system
  [m]
  (c/system-map
   :handler
   (tmck/make-mock-handler (:handler m))

   :web-server
   (c/using
    (chkit/make-web-server (:web-server m))
    [:handler])))

(deftest web-server-component
  (testing "web server lifecycle"
    (let [started (-> {:web-server {:config {:port 8080}}
                       :handler    identity}
                      (web-server-system)
                      (c/start))
          stopped (c/stop started)]
      (is (some? (get-in started [:web-server :server])))
      (is (nil? (get-in stopped [:web-server :server]))))))
