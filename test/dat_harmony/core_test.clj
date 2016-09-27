(ns dat-harmony.core-test
  (:require [clojure.test :refer :all]
            [dat-harmony.core :refer :all]
            [datascript.core :as d]))

(defn harmonize
  [conn1 conn2 selector eid]
  (d/transact! conn2 (pull-datoms d/q @conn1 selector eid))
  [(d/pull @conn1 selector eid)
   (d/pull @conn2 selector eid)])

(deftest a-test
  (testing "End to end"
    (let [schema {:ref {:db/cardinality :db.cardinality/many
                        :db/valueType :db.type/ref}}
          conn1 (d/create-conn schema)
          conn2 (d/create-conn schema)
          selector [:val {:_ref [:val]}]
          ]
      (d/transact! conn1 [{:db/id 1
                           :val "v1"
                           :ref [2 3]}
                          {:db/id 2 :val "v2"}
                          {:db/id 3 :val "v3"}])
      (let [[exp act] (harmonize conn1 conn2 selector 2)]
        (is (= exp act))))))
