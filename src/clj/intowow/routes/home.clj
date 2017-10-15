(ns intowow.routes.home
  (:require [intowow.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [intowow.db.core :as db]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn root-page []
  (layout/render "root.html"
                 {:movies (db/get-movie-average)}))

(defn about-page []
  (layout/render "about.html"))

(defn get-login []
  (layout/render "login.html"))

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

(defn is-authenticated [{user :user :as req}]
  (not (nil? user)))

(defroutes data-routes
  (GET "/data" [] "hello data"))

(defroutes home-routes
  (GET "/" [] (root-page))
  (GET "/home" [] (home-page))
  (GET "/login" [] (get-login))
  (POST "/login" [] post-login)
  (POST "/logout" [] post-logout)
  (GET "/about" [] (about-page)))
