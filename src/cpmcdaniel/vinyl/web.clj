(ns cpmcdaniel.vinyl.web
  (:require [compojure.core :refer :all]
            [ring.middleware.json :as json]
            [ring.util.response :as resp]
            [cpmcdaniel.vinyl.records :as recs]
            [clojure.stacktrace :refer [print-stack-trace]]))

(def db (atom #{}))

(defn create-records
  [{:keys [^java.io.InputStream body]}]
  (try
    (let [records (recs/parse-records body)]
      (swap! db into records)
      {:status 201})
    (catch IllegalArgumentException e
      (->
        (with-out-str
          (print "Invalid input: ")
          (print-stack-trace e))
        (resp/bad-request)
        (resp/header "Content-Type" "text/plain")))))

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