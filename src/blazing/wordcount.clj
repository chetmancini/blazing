(ns blazing.wordcount
  (:require [flambo.conf :as conf]
            [flambo.api :as f]
            [flambo.streaming :as fs])
  (:require [clojure.string :as s])
  (:gen-class))

(def master "local")
(def app-name "blazing")
(def conf {})
(def env {
           "spark.executor.memory" "1G",
           "spark.files.overwrite" "true"})

(defn -main [& args]
  (let [c (-> (conf/spark-conf)
              (conf/master master)
              (conf/app-name "adapters")
              (conf/set "spark.akka.timeout" "300")
              (conf/set-executor-env env))
        ssc (fs/streaming-context c 2000) ;; get a streaming context with a 2 second batch interval
        stream (fs/text-file-stream "gettysburg_address.clj")]

    (-> stream
        (fs/map (memfn _2))
        (fs/flat-map (f/fn [l] (s/split l #" ")))
        (fs/map (f/fn [w] [w 1]))
        (fs/reduce-by-key-and-window (f/fn [x y] (+ x y)) (* 10 60 1000) 2000)
        (fs/print))
    (.start ssc)
    (.awaitTermination ssc)))