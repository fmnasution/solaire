(ns solaire.components.bidi-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.bidi :as cb]))

(defn- html-router-system
  [m]
  (c/system-map
   :html-router
   (cb/make-html-router (:html-router m))))

(deftest html-router-component
  (testing "html router lifecycle"
    (let [started (-> {:html-router {:routes ["" {"/" ::index}]
                                     :callback identity
                                     :default-location {:handler ::index}}}
                      (html-router-system)
                      (c/start))
          stopped (c/stop started)]
      (is (some? (get-in started [:html-router :router])))
      (is (nil? (get-in stopped [:html-router :router]))))))
