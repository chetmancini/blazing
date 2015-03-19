(def provided '[[org.apache.spark/spark-core_2.10 "1.1.0"]
                [org.apache.spark/spark-mllib_2.10 "1.1.0"]])

(defproject blazing "0.1.0-SNAPSHOT"
  :description "Clojure library for Spark Streaming with Flambo"
  :url "http://chetmancini.com/blazing"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [yieldbot/flambo "0.4.0"]]
  :profiles {:provided {:dependencies ~provided}
             :libdir {:dependencies ~provided}
             :dev {:aot [blazing.test-helper]}})
