(ns supplier-portal.ring.middleware.non-lan-auth
  (:require [inet.data.ip :as ip]))

(def local-networks
  (ip/network-set
    "127.0.0.1" "::1/128"
    "10.0.0.0/8"
    "172.16.0.0/12"
    "192.168.0.0/16"
    "fc00::/7"))

(defn wrap-non-lan-auth
  [handler auth-handler & auth-args]
  (let [auth (apply auth-handler (cons handler auth-args))]
    (fn [req]
      (if (get local-networks (:remote-addr req))
        (handler req)
        (auth req)))))
