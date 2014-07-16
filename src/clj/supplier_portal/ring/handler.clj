(ns supplier-portal.ring.handler
  (:require [com.stuartsierra.component :as component]
            [bidi.bidi :as b]
            [ring.util.response :as response]
            [ring.middleware
              [reload :refer [wrap-reload]]
              [params :refer [wrap-params]]
              [file :refer [wrap-file]]
              [content-type :refer [wrap-content-type]]
              [not-modified :refer [wrap-not-modified]]]
            [supplier-portal.ring.middleware
              [react :refer [react-middleware]]
              [supplier-auth :refer [wrap-supplier-auth]]
              [non-lan-auth :refer [wrap-non-lan-auth]]]
            [supplier-portal.ring
              [frontend-handler :as frontend]
              [api-handler :as api]]
            [supplier-portal.http-api.v1]))

(defn not-found [req] (response/not-found "Not found"))

(defn routes [frontend-handler api-handlers]
  (let [asset-handler (-> not-found
                          (react-middleware "assets/scripts")
                          (wrap-file "resources/public")
                          wrap-content-type
                          wrap-not-modified)]
    ["/" {#"assets/.*" {:get asset-handler}
          "api/" api-handlers
          #"s/.*" {:get frontend-handler}}]))

(defn wrap-auth [handler resources]
  (-> (wrap-non-lan-auth handler wrap-supplier-auth resources)
      (wrap-params)))

(defn site-handler
  [env resources]
  (let [api (-> (api/create-handler
                  resources
                  "api/v1"
                  'supplier-portal.http-api.v1)
                (wrap-auth resources))
        fe (-> (frontend/create-handler env)
               (wrap-auth resources))
        h (b/make-handler (routes fe {#"v1/.*" api}))]
    (if (= :production env) h (wrap-reload h))))

(defrecord Handler [config database ring-handler]
  component/Lifecycle

  (start [this]
    (if ring-handler
      this
      (assoc this :ring-handler (site-handler (:env config)
                                              {:database database}))))

  (stop [this]
    (dissoc this :ring-handler)))

(defn create-handler
  [{:keys [config database]}]
  (map->Handler {:config config :database database}))
