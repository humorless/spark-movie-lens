(ns intowow.db.core
  (:require
   [intowow.data :as data]
   [clj-time.jdbc]
   [conman.core :as conman]
   [mount.core :refer [defstate]]
   [buddy.hashers :as hs]
   [intowow.config :refer [env]]))

(defstate ^:dynamic *db*
  :start (conman/connect! {:jdbc-url (env :database-url)})
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

(defn init-ratings!
  "initialize movie rating in database"
  []
  (create-ratings!
   {:ratings (mapv #(conj [] (:user %) (:item %) (:rating %))
                   (data/load-ratings "ua.base"))}))

(defn init-movies!
  "initialize movie id, name in database"
  []
  (map create-movies!
       (map (fn [[m-id m-name]] {:id m-id :name m-name})
            (data/load-items "u.item"))))

;; id-gen should be 1000 + count of user
(def id-gen (atom 1000))

(defn store-create-user!
  "store hash of pass into db."
  [email pass]
  (create-user! {:id (swap! id-gen inc) :email email :pass (hs/encrypt pass)}))
