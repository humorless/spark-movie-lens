(ns intowow.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [intowow.layout :refer [error-page]]
            [intowow.routes.home :refer [home-routes data-routes]]
            [compojure.route :as route]
            [intowow.env :refer [defaults]]
            [mount.core :as mount]
            [intowow.middleware :as middleware]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
   (-> #'home-routes
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (-> #'data-routes
       (middleware/wrap-restricted)
       (wrap-routes middleware/wrap-csrf)
       (wrap-routes middleware/wrap-formats))
   (route/not-found
    (:body
     (error-page {:status 404
                  :title "page not found"})))))

(defn app [] (middleware/wrap-base #'app-routes))
