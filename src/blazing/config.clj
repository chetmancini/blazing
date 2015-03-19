(ns blazing.config
  (:require [clojure.tools.logging :as log]
            [flambo.conf :as conf]))

(def environment
  (let [properties (System/getProperties)]
    (->> (keys properties)
         (select-keys properties ,,,))))

(def default
  {"spark.akka.timeout" "300"
   "spark.executor.memory" "40g"
   "spark.kryoserializer.buffer.mb" "1024"
   "spark.akka.frameSize" "60"})

(defn set-master [spark-conf]
  (if-let [master (System/getenv "MASTER")]
    (conf/master spark-conf master)
    (conf/master spark-conf))) ;;defaults to "local[*]"

(defn log-config [config-map]
  (when (log/enabled? :info)
    (doseq [[k v] config-map]
      (log/infof "%s => %s" k v))))

(defn build-spark-conf
  ([app-name]
   (build-spark-conf app-name {}))
  ([app-name override]
   (let [config-map (merge default environment override)]
     (log-config config-map)
     (-> (conf/spark-conf)
         (conf/app-name ,,, app-name)
         (set-master ,,,)
         (conf/set ,,, config-map)))))
