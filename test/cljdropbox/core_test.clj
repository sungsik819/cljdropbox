(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as dropbox]))

(def test-access-token (:access-token (load-file "./test-info.env")))

(def tag-file {:.tag "file",})
(def tag-folder {:.tag "folder"})

(defn mock-folder-files-map [file-counts folder-counts]
  {:entries (into (into [] (repeat file-counts tag-file)) (repeat folder-counts tag-folder)),
   :cursor "1234567", :has_more false })

;(dropbox/dropbox-list-folder test-access-token {:path "" :recursive true})

(defn mock-list-folder [file-counts folder-counts]
  (fn [params]
    (mock-folder-files-map file-counts folder-counts)))

(deftest file-counts
  (is (= 1 ((dropbox/get-file-counts (mock-list-folder 1 0)) {:path ""})))
  (is (= 2 ((dropbox/get-file-counts (mock-list-folder 2 0)) {:path ""})))
  (is (= 3 ((dropbox/get-file-counts (mock-list-folder 3 1)) {:path ""}))))

