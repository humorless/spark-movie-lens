(ns intowow.routes.home
  (:require [intowow.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :as response]
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

(defroutes home-routes
  (GET "/" [] (root-page))
  (GET "/home" [] (home-page))
  (GET "/about" [] (about-page)))
