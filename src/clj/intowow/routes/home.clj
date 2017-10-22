(ns intowow.routes.home
  (:require [intowow.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [intowow.db.core :as db]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn recommend-movies
  "This function implement the requirement #6"
  [uid]
  (let [item-id-set (set (mapv :item_id (db/get-movie-rating-by-id {:id uid})))
        raw-recommend-list (db/get-movie-average)]
    (remove
     #(item-id-set (:item_id %))
     raw-recommend-list)))

(defn root-page [{{user-sess-id :identity} :session :as req}]
  (if (nil? user-sess-id)
    (layout/render "root.html"
                   {:movies (db/get-movie-average)
                    :h2 "Hello Guest"})
    (let [{uid :id E :email :as user} (db/get-user-by-sess {:sess user-sess-id})]
      (layout/render "user.html"
                     {:movies (take 100 (recommend-movies uid))
                      :h2 E}))))

(defn rated-page [{{user-sess-id :identity} :session :as req}]
  (let [{id :id  :as user} (db/get-user-by-sess  {:sess user-sess-id})]
    (layout/render "rated.html"
                   {:movies (db/get-movie-rating-by-id {:id id}) :h2 (:email user)})))

(defn get-login []
  (layout/render "login.html"))

(defn get-accept []
  (layout/render "accept.html"))

(defn get-reject []
  (layout/render "reject.html"))

(defn get-register []
  (layout/render "register.html"))

(defn post-register [{{email "email" password "password"} :form-params}]
  (if (db/user-register! email password)
    (redirect "/accept")
    (redirect "/reject")))

(defn post-login [{{email "email" password "password"} :form-params
                   session :session :as req}]
  (if-let [user (db/user-auth email password)]

    ; If authenticated
    (assoc (redirect "/data")
           :session (assoc session :identity (:sess user)))

    ; Otherwise
    (redirect "/login")))

(defn post-logout [{session :session}]
  (assoc (redirect "/login")
         :session (dissoc session :identity)))

(defn post-rating [{{opt "optradio" itemid "itemid"} :form-params {user-sess-id :identity} :session :as req}]
  (let [{id :id  :as user} (db/get-user-by-sess  {:sess user-sess-id})]
    (log/info "user_id: " id "itemid: " itemid "rating" opt "type of opt:" (class opt))
    (if (nil? opt)
      (layout/render "empty-submit.html")
      (do (db/create-ratings! {:ratings [[id itemid (Integer/parseInt opt)]]})
          (redirect "/data")))))

(defroutes data-routes
  (POST "/logout" [] post-logout)
  (GET "/rated" [] rated-page)
  (GET "/data" [] root-page))

(defroutes submit-routes
  ;; submit_rating has no csrf checking
  (POST "/submit_rating" [] post-rating))

(defroutes home-routes
  (GET "/" [] root-page)
  (GET "/home" [] (home-page))
  (GET "/login" [] (get-login))
  (POST "/login" [] post-login)
  (GET "/accept" [] (get-accept))
  (GET "/reject" [] (get-reject))
  (GET "/register" [] (get-register))
  (POST "/register" [] post-register))
