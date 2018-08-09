(ns solaire.components.bidi-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.bidi :as cb]))

(defn- ring-endpoint-system
  [m]
  (c/system-map
   :ring-endpoint
   (cb/make-ring-endpoint (:endpoint m))))

(deftest ring-endpoint-component
  (testing "ring endpoint lifecycle"
    (let [started (-> {:endpoint {:routes {"/" ::index}}}
                      (ring-endpoint-system)
                      (c/start))
          stopped (c/stop started)]
      (is (= (:ring-endpoint started) (:ring-endpoint stopped))))))

(defn- throw-ring-router-system
  [m]
  (c/system-map
   :ring-router
   (cb/make-ring-router (:ring-router m))))

(defn- ring-router-system
  [m]
  (c/system-map
   :ring-endpoint
   (cb/make-ring-endpoint (:endpoint m))

   :ring-router
   (c/using
    (cb/make-ring-router (:ring-router m))
    [:ring-endpoint])))

(deftest ring-router-component
  (testing "ring router lifecycle"
    (testing "throw if no endpoint found"
      (is (thrown? Throwable (-> {:ring-router {:prefix ""}}
                                 (throw-ring-router-system)
                                 (c/start)))))
    (testing "has handler"
      (let [started (-> {:endpoint    {:routes {"/" ::index}}
                         :ring-router {:prefix ""}}
                        (ring-router-system)
                        (c/start))
            stopped (c/stop started)]
        (is (fn? (get-in started [:ring-router :handler])))
        (is (some? (get-in started [:ring-router :routes])))
        (is (nil? (get-in stopped [:ring-router :handler])))
        (is (nil? (get-in stopped [:ring-router :routes])))))))
