(ns user
  (:require [clojure.repl :refer :all]
            [com.stuartsierra.component :as component]
            [config.core :refer [env]]
            [spock.system :as system]
            [reloaded.repl :refer [system init start stop go reset reset-all]]))

(reloaded.repl/set-init! #(system/new-system env))
