(ns solaire.components.async-test
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.async :as casnc]
   #?@(:clj  [[clojure.test :refer [deftest testing is]]]
       :cljs [[cljs.test :refer [deftest testing is]]])))

(defn- item-dispatcher-system
  [m]
  (c/system-map
   :item-dispatcher
   (casnc/make-item-dispatcher (:item-dispatcher m))))

(deftest item-dispatcher-component
  (testing "item dispatcher lifecycle"
    (let [started (-> {}
                      (item-dispatcher-system)
                      (c/start))
          stopped (c/stop started)]
      (is true))))
