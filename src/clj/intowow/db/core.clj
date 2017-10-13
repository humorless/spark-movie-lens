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

(defn uuid [] (java.util.UUID/randomUUID))

(defn user-register!
  " if not exists email, store (email, hash(pass)) into db.
    else return false"
  [email pass]
  (if (nil? (get-user-by-email {:email email}))
    (create-user! {:id nil :email email :pass (hs/encrypt pass) :sess (uuid)})
    false))

(defn user-auth
  " user-auth returns user record or false
    get-user-by-email returns nil or {:id .., :pass ... :email ... }"
  [email pass]
  (if-let [user (get-user-by-email {:email email})]
    (if (hs/check pass (:pass user)) user false)
    false))
