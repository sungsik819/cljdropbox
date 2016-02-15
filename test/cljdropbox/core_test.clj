(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as cljdropbox]))

(def file-counts (atom 0))

(defn upload-file [local-path remote-path]
  (cljdropbox/upload-file (cljdropbox/get-access-token) local-path remote-path)
  (swap! file-counts inc))

(defn delete [path]
  (cljdropbox/delete (cljdropbox/get-access-token) path)
  (reset! file-counts 0))
  
(defn create-folder-file-fixture [f]
  (cljdropbox/create-folder (cljdropbox/get-access-token) "tmpfolder")
  (upload-file "/test.txt" "/tmpfolder/test.txt") 
  (f)
  (delete "tmpfolder"))

(use-fixtures :once create-folder-file-fixture)

(deftest get-file-counts
  (is (= (cljdropbox/get-file-counts (cljdropbox/get-access-token)
                                     {:path "/tmpfolder"}) @file-counts)))

(deftest access-token-test
  (testing "get-access-token"
    (is (not (= (cljdropbox/get-access-token) "FileNotFoundException")))))


