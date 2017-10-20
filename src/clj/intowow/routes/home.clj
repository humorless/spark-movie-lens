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

(defn root-page [{{user-sess-id :identity} :session :as req}]
  (if (nil? user-sess-id)
    (layout/render "root.html"
                   {:movies (db/get-movie-average) :h2 "Hello Guest"})
    (let [{E :email :as user} (db/get-user-by-sess {:sess user-sess-id})]
      (layout/render "user.html"
                     {:movies (db/get-movie-average) :h2 E}))))

(defn rated-page [{{user-sess-id :identity} :session :as req}]
  (let [{id :id  :as user} (db/get-user-by-sess  {:sess user-sess-id})]
    (layout/render "rated.html"
                   {:movies (db/get-movie-rating-by-id {:id id}) :h2 (:email user)})))

(defn about-page []
  (layout/render "about.html"))

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

(defn is-authenticated [{user :user :as req}]
  (not (nil? user)))

(defroutes data-routes
  (POST "/logout" [] post-logout)
  (GET "/rated" [] rated-page)
  (GET "/data" [] root-page))

(defroutes home-routes
  (GET "/" [] root-page)
  (GET "/home" [] (home-page))
  (GET "/login" [] (get-login))
  (POST "/login" [] post-login)
  (GET "/accept" [] (get-accept))
  (GET "/reject" [] (get-reject))
  (GET "/register" [] (get-register))
  (POST "/register" [] post-register)
  (GET "/about" [] (about-page)))
