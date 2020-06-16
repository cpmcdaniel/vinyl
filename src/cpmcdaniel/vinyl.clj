(ns cpmcdaniel.vinyl
  (:require [cpmcdaniel.vinyl.files :as files]
            [cpmcdaniel.vinyl.console :as console]
            [cpmcdaniel.vinyl.web :as web]
            [ring.adapter.jetty :as jetty])
  (:gen-class))


(defn -main
  "When called with 0 args, will expose a REST API on port 8999.
  Any arguments passed must be valid file paths and contain records
  using any of the 3 delimiters (pipe, comma, space). If arguments are
  passed, output is written to standard out."
  [& args]
  (if (pos? (count args))
    (->>
      (files/strings->files args)
      (files/parse-files)
      (console/print-all))

    (jetty/run-jetty web/app {:port 8999
                              :join? true})))
