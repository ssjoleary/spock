(defproject spock "0.1.0-SNAPSHOT"
  :description "Spock - Slack controlled Spotify"
  :dependencies [[camel-snake-kebab          "0.4.0"]
                 [cheshire                   "5.7.0"]
                 [clj-http                   "3.4.1"]
                 [clj-time                   "0.13.0"]
                 [clojusc/friend-oauth2      "0.2.0"]
                 [com.cemerick/friend        "0.2.3" :exclusions [org.apache.httpcomponents/httpclient]]
                 [com.stuartsierra/component "0.3.2"]
                 [com.taoensso/sente         "1.11.0"]
                 [com.taoensso/timbre        "4.7.4"]
                 [environ                    "1.1.0"]
                 [metosin/compojure-api      "1.1.10"]
                 [mpd-clj                    "0.2.0"]
                 [org.clojure/clojure        "1.8.0"]
                 [org.clojure/core.async     "0.2.395"]
                 [org.immutant/web           "2.1.6"]
                 [ring/ring-core             "1.6.0-RC1"]
                 [ring/ring-defaults         "0.2.1"]
                 [yogthos/config             "0.8"]]
  :ring {:handler spock.routes/app}
  :main ^:skip-aot spock.core
  :repl-options {:init-ns user
                 :welcome (println (slurp (clojure.java.io/resource "repl.txt")))}
  :uberjar-name "spock-standalone.jar"
  :resource-paths ["resources"]
  :profiles {:dev  {:dependencies [[reloaded.repl "0.2.3"]
                                   [midje "1.8.3"]]
                    :source-paths ["dev"]
                    :resource-paths ["dev/resources"]
                    :plugins [[lein-environ "1.1.0"]
                              [lein-ring "0.10.0"]
                              [lein-midje "3.2.1"]]}
             :uberjar {:aot :all}})
