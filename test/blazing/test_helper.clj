(ns blazing.test-helper
  (:require [clojure.java.io :as jio]
            [clojure.string :as string]
            [blazing.config :as config]
            [flambo.api :as f])
  (:import [java.nio.file Files]
           [java.nio.file.attribute FileAttribute]
           [scala Tuple2]))

;; Helpers and fixturing

(declare ^:dynamic shared-spark-context)

(def config-options
  {"spark.kryoserializer.buffer.mb" "64"
   "spark.executor.memory" "2g"
   "spark.akka.frameSize" "10"})

(defn fixture-spark-context
  ([f]
   (fixture-spark-context #'shared-spark-context f))
  ([ctx-var f]
   (f/with-context ctx (config/build-spark-conf "util-test" config-options)
     (with-bindings {ctx-var ctx}
       (f)))))

(def java-rdd? (partial instance? org.apache.spark.api.java.JavaRDD))

(defn vec->tuple2 [[k v & _]] (Tuple2. k v))

(defn get-path [s]
  (.getPath (jio/resource s)))

(defn temp-dir
  ([]
   (temp-dir "" true))
  ([prefix]
   (temp-dir prefix true))
  ([prefix delete?]
   (when-let [file (->> (into-array FileAttribute [])
                        (Files/createTempDirectory prefix ,,,)
                        (.toFile ,,,))]
     (when delete?
       (.deleteOnExit file))
     file)))

(defn filenames [pathname]
  (->> (jio/as-file pathname)
       file-seq
       (remove (memfn isDirectory) ,,,)
       (map #(string/replace-first (str %) pathname "") ,,,)))
