(ns cpmcdaniel.vinyl.files
  (:require [clojure.java.io :as io]
            [cpmcdaniel.vinyl.records :as recs])
  (:import (cpmcdaniel.vinyl.records RecordParser)))

(defn strings->files
  "Converts string paths to files. Throws exception if one of the files does not exist."
  [args]
  (for [file-string args
        :let [file (io/file file-string)]]
    (do
      (when (not (.exists file))
        (throw (IllegalArgumentException.
                 (format "%s does not exist" file-string))))
      (when (not (.isFile file))
        (throw (IllegalArgumentException.
                 (format "%s is not a regular file" file-string))))
      file)))

(defn parse-files [files]
  (mapcat recs/parse-records files))
