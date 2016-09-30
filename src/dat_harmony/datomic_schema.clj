(ns dat-harmony.datomic-schema
  (:require [datomic.api]))

(def to-datomic-schema
  (partial map #(let [partition (:partition %)
                      partition-id-makers {:db.part/db #db/id [:db.part/db]
                                           :db.part/tx #db/id [:db.part/tx]
                                           :db.part/user #db/id [:db.part/user]}]
                  (-> (if (:install %)
                        (assoc % :db.install/_attribute partition)
                        %)
                      (assoc :db/id (get partition-id-makers partition))
                      (dissoc :auth :install :partition)))))
