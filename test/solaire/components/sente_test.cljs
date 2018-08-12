(ns solaire.components.sente-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.sente :as csnt]))

;; TODO: fix this

;; (defn- websocket-client-system
;;   [m]
;;   (c/system-map
;;    :websocket-client
;;    (csnt/make-websocket-client (:websocket-client m))))

;; (deftest websocket-client-component
;;   (testing "websocket client lifecycle"
;;     (let [started (-> {:websocket-client {:uri "fooo"}}
;;                       (websocket-client-system)
;;                       (c/start))
;;           stopped (c/stop started)]
;;       (is (map? (get-in started [:websocket-client :sente-map])))
;;       (is (nil? (get-in stopped [:websocket-client :sente-map]))))))
