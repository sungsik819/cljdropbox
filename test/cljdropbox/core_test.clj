(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as cljdropbox]))

(spit "test.txt", "test")

(slurp "test.txt")

(defn create-folder-file-fixture [f]
  (cljdropbox/create-folder (cljdropbox/get-access-token) "tmpfolder")
  ; 파일 생성 및 업로드 추가 
  (f)
  (cljdropbox/delete (cljdropbox/get-access-token) "tmpfolder"))

(use-fixtures :once create-folder-file-fixture)

(deftest get-file-counts
  (is (= (cljdropbox/get-file-counts (cljdropbox/get-access-token)
                                     {:path "/tmpfolder"}) 0)))

(deftest access-token-test
  (testing "get-access-token"
    (is (not (= (cljdropbox/get-access-token) "FileNotFoundException")))))


