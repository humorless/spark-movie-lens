(ns intowow.routes.home
  (:require [intowow.sparkling :as spk]
            [intowow.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [intowow.db.core :as db]
            [clojure.tools.logging :as log]
            [cheshire.core :refer :all]
            [hiccup.core :refer :all]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
   "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(def item-count 1680)

(defn recommend-movies
  "Output format is:
   ({:item_id 1642, :name \"Some Mother's Son  (1996)\", :r 3.506496566645136}
    ... "
  [uid]
  (let [item-id-set (set (mapv :item_id (db/get-movie-rating-by-id {:id uid})))
        raw-recommend-list (map #(zipmap [:item_id :name :r] %) (spk/recommend uid item-count))]
    (remove
     #(item-id-set (:item_id %))
     raw-recommend-list)))

;; user-submit-form use html function from hiccup
(defn user-submit-form [iid]
  (html [:form {:action "/submit_rating" :method "post" :style "display:inline!important;"}
         (for [x (range 1 6)]
           [:label {:class "radio-inline"}
            [:input {:type "radio" :name "optradio" :value (str x)}
             x]])
         [:input {:type "hidden" :name "itemid" :value (str iid)}]
         [:input {:type "submit" :value "Submit"}]]))

(defn guest-data-page [{{start "start" length "length" draw "draw"}
                        :form-params :as req}]
  (let [s (Integer/parseInt start)
        len (Integer/parseInt length)
        d (Integer/parseInt draw)
        data (db/get-movie-average)
        page (mapv #(vector (:item_id %) (:name %) (:r %))
                   (drop s (take (+ s len) data)))]
    (generate-string
     {:draw d
      :recordsTotal (count data)
      :recordsFiltered (count data)
      :data page})))

;; server-side processing function
(defn data-page [{{uid "uid" start "start" length "length" draw "draw"}
                  :form-params :as req}]
  (log/info "uid: " uid "start: " start "length: " length "type of uid: " (class uid))
  (let [u (Integer/parseInt uid)
        s (Integer/parseInt start)
        len (Integer/parseInt length)
        d (Integer/parseInt draw)
        data (recommend-movies u)
        page  (mapv #(vector (:item_id %) (:name %)  (:r %)  (user-submit-form  (:item_id %)))
                    (drop s (take (+ s len) data)))]
    (log/info (take 2 data))
    (generate-string
     {:draw d
      :recordsTotal (count data)
      :recordsFiltered (count data)
      :data page})))

(defn root-page [{{user-sess-id :identity} :session :as req}]
  (if (nil? user-sess-id)
    (layout/render "root.html"
                   {:h2 "Hello Guest"})
    (let [{uid :id E :email :as user} (db/get-user-by-sess {:sess user-sess-id})]
      (layout/render "user.html"
                     {:h2 E :uid uid}))))

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
  (POST "/data-page" [] data-page)
  (POST "/submit_rating" [] post-rating))

(defroutes guest-data-routes
  (POST "/guest-data-page" [] guest-data-page))

(defroutes home-routes
  (GET "/" [] root-page)
  (GET "/home" [] (home-page))
  (GET "/login" [] (get-login))
  (POST "/login" [] post-login)
  (GET "/accept" [] (get-accept))
  (GET "/reject" [] (get-reject))
  (GET "/register" [] (get-register))
  (POST "/register" [] post-register))
