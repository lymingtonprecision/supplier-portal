(ns supplier-portal.ring.middleware.supplier-auth
  (:require [ring.util.request :as request]
            [ring.middleware.json :refer [wrap-json-response]]
            [supplier-portal.supplier :as supplier]))

(def header-token "X-LPE-Supplier-Auth")

(defn not-authorized [req]
  {:status 403
   :headers {"Content-Type" "text/plain"}
   :body "Invalid auth token for supplier"})

(def supplier-uri-matcher #"supplier\/(5\d{4})")

(defn match-supplier-uri
  [req]
  (re-find supplier-uri-matcher (request/path-info req)))

(defn auth-token-from-request
  [req]
  (let [params (:query-params req)
        key-param (or (:k params) (get params "k"))
        header-token (some (fn [[k v]]
                             (if (.equalsIgnoreCase header-token k) v))
                           (:headers req))]
    (or key-param header-token)))

(defn supplier-from-uri
  [database]
  (fn [req]
    (let [m (re-matcher supplier-uri-matcher (request/path-info req))]
      (if-let [id (second (re-find m))]
        (supplier/get-supplier database (Integer/parseInt id))))))

(defn authorized-request
  [req matcher lookup authenticate]
  (if (matcher req)
    (authenticate (lookup req) (auth-token-from-request req))
    true))

(defn wrap-supplier-auth
  [handler & [opts]]
  (let [matcher (or (:match opts) match-supplier-uri)
        lookup (or (:lookup opts) (supplier-from-uri (:database opts)))
        auth (or (:auth opts) supplier/authenticates)
        failure-callback (or (:failure opts) not-authorized)]
    (fn [req]
      (if (authorized-request req matcher lookup auth)
        (handler req)
        (failure-callback req)))))
