(ns supplier-portal.http-api.v1
  (:use plumbing.core)
  (:require [schema.core :as s]
            [clojure.string :as str]
            [supplier-portal.schemas :as schema]
            [supplier-portal.supplier :as supplier]
            [supplier-portal.quotes :as quotes]))

(set! *warn-on-reflection* true)

(defnk $supplier$:supplier-id$GET
  "Get the specified supplier record"
  {:responses {200 schema/Supplier
               404 schema/Missing}}
  [[:request [:uri-args supplier-id :- schema/SupplierId]]
   [:resources database]]
  (if-let [s (supplier/get-supplier database supplier-id)]
    {:body s}
    (schema/not-found (format "Supplier %s not found" supplier-id))))

(defnk $supplier$:supplier-id$quotes$GET
  "Get the active quotes for the specified supplier"
  {:responses {200 [schema/Quote]
               404 schema/Missing}}
  [[:request [:uri-args supplier-id :- schema/SupplierId]]
   [:resources database]]
  (if-let [s (supplier/get-supplier database supplier-id)]
    {:body (quotes/active-for-supplier database s)}
    (schema/not-found "Not found")))

(set! *warn-on-reflection* false)
