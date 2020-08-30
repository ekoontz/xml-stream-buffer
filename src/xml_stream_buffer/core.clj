(ns xml-stream-buffer.core
  (:require [clojure.data.xml :as xml :refer [parse]]))

(defn small []
  (with-open [input (java.io.FileInputStream. "small.xml")]
    (parse input :include-node? #{:element})))

(defn too-big []
  (with-open [input (java.io.FileInputStream. "big.xml")]
    (parse input :include-node? #{:element})))

(defn parseable []
  (with-open [input (java.io.FileInputStream. "big.xml")]
    (parse input :include-node? #{:element} :input-buffer-length 16384)))

