(ns shoesshop-sample.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[shoesshop-sample started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[shoesshop-sample has shut down successfully]=-"))
   :middleware identity})
