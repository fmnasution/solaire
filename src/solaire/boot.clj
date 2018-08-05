(ns solaire.boot
  (:require
   [clojure.pprint :refer [pprint]]
   [boot.core :as bt]
   [boot.util :as btu]
   [clojure.tools.namespace.dir :as nsdir]
   [clojure.tools.namespace.track :as nstrk]
   [solaire.repl :as rpl]))

(defn- restart-system?
  [system-symbol prev-fileset next-fileset files {:keys [paths regexes]}]
  (when (some? system-symbol)
    (if (empty? files)
      (btu/info "No `files` to be watched."
                "Will not attempt to manage `system`'s lifecycles.")
      (let [input-files (bt/input-files
                         (bt/fileset-diff prev-fileset next-fileset))
            query-fn     (cond
                           paths
                           bt/by-path

                           regexes
                           bt/by-re

                           :else
                           bt/by-name)
            files        (if regexes (map re-pattern files) files)]
        (->> input-files
             (query-fn files)
             (seq)
             (boolean))))))

(bt/deftask system
  [s sys   VAL  sym   "Symbol of the system. Should point to a non-arity fn"
   f files VALS [str] "Files to be watched for restarting system. Name-based."
   p paths      bool  "Treat `files` as a path. Pick between this and `regexes`"
   r regexes    bool  "Treat `files` as a regex. Pick between this and `paths`"]
  (let [prev-fileset_ (atom nil)
        dirs          (into [] (bt/get-env :directories))
        tracker_      (atom (nsdir/scan-dirs (nstrk/tracker) dirs))
        init-system   (if (some? sys)
                        (delay
                         (btu/info "Starting system...\n")
                         (rpl/setup! sys)
                         (rpl/boot!))
                        (delay
                         (btu/info "No system is supplied."
                                   "Will only refresh namespaces.\n")))]
    (fn [next-task]
      (fn [fileset]
        (when (realized? init-system)
          (swap! tracker_ nsdir/scan-dirs)
          (rpl/reboot! @tracker_ (restart-system? sys
                                                  @prev-fileset_
                                                  fileset
                                                  files
                                                  {:paths   paths
                                                   :regexes regexes})))
        @init-system
        (next-task (reset! prev-fileset_ fileset))))))
