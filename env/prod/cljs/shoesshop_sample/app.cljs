(ns shoesshop-sample.app
  (:require [shoesshop-sample.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
