(ns solaire.components.datascript-test
  (:require
   [com.stuartsierra.component :as c]
   [datascript.core :as dts]
   [solaire.components.datascript :as cdts]
   #?@(:clj  [[clojure.test :refer [deftest testing is]]]
       :cljs [[cljs.test :refer [deftest testing is]]])))

(defn- datascript-system
  [m]
  (c/system-map
   :datascript
   (cdts/make-datascript (:datascript m))))

(deftest datascript-component
  (testing "datascript lifecycle"
    (let [started (-> {}
                      (datascript-system)
                      (c/start))
          stopped (c/stop started)]
      (is (dts/conn? (get-in started [:datascript :conn])))
      (is (nil? (get-in stopped [:datascript :conn]))))))
