(ns solaire.components.datascript.monitor-test
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.datascript.monitor :as cdtsmnt]
   [solaire.test.mock :as tmck]
   #?@(:clj  [[clojure.test :refer [deftest testing is]]]
       :cljs [[cljs.test :refer [deftest testing is]]])))

(defn- datascript-monitor-system
  [m]
  (c/system-map
   :datascript
   (tmck/make-mock-datascript {})

   :datascript-monitor
   (c/using
    (cdtsmnt/make-datascript-monitor (:datascript-monitor m))
    [:datascript])))

(deftest datascript-monitor-component
  (testing "datascript monitor lifecycle"
    (let [started (-> {:datascript-monitor {:callback identity}}
                      (datascript-monitor-system)
                      (c/start))
          stopped (c/stop started)]
      (is (fn? (get-in started [:datascript-monitor :stopper])))
      (is (nil? (get-in stopped [:datascript-monitor :stopper]))))))
