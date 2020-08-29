(ns xml-stream-buffer.core
  (:require [clojure.data.xml :as xml :refer [parse]]))

(defn parsable []
  (with-open [input (java.io.FileInputStream. "parsable.xml")]
    (parse input :include-node? #{:element})))

(defn too-big []
  (with-open [input (java.io.FileInputStream. "too-big.xml")]
    (parse input)))


