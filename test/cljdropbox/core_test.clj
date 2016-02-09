(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :refer :all]))

(deftest core-test
  (is (= (:path (create-folder (get-access-token) "tmpcreate")) "/tmpcreate"))
  (is (= (:path (delete (get-access-token) "tmpcreate")) "/tmpcreate")))
