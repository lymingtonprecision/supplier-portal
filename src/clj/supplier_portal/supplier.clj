(ns supplier-portal.supplier
  (:require [schema.core :as s]
            [schema.macros :as sm]
            [supplier-portal.schemas :as schema]))

(sm/defn get-supplier
  [database id :- schema/SupplierId]
  (get (->> database :data :suppliers) id))

(sm/defn authenticates
  [supplier :- schema/Supplier auth-token]
  (= (:auth-token supplier) auth-token))
