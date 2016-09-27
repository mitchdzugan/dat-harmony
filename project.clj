(defproject dat-harmony "0.1.0-SNAPSHOT"
  :description "Datomic <-> Datascript syncing utilities"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]]
  :plugins [[lein-cljsbuild "1.1.4"]]
  :profiles {:dev {:dependencies [[datascript "0.15.4"]]}})
