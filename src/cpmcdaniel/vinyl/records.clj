(ns cpmcdaniel.vinyl.records
  (:require [clojure.string :as s]
            [clj-time.core :as t]
            [clj-time.format :as tf])
  (:import (java.io BufferedReader StringReader)))

(defrecord Record [last-name first-name gender favorite-color date-of-birth])

;; Delimiter data and logic
(def pipe-delimiter #"\s*\|\s*")
(def comma-delimiter #",\s*")
(def space-delimiter #"\s+")

(def delimiters [pipe-delimiter comma-delimiter space-delimiter])
(def delimiter? (set delimiters))

(defn check-delimiter!
  "Throws an exception if the delimiter could not be identified."
  [delimiter]
  (if (delimiter? delimiter)
    delimiter
    (throw (IllegalArgumentException. "Delimiter cannot be determined from input."))))

(defn get-delimiter
  "Given a line of input, try to determine which delimiter to use.
  Pipe is given highest priority, followed by comma, then space."
  [line]
  (check-delimiter!
    (first
     (filter
       (fn [delimiter]
         (= 5 (count (s/split line delimiter))))
       delimiters))))

;; Gender data and logic
(def gender-set #{"M" "F"})

(defn parse-gender
  "Parses a gender string into one of 'M' or 'F'. Valid
  inputs include (ingnoring case): 'M', 'F', 'male', 'female'"
  [^String g]
  (letfn [(short-gender [long-gender]
            (get {"MALE" "M"
                  "FEMALE" "F"}
                 long-gender
                 long-gender))]
    (-> (.toUpperCase g)
        short-gender
        gender-set)))

;; Date of birth logic
(def multi-date-parser
  (tf/formatter (t/default-time-zone)
                :basic-date
                :date
                "MM/dd/yyyy"
                "yyyy/MM/dd"))

(defn parse-date
  "Parses a date string. Accepts the following formats:
  - 20200616
  - 2020-06-16
  - 06/16/2020
  - 2020/06/16"
  [date-string]
  (tf/parse-local-date multi-date-parser date-string))

(defn format-date
  "Formats dates in MM/DD/YYYY format"
  [dt]
  (tf/unparse-local-date (tf/formatter "MM/dd/yyyy") dt))

(defn strings->record
  "Converts string arguments to a Record."
  [[last-name first-name gender-string color-string dob-string]]
  (->Record last-name
            first-name
            (parse-gender gender-string)
            color-string
            (parse-date dob-string)))

(defn check-record!
  "Throws an exception if the Record data is invalid."
  [record]
  (let [errors
        (cond-> []
                (= "" (:last-name record)) (conj "Last Name is a required field!")
                (= "" (:first-name record)) (conj "First Name is a required field!")
                (nil? (:gender record)) (conj "Gender must be M/F/male/female!")
                (= "" (:favorite-color record)) (conj "Favorite color is a required field!")
                (nil? (:date-of-birth record)) (conj "Invalid date of birth!"))]
    (if (empty? errors)
      record
      (throw (IllegalArgumentException.
               (str "Invalid record: "
                    (s/join "\n" errors)))))))

(defn make-line->record
  "Creates a fn for converting lines to records, based on the given delimiter."
  [delimiter]
  (fn [line]
    (let [args (map s/trim (s/split line delimiter))]
      (if (= 5 (count args))
        (check-record!
          (strings->record args))
        (throw (IllegalArgumentException.
                 (format "Expected 5 fields per line, received %d from line: %s"
                         (count args) line)))))))

(defprotocol RecordParser
  "Reads an input into a seq of Records"
  (parse-records [input]))

(extend-protocol RecordParser
  java.io.Reader
  (parse-records [input]
    (let [lines (line-seq (BufferedReader. input))
          delimiter (get-delimiter (first lines))
          line->record (make-line->record delimiter)]
      (map line->record lines)))

  String
  (parse-records [input]
    (parse-records (StringReader. input))))

(defn sort-by-gender
  [records]
  (sort-by :gender records))

(defn sort-by-dob
  [records]
  (sort-by :date-of-birth records))

(defn sort-by-last-name
  [records]
  (reverse (sort-by :last-name records)))

(def join-fields #(s/join ", " %))

(defn format-record
  "Formats a Record as a string."
  [rec]
  (-> rec
      (update :date-of-birth format-date)
      (vals)
      (join-fields)))

