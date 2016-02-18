(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as dropbox]))

(def test-access-token (:access-token (load-file "./test-info.env")))

(def tag-file {:.tag "file",})
(def tag-folder {:.tag "folder"})

(defn mock-files-map [file-counts folder-counts]
  {:entries (into (into [] (repeat file-counts tag-file)) (repeat folder-counts tag-folder)),
   :cursor "1234567", :has_more false })

;(dropbox/dropbox-list-folder test-access-token {:path "" :recursive true})
(defn mock-list-folder-1files [params]
  (mock-files-map 1 0))

(defn mock-list-folder-2files [params]
  (mock-files-map 2 0))

(defn mock-list-1folder-3files [params]
  (mock-files-map 3 1))

((dropbox/get-file-counts mock-list-folder-1files) {:path ""})

(deftest file-counts
  (is (= 1 ((dropbox/get-file-counts mock-list-folder-1files) {:path ""})))
  (is (= 2 ((dropbox/get-file-counts mock-list-folder-2files) {:path ""})))
  (is (= 3 ((dropbox/get-file-counts mock-list-1folder-3files) {:path ""}))))

