(defproject dat-harmony "0.1.1-SNAPSHOT"
  :description "Datomic <-> Datascript syncing utilities"
  :url "https://github.com/mitchdzugan/dat-harmony"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [com.datomic/datomic-free "0.9.5344"]]
  :plugins [[lein-cljsbuild "1.1.4"]]
  :profiles {:dev {:dependencies [[datascript "0.15.4"]]}})
