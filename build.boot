(set-env!
 :source-paths #{"src/"}
 :dependencies '[;; ---- clj ----
                 [org.clojure/clojure "1.10.0-alpha5"]
                 [aero "1.1.3"]
                 [com.datomic/datomic-free "0.9.5697"]
                 [io.rkn/conformity "0.5.1"]
                 [http-kit "2.3.0"]
                 ;; ---- cljc ----
                 [org.clojure/core.async "0.4.474"]
                 [com.stuartsierra/component "0.3.2"]
                 [bidi "2.1.3"]
                 [rum "0.11.2"]
                 [datascript "0.16.6"]
                 [datascript-schema "0.2.1"]
                 [cljs-ajax "0.7.4"]
                 [com.taoensso/sente "1.12.0"]
                 ;; ---- cljs -----
                 [org.clojure/clojurescript "1.10.339"]
                 ;; ---- dev ----
                 [org.clojure/tools.reader "1.3.0" :scope "test"]
                 [samestep/boot-refresh "0.1.0" :scope "test"]
                 [metosin/bat-test "0.4.0" :scope "test"]])

(require
 '[samestep.boot-refresh :refer [refresh]]
 '[metosin.bat-test :refer [bat-test]])

(deftask dev-repl
  []
  (merge-env!
   :source-paths #{"test/"}
   :resource-paths #{"resources/"})
  (comp
   (repl :server true)
   (watch)
   (refresh)
   (bat-test)))
