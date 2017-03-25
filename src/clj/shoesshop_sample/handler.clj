(ns shoesshop-sample.handler
  (:require [compojure.core :refer [routes wrap-routes]]
            [shoesshop-sample.layout :refer [error-page]]
            [shoesshop-sample.routes.home :refer [home-routes]]
            [shoesshop-sample.routes.services :refer [service-routes]]
            [compojure.route :as route]
            [shoesshop-sample.env :refer [defaults]]
            [mount.core :as mount]
            [shoesshop-sample.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(def app-routes
  (routes
    (-> #'home-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    #'service-routes
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
