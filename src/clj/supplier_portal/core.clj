(ns supplier-portal.core
  (:require [com.stuartsierra.component :as component]
            [supplier-portal.ring.server :as http]
            [supplier-portal.ring.handler :refer [create-handler]])
  (:gen-class))

(def dummy-database
  {:data {:suppliers {50220 {:id 50220
                             :name "Thyssenkrupp"
                             :auth-token "617baJA29N"}
                      50048 {:id 50048
                             :name "Bridge Precision"
                             :auth-token "93hfka2AaA"}}
          :quotes {50220 {}}}})

(defn system
  ([] (system {}))
  ([options]
    (component/system-map
      :config {:env :development}
      :database dummy-database
      :ring-handler (component/using
                      (create-handler (:env options))
                      [:config :database])
      :http-server (component/using
                     (http/create-server options)
                     {:handler :ring-handler}))))

(defn -main [port]
  (let [sys (component/start (system {:port (Integer/parseInt port)}))]
    (println (str "HTTP server running at "
                  (-> sys :http-server :ip)
                  ":"
                  (-> sys :http-server :port)))))
