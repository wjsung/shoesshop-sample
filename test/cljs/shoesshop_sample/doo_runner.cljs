(ns shoesshop-sample.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [shoesshop-sample.core-test]))

(doo-tests 'shoesshop-sample.core-test)

