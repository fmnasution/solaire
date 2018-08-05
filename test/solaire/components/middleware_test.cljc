(ns solaire.components.middleware-test
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.middleware :as cmdw]
   #?(:clj [clojure.test :refer [deftest testing is]]
      :cljs [cljs.test :refer [deftest testing is]])))

(defn- middleware-system
  [m]
  (c/system-map
   :middleware
   (cmdw/make-middleware (:middleware m))))

(deftest middleware-component
  (testing "middleware lifecycle"
    (let [started (-> {:middleware {:entries [identity identity]}}
                      (middleware-system)
                      (c/start))
          stopped (c/stop started)]
      (is (= (:middleware started) (:middleware stopped))))))
