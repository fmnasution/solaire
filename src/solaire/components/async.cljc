(ns solaire.components.async
  (:require
   [com.stuartsierra.component :as c]
   [solaire.components.protocols :as cprt]
   #?@(:clj  [[clojure.core.async :as a :refer [go-loop]]]
       :cljs [[cljs.core.async :as a]]))
  #?(:cljs
     (:require-macros
      [cljs.core.async.macros :refer [go-loop]])))

;; ===============================================================
;; item dispatcher
;; ===============================================================

(defn- new-chan
  [{:keys [fixed sliding dropping] :as x}]
  (cond
    (nil? x)
    (a/chan)

    (pos-int? x)
    (a/chan x)

    (pos-int? fixed)
    (a/chan fixed)

    (pos-int? sliding)
    (a/chan (a/sliding-buffer sliding))

    (pos-int? dropping)
    (a/chan (a/dropping-buffer dropping))

    :else (a/chan)))

(defrecord ItemDispatcher [config item-chan]
  c/Lifecycle
  (start [this]
    (if (some? item-chan)
      this
      (assoc this :item-chan (new-chan config))))
  (stop [this]
    (if (nil? item-chan)
      this
      (do (a/close! item-chan)
          (assoc this :item-chan nil))))

  cprt/ISource
  (source-chan [this]
    (:item-chan this))

  cprt/ISink
  (sink-chan [this]
    (:item-chan this)))

(defn make-item-dispatcher
  [option]
  (-> option
      (select-keys [:config])
      (map->ItemDispatcher)))

;; ===============================================================
;; item listener
;; ===============================================================

(defn- collect-chans
  [component]
  (if-let [chans (transduce (comp
                             (map val)
                             (filter #(satisfies? cprt/ISource %))
                             (map cprt/source-chan))
                            conj
                            component)]
    chans
    (throw (ex-info "No channel to listen to" {}))))

(defrecord ItemListener [callback stop-chan]
  c/Lifecycle
  (start [this]
    (if (some? stop-chan)
      this
      (let [stop-chan (a/chan)
            chans     (collect-chans this)]
        (go-loop []
          (let [[item chan] (a/alts! (conj chans stop-chan) :priority true)
                stop?       (or (= stop-chan chan) (nil? item))]
            (when-not stop?
              (callback this item)
              (recur))))
        (assoc this :stop-chan stop-chan))))
  (stop [this]
    (if (nil? stop-chan)
      this
      (do (a/close! stop-chan)
          (assoc this :stop-chan nil)))))

(defn make-item-listener
  [option]
  (-> option
      (select-keys [:callback])
      (map->ItemListener)))

;; ===============================================================
;; item pipeliner
;; ===============================================================

(defn- pipeline!
  [kind parallelism to-chan updater from-chan close-both? ex-handler]
  (case kind
    :normal
    (a/pipeline parallelism
                to-chan
                updater
                from-chan
                close-both?
                ex-handler)

    :async
    (a/pipeline-async parallelism
                      to-chan
                      updater
                      from-chan
                      close-both?)

    #?@(:clj [:blocking
              (a/pipeline-blocking parallelism
                                   to-chan
                                   updater
                                   from-chan
                                   close-both?
                                   ex-handler)])

    (pipeline! :normal
               parallelism
               to-chan
               updater
               from-chan
               close-both?
               ex-handler)))

(defrecord ItemPipeliner [kind
                          parallelism
                          to
                          updater-fn
                          from
                          close-both?
                          ex-handler
                          started?]
  c/Lifecycle
  (start [this]
    (if started?
      this
      (let [parallelism (or parallelism 1)
            to-chan     (cprt/sink-chan to)
            updater     (updater-fn this)
            from-chan   (cprt/source-chan from)
            close-both? (if (nil? close-both?) true close-both?)]
        (pipeline! kind parallelism to-chan updater from-chan close-both? ex-handler)
        (assoc this :started? true))))
  (stop [this]
    (if-not started?
      this
      (assoc this :started? false))))

(defn make-item-pipeliner
  [option]
  (-> option
      (select-keys [:kind :parallelism :updater-fn :close-both? :ex-handler])
      (map->ItemPipeliner)))
