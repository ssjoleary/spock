(ns spock.system
  (:require [com.stuartsierra.component :as component]
            [mpd-clj.core :as mpd]
            [spock.routes :as r]
            [immutant.web :as web]
            [taoensso.timbre :as log]))

(defrecord App
           [handler-fn]
  component/Lifecycle
  (start [this]
    (assoc this :handler (handler-fn this)))
  (stop [this]
    (assoc this :handler nil)))

(defrecord ImmutantWebServer
           [config]
  component/Lifecycle
  (start [this]
    (let [handler (get-in this [:app :handler :handler])]
      (assoc this :server (web/run handler config))))
  (stop [this]
    (when-let [server (:server this)]
      (web/stop server))
    (assoc this :server nil)))

(defrecord Mpd
           [config]
  component/Lifecycle
  (start [this]
    (let [mpd (mpd/client {:host (:host config)
                           :port (:port config)})]
      (assoc this :mpd mpd)))
  (stop [this]
    (assoc this :mpd nil)))

(defn new-system
  [{:keys [server mpd]
    :as   config}]
  (-> (component/system-map
       :app    (->App r/app)
       :server (->ImmutantWebServer server)
       :mpd    (mpd/client {:host (:host mpd)
                            :port (:port mpd)}))
      (component/system-using
       {:app    [:mpd]
        :server [:app]})))
