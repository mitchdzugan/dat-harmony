(ns dat-harmony.core)

(defn add-id-to-pull
  [sel]
  (->> sel
       (map #(if (map? %)
               (->> %
                    (map (fn [[k v]] [k (add-id-to-pull v)]))
                    (into {}))
               %))
       (#(conj % :db/id))
       (into #{})
       (into [])))

(defn pull-res-to-datoms
  [{:keys [db/id] :as res}]
  (->> res
       (filter (fn [[k v]] (not= :db/id k)))
       (reduce (fn [datoms [k v]]
                 (into [] (concat datoms
                                  (cond
                                    (and
                                     (map? v)
                                     (contains? v :db/id))
                                    (conj
                                     (pull-res-to-datoms v)
                                     {:db/id id
                                      k (:db/id v)})
                                    (and
                                     (sequential? v)
                                     (every? #(and
                                               (map? %)
                                               (contains? % :db/id)) v))
                                    (concat (mapcat pull-res-to-datoms v)
                                            (map #(-> {:db/id id
                                                       k (:db/id %)}) v))
                                    :else [{:db/id id
                                            k v}]))))
               [])
       (into [])))

(defn pull-datoms
  [pull-many db selector eids]
  (->> (pull-many db (add-id-to-pull selector) (flatten [eids]))
       (mapcat pull-res-to-datoms)
       (into #{})
       (into [])))

(defn to-datascript-schema
  [schema]
  (->> schema
       (map #(-> [(:db/ident %) {:db/cardinality (:db/cardinality %)
                                 :db/valueType (:db/valueType %)}]))
       (into {})))
