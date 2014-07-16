(ns supplier-portal.ring.api-handler
  (:require [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [plumbing.core :refer [keywordize-map]]
            [fnhouse.handlers :refer [nss->proto-handlers curry-resources]]
            [fnhouse.middleware :refer [coercion-middleware]]
            [fnhouse.routes :refer [root-handler]]))

(defn wrap-exception
  [handler]
  (fn [request]
    (try (handler request)
         (catch Exception e
           (.printStackTrace e)
           {:status 500
            :body "Exception caught"}))))

(defn keywordize-middleware
  [handler]
  (fn [req]
    (handler
      (update-in req [:query-params] keywordize-map))))

(defn ring-middleware
  [handler]
  (-> handler
      keywordize-middleware
      (wrap-json-body {:keywords? true})
      wrap-params
      wrap-json-response
      wrap-exception))

(defn custom-coercion-middleware
  [handler]
  (coercion-middleware handler (constantly nil) (constantly nil)))

(defn create-handler
  [resources prefix api-namespace]
  (let [proto-handlers (-> {prefix api-namespace} nss->proto-handlers)
        curried-handlers (-> resources ((curry-resources proto-handlers)))]
    (->> curried-handlers
         (map custom-coercion-middleware)
         root-handler
         ring-middleware)))
