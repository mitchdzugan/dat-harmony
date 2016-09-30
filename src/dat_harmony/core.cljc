(ns dat-harmony.core)

(defn get-datoms
  [query-res datom-desc]
  (map #(-> {:db/id (->> % :id-pos (get query-res))
             (:attr-name %) (->> % :value-pos (get query-res))})
       datom-desc))

(defn add-state-for-attr
  [pull-state attr-desc]
  (let [reverse? (-> attr-desc name (subs 0 1) (= "_"))
        attr (if reverse?
               (keyword (str
                         (if (namespace attr-desc)
                           (str (namespace attr-desc) "/")
                           "")
                         (subs (name attr-desc) 1)))
               attr-desc)
        make-sym #(symbol (str "?t" %))
        [e v] ((if reverse? reverse identity)
               [(:current-eid-ref pull-state)
                (:count pull-state)])]
    (-> pull-state
        (update :datom-desc
                #(conj % {:id-pos e
                          :attr-name attr
                          :value-pos v}))
        (update :where-items
                #(conj % [(make-sym e)
                          attr
                          (make-sym v)]))
        (update :count inc))))

(declare get-pull-state)

(defn pull-fold-function
  [pull-state pull-pattern]
  (cond
    (keyword? pull-pattern)
    (add-state-for-attr pull-state pull-pattern)
    (map? pull-pattern)
    (let [attr (first (keys pull-pattern))
          updated-pull-state (-> pull-state
                                 (add-state-for-attr attr)
                                 (assoc :current-eid-ref (:count pull-state)))]
      (get-pull-state (get pull-pattern attr) updated-pull-state))))

(defn get-pull-state
  ([selector]
  (let [initial-pull-state {:current-eid-ref 0
                            :count 1
                            :datom-desc []
                            :where-items []}]
    (get-pull-state selector initial-pull-state)))
  ([selector pull-state]
   (reduce pull-fold-function pull-state selector)))

(defn pull-datoms
  [q db selector eids]
  (let [pull-state (get-pull-state selector)
        query `[:find ~@(map #(symbol (str "?t" %)) (range (:count pull-state)))
                :in ~'$ [~'?t0 ...]
                :where ~@(:where-items pull-state)]
        query-res (q query db (flatten [eids]))]
    (->> query-res
         (map #(get-datoms % (:datom-desc pull-state)))
         (reduce concat)
         (into #{})
         vec)))

(defn to-datascript-schema
  [schema]
  (->> schema
       (map #(-> [(:db/ident %) {:db/cardinality (:db/cardinality %)
                                 :db/valueType (:db/valueType %)}]))
       (into {})))
