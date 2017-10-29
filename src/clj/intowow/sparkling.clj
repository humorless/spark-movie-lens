(ns intowow.sparkling
  (:gen-class)
  (:require [intowow.db.core :as db]
            [clojure.string :as str]
            [sparkling.conf :as conf]
            [sparkling.core :as spark]
            [sparkling.debug :as s-dbg]
            [sparkling.destructuring :as s-de]
            [sparkling.kryo :as k]
            [sparkling.scalaInterop :as scala])
  (:import [org.apache.spark.api.java JavaRDD]
           [org.apache.spark.mllib.linalg Vector SparseVector]
           [org.apache.spark.mllib.linalg.distributed RowMatrix]
           [org.apache.spark.mllib.recommendation ALS Rating]))

(defn trans-rating [item]
  (spark/tuple (rand-int 10)
               (Rating. (:user_id item) (:item_id item) (:rating item))))

(defn get-db-ratings [sc]
  (->> (spark/parallelize sc (apply vector (db/get-movie-ratings)))
       (spark/map-to-pair trans-rating)))

(defn to-mllib-rdd [rdd]
  (.rdd rdd))

(defn alternating-least-squares [data {:keys [rank num-iter
                                              lambda]}]
  (ALS/train (to-mllib-rdd data) rank num-iter lambda 10))

(defn ex-7-39 []
  (spark/with-context sc (-> (conf/spark-conf)
                             (conf/master "local")
                             (conf/app-name "ch7"))
    (let [items (apply assoc {}
                       (mapcat #(vector (:id %) (:name %)) (db/get-movies)))
          id->name (fn [id] (get items id))
          options {:rank 10
                   :num-iter 10
                   :lambda 1.0}
          model (-> (get-db-ratings sc)
                    (spark/values)
                    (alternating-least-squares options))]
      (->> (.recommendProducts model 1 3)
           (map (comp id->name #(.product %)))))))

(def sc
  (let [cfg (-> (conf/spark-conf)
                (conf/master "local[*]")
                (conf/app-name "sparkling"))]
  (spark/spark-context cfg)))

(def model (atom {}))

(defn new-model
  "create a new model.
   Class: org.apache.spark.mllib.recommendation.MatrixFactorizationModel"
  []
  (let [options {:rank 8
                 :num-iter 1
                 :lambda 1.0}]
    (-> (get-db-ratings sc)
        (spark/values)
        (alternating-least-squares options))))

(defn re-train
  "change old model with new model"
  []
  (reset! model (new-model)))

(defn recommend
  " return data like
  ( [1536 \"Aiqing wansui (1994)\" 3.75890842304711]
    [1500 \"Santa with Muscles (1996)\" 3.723743476150793]
    ... )
  "
  [uid number]
  (let [items (apply assoc {}
                     (mapcat #(vector (:id %) (:name %)) (db/get-movies)))
        id->name (fn [id] (get items id))]
    ;; .recommendProducts returns an object with methods:
    ;; .user .product .rating
    (map #(vector (.product %) (id->name (.product %)) (.rating %))
         (.recommendProducts @model uid number))))
