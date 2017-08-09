(ns spock.middleware
  (:require [cemerick.friend :as friend]
            [compojure.api.middleware :as compojure-api]
            [spock.auth :as auth]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn wrap-middleware
  [handler]
  (-> handler
      (friend/authenticate auth/friend-config)
      (wrap-defaults site-defaults)))
