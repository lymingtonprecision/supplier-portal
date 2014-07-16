(defproject supplier-portal "0.1.0-SNAPSHOT"
  :description "Web based supplier portal"
  :url "https://github.com/lymingtonprecision/supplier-portal"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.reader "0.8.4"]
                 [org.clojure/tools.namespace "0.2.4"]

                 [com.stuartsierra/component "0.2.1"]
                 [prismatic/plumbing "0.2.2"]
                 [prismatic/schema "0.2.1"]
                 [prismatic/fnhouse "0.1.0"]

                 [inet.data "0.5.5"]

                 [ring/ring-core "1.3.0"]
                 [ring/ring-devel "1.3.0"]
                 [ring/ring-json "0.2.0"]
                 [http-kit "2.1.18"]
                 [bidi "1.10.4"]
                 [enlive "1.1.5"]

                 [org.clojure/clojurescript "0.0-2268"]
                 [om "0.6.5"]
                 [secretary "1.2.0"]
                 [kioo "0.4.0" :exclusions [com.cemerick/clojurescript.test]]]

  :source-paths ["src/clj"]

  :main supplier-portal.core
  :aot [supplier-portal.core]

  :repl-options {:host "0.0.0.0"}

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]]
  :hooks [leiningen.cljsbuild]

  :cljsbuild {
    :builds {
      :dev {:source-paths ["src/cljs"]
            :compiler {
              :output-to "resources/public/assets/scripts/portal-debug.js"
              :output-dir "resources/public/assets/scripts/"
              :optimizations :none
              :pretty-print true
              :source-map true
              :source-map-path "assets/scripts"}}
      :prod {:source-paths ["src/cljs"]
             :compiler {
               :output-to "resources/public/assets/scripts/portal.js"
               :optimizations :advanced
               :elide-asserts true
               :pretty-print false
               :output-wrapper false
               :preamble ["react/react.min.js"]
               :externs ["react/react.js"]
               :closure-warnings {:externs-validation :off
                                  :non-standard-jsdoc :off}}}}})
