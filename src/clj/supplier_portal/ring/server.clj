(ns supplier-portal.ring.server
  (:require [com.stuartsierra.component :as component]
            [org.httpkit.server :refer [run-server]]))

(defrecord HttpServer [handler ip port server]
  component/Lifecycle

  (start [this]
    (if server
      this
      (let [s (run-server (:ring-handler handler) {:ip ip :port port})]
        (assoc this :server s
                    :ip ip
                    :port (:local-port (meta s))))))

  (stop [this]
    (if (not server)
      this
      (do (server)
          (dissoc this :server)))))

(defn create-server
  [{:keys [handler ip port] :or {ip "0.0.0.0" port 0}}]
  (map->HttpServer {:handler handler :ip ip :port port}))
