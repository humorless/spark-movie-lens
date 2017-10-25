(ns intowow.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [intowow.layout :refer [error-page]]
            [intowow.routes.home :refer [home-routes data-routes guest-data-routes submit-routes]]
            [compojure.route :as route]
            [intowow.env :refer [defaults]]
            [mount.core :as mount]
            [intowow.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
   (-> #'guest-data-routes
       (middleware/wrap-formats))
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'data-routes
       (middleware/wrap-restricted)
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'submit-routes
       (middleware/wrap-restricted)
       (middleware/wrap-formats))
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
