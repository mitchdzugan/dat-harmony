(ns dat-harmony.core-test
  (:require [clojure.test :refer :all]
            [dat-harmony.core :refer :all]
            [dat-harmony.datomic-schema :refer :all]
            [datascript.core :as d]))

(defn harmonize
  [conn1 conn2 selector eid]
  (d/transact! conn2 (pull-datoms d/pull-many @conn1 selector eid))
  [(d/pull-many @conn1 selector (flatten [eid]))
   (d/pull-many @conn2 selector (flatten [eid]))])

(deftest pull-datoms-test
  (testing "Pull Datoms"
    (let [schema {:ref {:db/cardinality :db.cardinality/many
                        :db/valueType :db.type/ref}}
          conn1 (d/create-conn schema)
          conn2 (d/create-conn schema)
          selector [:val {:ref [:val :vil]}]
          ]
      (d/transact! conn1 [{:db/id 1
                           :val "v1"
                           :ref [2 3]}
                          {:db/id 2 :val "v2"}
                          {:db/id 3 :vil "e3" :val ["v3" "v2"]}])
      (let [[exp act] (harmonize conn1 conn2 selector 1)]
        (is (= exp act))))))

(deftest datomic-schema-test
  (testing "Datomic Schema"
    (let [schema-glo [{:partition :db.part/db
                       :install true
                       :db/ident :token/value
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "token value"
                       :auth false}]
          schema-dat [{:db/id #db/id [:db.part/db]
                       :db/ident :token/value
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "token value"
                       :db.install/_attribute :db.part/db}]]
      (is (=
           (map #(dissoc % :db/id) schema-dat)
           (map #(dissoc % :db/id) (to-datomic-schema schema-glo)))))))

(deftest datascript-schema-test
  (testing "Datomic Schema"
    (let [schema-glo [{:partition :db.part/db
                       :install true
                       :db/ident :token/value
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one
                       :db/doc "token value"
                       :auth false}]
          schema-dsc {:token/value {:db/cardinality :db.cardinality/one
                                    :db/valueType :db.type/string}}]
      (is (=
           schema-dsc
           (to-datascript-schema schema-glo))))))
