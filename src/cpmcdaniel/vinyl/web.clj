(ns cpmcdaniel.vinyl.web
  (:require [compojure.core :refer :all]
            [ring.middleware.json :as json]
            [ring.util.response :as resp]
            [cpmcdaniel.vinyl.records :as recs]))

(def db (atom #{}))

(defn create-records
  [{:keys [^java.io.InputStream body]}]
  (let [records (recs/parse-records body)]
    (swap! db into records)
    {:status 201}))

(defn get-records
  [sort-fn]
  (resp/response
    (map #(update % :date-of-birth recs/format-date) (sort-fn @db))))

(defroutes
  handler
  (POST "/records" request (create-records request))
  (GET "/records/gender" [] (get-records recs/sort-by-gender))
  (GET "/records/birthdate" [] (get-records recs/sort-by-dob))
  (GET "/records/name" [] (get-records recs/sort-by-last-name)))

(def app (json/wrap-json-response
           handler))