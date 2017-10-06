(ns intowow.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [intowow.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[intowow started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[intowow has shut down successfully]=-"))
   :middleware wrap-dev})
