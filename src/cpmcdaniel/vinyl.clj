(ns cpmcdaniel.vinyl
  (:require [cpmcdaniel.vinyl.files :as files]
            [cpmcdaniel.vinyl.console :as console])
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (->>
    (files/strings->files args)
    (files/parse-files)
    (console/print-all)))
