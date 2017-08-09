(ns spock.exceptions
  (:require [compojure.api.exception :as ex]
            [ring.util.http-response :as response]
            [taoensso.timbre :as log]))

(defn wrap-error
  [handler]
  (fn [^Exception e data req]
    (log/error e)
    (handler e data req)))

(defn wrap-warn
  [handler]
  (fn [^Exception e data req]
    (log/warn e)
    (handler e data req)))

(defn custom-handler
 [handler]
 (fn [^Exception e data req]
   (log/error e)
   (handler {:message (.getMessage e)})))

(def exceptions
  {:handlers
    {::ex/request-parsing     (wrap-error ex/request-parsing-handler)
     ::ex/request-validation  (wrap-error ex/request-validation-handler)
     ::ex/response-validation (wrap-error ex/response-validation-handler)
     ::ex/default             (wrap-error (custom-handler response/internal-server-error))}})
