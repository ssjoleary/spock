(ns spock.routes
  (:require [cemerick.friend :as friend]
            [clojure.java.io :as io]
            [compojure.api.sweet :refer [ANY GET POST api context middleware defroutes]]
            [spock.auth :as auth]
            [spock.exceptions :as ex]
            [spock.middleware :as mw]
            [spock.spotify.handlers :as spotify]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.util.response :as response]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(defn app
  [components]
  (api
   {:swagger
    {:ui "/docs"
     :spec "/swagger.json"
     :data {:info {:title "Spock"
                   :description "Slack controlled Spotify"}
            :tags [{:name "auth" :description "User Authentication"}]}}
    :components components
    :exceptions ex/exceptions}

   (middleware
    [[wrap-defaults api-defaults]]
    (GET "/" [] (io/resource "repl.txt"))
    (context "/healthcheck" [] :tags ["Healthcheck"]
      (GET "/ping" []
        (-> (response/response "pong")
            (response/content-type "text/plain; charset=UTF-8"))))

    (POST "/spock" request
      :components [mpd]
      (log/info "Route handler")
      (-> (spotify/spotify-handler mpd request)
          response/response))

    (middleware
     [mw/wrap-middleware]
     (context "/auth" [] :tags ["auth"]
       (GET "/login" [] (friend/authorize #{:spock.auth/user} (response/redirect "/"))))))))
