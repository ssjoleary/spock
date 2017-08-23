(ns spock.middleware
  (:require [cemerick.friend :as friend]
            [compojure.api.middleware :as compojure-api]
            [mpd-clj.core :as mpd]
            [spock.auth :as auth]
            [spock.mpd :as s-mpd]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [taoensso.timbre :as log]))

(defn wrap-mpd
  [handler]
  (fn [req]
    (let [mpd-details (get-in req [::compojure-api/components :mpd])
          connection  @mpd-details]
      (when (= false (mpd/status @connection))
        (log/info "refreshing connection")
        (s-mpd/refresh-connection! connection)))))

(defn wrap-middleware
  [handler]
  (-> handler
      (friend/authenticate auth/friend-config)
      (wrap-defaults site-defaults)))
