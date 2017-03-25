(ns shoesshop-sample.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [shoesshop-sample.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[shoesshop-sample started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[shoesshop-sample has shut down successfully]=-"))
   :middleware wrap-dev})
