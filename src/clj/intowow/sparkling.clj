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

(defn parse-long [i]
  (Long/parseLong i))

(defn parse-rating [line]
  (let [[user item rating time] (->> (str/split line #"\t")
                                     (map parse-long))]
    (spark/tuple (mod time 10)
                 (Rating. user item rating))))

(defn parse-ratings [sc]
  (->> (spark/text-file sc "resources/ua.base")
       (spark/map-to-pair parse-rating)))

(defn training-ratings [ratings]
  (->> ratings
       (spark/filter (fn [tuple]
                       (< (s-de/key tuple) 8)))
       (spark/values)))

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
          model (-> (parse-ratings sc)
                    (training-ratings)
                    (alternating-least-squares options))]
      (->> (.recommendProducts model 1 3)
           (map (comp id->name #(.product %)))))))
