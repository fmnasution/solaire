(ns solaire.components.bidi-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.bidi :as cb]))

(defn- ring-router-system
  [m]
  (c/system-map
   :ring-router
   (cb/make-ring-router (:ring-router m))))

(deftest ring-router-component
  (testing "ring router lifecycle"
    (testing "has handler"
      (let [started (-> {:ring-router {:routes {"/" ::index}}}
                        (ring-router-system)
                        (c/start))
            stopped (c/stop started)]
        (is (fn? (get-in started [:ring-router :handler])))
        (is (nil? (get-in stopped [:ring-router :handler])))))))
