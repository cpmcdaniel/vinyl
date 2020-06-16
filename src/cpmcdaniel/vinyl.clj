(ns cpmcdaniel.vinyl
  (:require [cpmcdaniel.vinyl.files :as files]
            [cpmcdaniel.vinyl.console :as console]
            [cpmcdaniel.vinyl.web :as web]
            [ring.adapter.jetty :as jetty])
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if (pos? (count args))
    (->>
      (files/strings->files args)
      (files/parse-files)
      (console/print-all))

    (jetty/run-jetty web/app {:port 8999
                              :join? true})))
