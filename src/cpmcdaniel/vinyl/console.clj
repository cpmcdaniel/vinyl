(ns cpmcdaniel.vinyl.console
  (:require [cpmcdaniel.vinyl.records :as recs])
  (:import [cpmcdaniel.vinyl.records Record]))

(defmethod print-method Record [r ^java.io.Writer w]
  (.write w (recs/format-record r)))

(defn print-all
  "Prints all records, first sorted by gender, then birth date, then last name descending."
  [records]
  (println "Sorted by gender:")
  (doseq [rec (recs/sort-by-gender records)]
    (prn rec))
  (println)
  (println "Sorted by birth date:")
  (doseq [rec (recs/sort-by-dob records)]
    (prn rec))
  (println)
  (println "Sorted by last name, ascending:")
  (doseq [rec (recs/sort-by-last-name records)]
    (prn rec)))