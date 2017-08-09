(ns spock.auth
  (:require [cemerick.friend :as friend]
            [config.core :refer [env]]
            [friend-oauth2.util :as util]
            [friend-oauth2.workflow :as oauth2]))

(def client-config
  {:client-id     (get-in env [:spotify-oauth2 :client-id])
   :client-secret (get-in env [:spotify-oauth2 :client-secret])
   :callback      (get-in env [:spotify-oauth2 :callback])})

(def access-token (atom nil))

(defn credential-fn
  [token]
  (swap! access-token assoc :access-token (:access-token token))
  {:identity token :roles #{::user}})

(def uri-config
  {:authentication-uri {:url   (get-in env [:spotify-oauth2 :auth-url])
                        :query {:client_id (:client-id client-config)
                                :response_type "code"
                                :redirect_uri (util/format-config-uri client-config)
                                :scope "user-read-currently-playing user-modify-playback-state"}}

   :access-token-uri {:url   (get-in env [:spotify-oauth2 :access-uri])
                      :query {:client_id (:client-id client-config)
                              :client_secret (:client-secret client-config)
                              :grant_type "authorization_code"
                              :redirect_uri (util/format-config-uri client-config)}}})

(def friend-config
  {:allow-anon? true
   :workflows   [(oauth2/workflow {:client-config        client-config
                                   :uri-config           uri-config
                                   :access-token-parsefn util/extract-access-token
                                   :credential-fn        credential-fn})]})
