(ns spock.spotify.handlers
  (:require [camel-snake-kebab.core :refer :all]
            [cheshire.core :as json]
            [clj-http.client :as client]
            [config.core :refer [env]]
            [mpd-clj.core :as mpd]
            [spock.auth :as auth]
            [taoensso.timbre :as log]))

(def api-url "https://api.spotify.com/v1/me")

(defn get-mpd-connection
  [mpd]
  (log/info "Get Mpd Connection")
  (if (= (mpd/status mpd) false)
    (do
      (log/info "Status was false")
      (mpd/client {:host (get-in env [:mpd :host])
                   :port (get-in env [:mpd :port])}))
    mpd))

(defn get-currently-playing-spot-api
  []
  (let [spotify-response (client/get (str api-url "/player/currently-playing")
                                     {:query-params
                                      {:access_token
                                       (:access-token @auth/access-token)}})
        result-info      (json/parse-string (:body spotify-response) true)
        item             (:item result-info)
        song-name        (:name item)
        artist           (:name (first (:artists item)))]
    (log/debug result-info)
    (str "Currently playing " song-name " by " artist)))

(defn pause-playback-spot-api
  []
  (let [spotify-response (client/put (str api-url "/player/pause")
                                     {:query-params
                                      {:access_token
                                       (:access-token @auth/access-token)}})
        result-info      (json/parse-string (:body spotify-response) true)]
    "Playback paused"))

(defn resume-playback-spot-api
  []
  (let [spotify-response (client/put (str api-url "/player/play")
                                     {:query-params
                                      {:access_token
                                       (:access-token @auth/access-token)}})
        result-info      (json/parse-string (:body spotify-response) true)]
    "Playback resumed"))

(defn send-help
  [response-url]
  (client/post response-url
               {:form-params
                {:text "Available Commands:\nwhats playing\npause\nresume"
                 :response_type "ephemeral"}
                :content-type :json})
  nil)

(defn get-currently-playing
  [mpd request])

(defn pause-playback
  [mpd]
  (log/info (mpd/stats mpd))
  (log/info (mpd/status mpd))
  (log/info (mpd/pause mpd))
  "Playback paused")

(defn resume-playback
  [mpd]
  (log/info (mpd/stats mpd))
  (log/info (mpd/status mpd))
  (log/info (mpd/resume mpd))
  "Playback resumed")

(defn delegate
  [mpd command response-url]
  (case command
    :whats-playing (get-currently-playing)
    :pause         (pause-playback mpd)
    :resume        (resume-playback mpd)
    (send-help response-url)))

(defn spotify-handler
  [mpd request]
  (let [form-params      (:form-params request)
        response-url     (get form-params "response_url")
        command          (->kebab-case-keyword (get form-params "text"))
        mpd-client       (get-mpd-connection mpd)
        spotify-response (delegate mpd-client command response-url)]
    (when-not (nil? spotify-response)
      (client/post response-url
                   {:form-params {:text spotify-response
                                  :response_type "in_channel"}
                    :content-type :json}))
    nil))
