(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as cljdropbox]
            [clojure.java.io :as io]))

(spit "test.txt", "test")

(slurp "test.txt")

(deftest access-token-test
  (testing "get-access-token"
    (is (not (= (cljdropbox/get-access-token) "FileNotFoundException")))))

(deftest get-file-counts
  (is (= (cljdropbox/get-file-counts (cljdropbox/get-access-token)
                                     {:path ""}) 18)))
