(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as cljdropbox]))

(def file-counts (atom 0))

(defn create-folder-file-fixture [f]
  (cljdropbox/create-folder (cljdropbox/get-access-token) "tmpfolder")
  (cljdropbox/upload-file (cljdropbox/get-access-token) "/test.txt" "/tmpfolder/test.txt")
  (swap! file-counts inc)
  (f)
  (cljdropbox/delete (cljdropbox/get-access-token) "tmpfolder"))

(use-fixtures :once create-folder-file-fixture)

(deftest get-file-counts
  (is (= (cljdropbox/get-file-counts (cljdropbox/get-access-token)
                                     {:path "/tmpfolder"}) @file-counts)))

(deftest access-token-test
  (testing "get-access-token"
    (is (not (= (cljdropbox/get-access-token) "FileNotFoundException")))))


