(ns supplier-portal.ring.middleware.react
  "A small piece of middleware to serve the react.js files provided
   by the swannodette/react-cljs package

   Useful when you're bundling those files up into your production JS
   and want to be able to refer to the same file in development."
  (:require [ring.util.codec :as codec]
            [ring.util.response :as response]
            [ring.util.request :as request]
            [ring.middleware.head :as head]))

(defn react-pattern
  [prefix]
  (re-pattern (str prefix (if prefix "/")
                   "(react/([^\\.]+)(\\.[^\\.]+)*\\.js$)")))

(defn react-request
  [req url-prefix]
  (if (= :get (:request-method req))
    (let [path (subs (codec/url-decode (request/path-info req)) 1)
          matcher (re-matcher (react-pattern url-prefix) path)]
      (if-let [react-path (second (re-find matcher))]
        (response/resource-response react-path)))))

(defn react-middleware
  ([handler] (react-middleware handler ""))
  ([handler url-prefix]
    (fn [req]
      (or ((head/wrap-head #(react-request % url-prefix)) req)
          (handler req)))))
