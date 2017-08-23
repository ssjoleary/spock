(ns spock.mpd
  (:require [config.core :refer [env]]
            [mpd-clj.core :as mpd]
            [taoensso.timbre :as log]))

(defn mpd-client
  []
  (mpd/client (:mpd env)))

(def mpd-connection (atom (delay (mpd-client))))

(defn refresh-connection!
  [broken-connection]
  (if (compare-and-set! mpd-connection
                        broken-connection
                        (delay (mpd-client)))
    (log/info "closing connection" @broken-connection)))

(defn check-connection!
  [current-mpd]
  (log/info current-mpd)
  (let [connection @current-mpd]
    (log/info (mpd/status @connection))
    (when (= false (mpd/status @connection))
      (log/info "refreshing connection")
      (refresh-connection! connection))))
