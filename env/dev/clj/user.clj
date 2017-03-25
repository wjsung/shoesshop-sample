(ns user
  (:require [mount.core :as mount]
            [shoesshop-sample.figwheel :refer [start-fw stop-fw cljs]]
            shoesshop-sample.core))

(defn start []
  (mount/start-without #'shoesshop-sample.core/http-server
                       #'shoesshop-sample.core/repl-server))

(defn stop []
  (mount/stop-except #'shoesshop-sample.core/http-server
                     #'shoesshop-sample.core/repl-server))

(defn restart []
  (stop)
  (start))


