(ns user
  (:require [luminus-migrations.core :as migrations]
            [intowow.config :refer [env]]
            [mount.core :as mount]
            intowow.core))

(defn start []
  (mount/start-without #'intowow.core/repl-server))

(defn stop []
  (mount/stop-except #'intowow.core/repl-server))

(defn restart []
  (stop)
  (start))

(defn migrate []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(defn rollback []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))


