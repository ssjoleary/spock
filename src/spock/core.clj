(ns spock.core
  (:require [spock.system :as system]
            [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [taoensso.timbre :as log])
  (:gen-class))

(defn -main
  [& args]
  (let [system (component/start (system/new-system env))]
    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. ^Runnable #(component/stop system)))))
