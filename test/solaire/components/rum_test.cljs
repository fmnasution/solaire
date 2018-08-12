(ns solaire.components.rum-test
  (:require
   [cljs.test :refer [deftest testing is]]
   [com.stuartsierra.component :as c]
   [solaire.components.rum :as crm]))

;; TODO: fix this

;; (defn- html-element-system
;;   [m]
;;   (c/system-map
;;    :html-element
;;    (crm/make-html-element (:html-element m))))

;; (deftest html-element-component
;;   (testing "html element lifecycle"
;;     (let [started (-> {:html-element {:id          "app"
;;                                       :constructor identity}}
;;                       (html-element-system)
;;                       (c/start))
;;           stopped (c/stop started)]
;;       (is (some? (get-in started [:html-element :node])))
;;       (is (nil? (get-in stopped [:html-element :node]))))))
