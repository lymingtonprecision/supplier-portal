(ns supplier-portal.ring.frontend-handler
  (:require [ring.util.request :as request]
            [net.cgrand.enlive-html :as html]
            [net.cgrand.reload]))

(net.cgrand.reload/auto-reload *ns*)

(html/defsnippet debug-script "templates/frontend/debug_script.html"
  [:script]
  [])

(html/deftemplate index-template "templates/frontend/index.html"
  [debug]
  [:#portal-script] (if debug (html/substitute (debug-script)) identity))

(defn serve-frontend [req env]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (index-template (not (= :production env)))})

(defn create-handler
  ([] (create-handler ""))
  ([prefix] (create-handler "" :development))
  ([prefix env]
    (fn [req]
      (if (= 0 (.indexOf (request/path-info req) prefix))
        (serve-frontend req env)))))
