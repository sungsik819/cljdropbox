(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :refer :all]))

(deftest core-test
  (is (= (file-counts (get-access-token) "") 17))
  (is (= (folder-file-size (get-access-token) "") 34126843)))
