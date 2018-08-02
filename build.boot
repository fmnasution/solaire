(set-env!
 :source-paths #{"src/"}
 :dependencies '[;; ---- clj ----
                 [org.clojure/clojure "1.10.0-alpha5"]
                 [com.datomic/datomic-free "0.9.5697"]
                 ;; ---- cljc ----
                 [com.stuartsierra/component "0.3.2"]
                 ;; ---- dev ----
                 [org.clojure/tools.reader "1.3.0" :scope "test"]
                 [samestep/boot-refresh "0.1.0" :scope "test"]
                 [metosin/bat-test "0.4.0" :scope "test"]])

(require
 '[samestep.boot-refresh :refer [refresh]]
 '[metosin.bat-test :refer [bat-test]])

(deftask dev-repl
  []
  (comp
   (repl :server true)
   (watch)
   (refresh)
   (bat-test)))
