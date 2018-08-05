(ns solaire.components.http-client-test
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.http-client :as chcl]
   #?@(:clj  [[clojure.test :refer [deftest testing is]]]
       :cljs [[cljs.test :refer [deftest testing is]]])))

(defn- http-client-system
  [m]
  (c/system-map
   :http-client
   (chcl/make-http-client (:http-client m))))

(deftest http-client-component
  (testing "http client lifecycle"
    (let [started (-> {:http-client {:routes ["" {"/" ::index}]}}
                      (http-client-system)
                      (c/start))
          stopped (c/stop started)]
      (is (fn? (get-in started [:http-client :caller])))
      (is (nil? (get-in stopped [:http-client :caller]))))))
