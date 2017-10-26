(defproject intowow "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[gorillalabs/sparkling  "1.2.2"]
                 [org.apache.spark/spark-mllib_2.10  "1.1.0" :exclusions  [com.google.guava/guava org.slf4j/slf4j-log4j12]]
                 [org.apache.spark/spark-core_2.10  "1.1.0" :exclusions  [com.google.guava/guava com.thoughtworks.paranamer/paranamer org.slf4j/slf4j-log4j12]]
                 [buddy "1.3.0"]
                 [cheshire "5.8.0"]
                 [clj-time "0.14.0"]
                 [com.h2database/h2 "1.4.196"]
                 [compojure "1.6.0"]
                 [conman "0.6.9"]
                 [cprop "0.1.11"]
                 [funcool/struct "1.1.0"]
                 [hiccup "1.0.5"]
                 [luminus-immutant "0.2.3"]
                 [luminus-migrations "0.4.2"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.1"]
                 [metosin/muuntaja "0.3.2"]
                 [metosin/ring-http-response "0.9.0"]
                 [mount "0.1.11"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.7.1"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.4.0"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "3.3.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [org.webjars/jquery "3.2.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.6.2"]
                 [ring/ring-defaults "0.3.1"]
                 [selmer "1.11.1"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot intowow.core
  :migratus {:store :database :db ~(get (System/getenv) "DATABASE_URL")}

  :plugins [[lein-cprop "1.0.3"]
            [migratus-lein "0.5.2"]
            [lein-immutant "2.1.0"]]

  :profiles
  {:uberjar {:omit-source true
             :aot :all
             :uberjar-name "intowow.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.1.4"]
                                 [ring/ring-mock "0.3.1"]
                                 [ring/ring-devel "1.6.2"]
                                 [pjstadig/humane-test-output "0.8.2"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]]
                  
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :timeout 180000}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]}
   :profiles/dev {}
   :profiles/test {}}
   :aot [intowow.sparkling clojure.tools.logging.impl sparkling.serialization sparkling.destructuring])
