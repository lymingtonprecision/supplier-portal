(ns supplier-portal.schemas
  (:require [schema.core :as s]
            [schema.macros :as sm]
            [clojure.string :as str]))

;; domain models
(s/defschema SupplierId
  (s/both s/Int
          (s/pred (fn [n] (>= 60000 n 50000)) 'in-range?)))

(s/defschema Supplier
  "Schema for a supplier"
  {:id SupplierId
   :name String
   :auth-token (s/maybe String)})

(s/defschema CapabilityType
  (s/enum ["material" "process"]))

(s/defschema Capability
  "Schema for a capability"
  {:id String
   :description String
   :category (s/maybe String)
   :type CapabilityType})

(s/defschema CapabilityUpdate
  "Schema for an update to a supplier capability"
  {:id String
   :type CapabilityType
   :selected s/Bool})

(s/defschema PartNumber
  (s/both String
          (s/pred (fn [s] (re-matches #"100\d{6}R(0\d|[1-9]\d+)" s))
                  'valid-part-number?)))

(s/defschema Part
  "Schema for a part"
  {:id (s/maybe PartNumber)
   :description String
   (s/optional-key :notes) (s/maybe [String])
   (s/optional-key :note-id) (s/maybe s/Int)})

(s/defschema Buyer
  "Schema for a buyer"
  {:id String})

(s/defschema LineItem
  "Schema for a line item"
  {:id s/Int
   :part Part
   :quantity s/Num
   :uom String
   :price-per s/Num
   :wanted s/Inst
   :promised s/Inst
   :notes (s/maybe [String])
   :note-id s/Int
   :status String})

(s/defschema QuotationLine
  "Schema for a quotation line"
  (merge LineItem
         {:quote-id s/Int
          :supplier-notes (s/maybe String)}))

(s/defschema Rfq
  "Schema for an RFQ"
  {:id s/Int
   :status String
   :expires s/Inst
   :buyer Buyer
   :notes (s/maybe [String])
   :note-id s/Int})

(s/defschema QuotationItem
  "Schema for a quotation item"
  {:part Part
   :lines [QuotationLine]})

(s/defschema Quote
  "Schema for a quotation"
  {:id s/Int
   :rev s/Int
   :received (s/maybe s/Inst)
   :last-modified s/Inst
   :currency String
   :rfq (s/maybe Rfq)
   :items [QuotationItem]
   :readonly (s/maybe s/Bool)})

(s/defschema QuotationUpdate
  "Schema for an update to a quotation
   A map of line numbers to updated fields"
  {s/Int {:price-per s/Num
          :promised s/Inst
          :declined s/Bool
          :notes String}})

(s/defschema OrderLineFeedback
  "Schema for order line feedback from a supplier"
  {:customer-issue s/Bool
   :supplier-issue s/Bool
   :comment (s/maybe String)})

(s/defschema OrderLineFeedbackUpdate
  "Schema for update of order line feedback from a supplier"
   (merge {:order-id s/Int :line-id s/Int} OrderLineFeedback))

(s/defschema OrderLine
  "Schema for an order line"
  (merge LineItem
         {:order-id s/Int
          :release s/Int
          :qty-shipped s/Num
          :qty-received s/Num
          :qty-accepted s/Num
          :qty-rejected s/Num
          :qty-outstanding s/Num
          (s/optional-key :shop-order) (s/either
                                         {:id nil :operation :nil}
                                         {:id String :operation s/Int})
          (s/optional-key :feedback) OrderLineFeedback}))

(s/defschema Order
  "Schema for a purchase order"
  {:id s/Int
   :lines [OrderLine]
   :status String})

;; api schema models
(s/defschema Ack
  "Simple acknowledgement for successful requests"
  {:message (s/eq "OK")})

(def ack
  {:status 200
   :body {:message "OK"}})

(s/defschema Missing
  {:message String})

(sm/defn not-found
  [message :- String]
  {:status 404
   :body {:message message}})
