(ns cljdropbox.core-test
  (:require [clojure.test :refer :all]
            [cljdropbox.core :as dropbox]))

(def access-token (:access-token (load-file "./test-info.env")))
(reset! dropbox/access-token access-token)

(defn searched-data-file [path-display name]
  { :metadata {:path_display path-display, :name name, :.tag "file"}})

(defn mock-search [path query]
  searched-files)

;(mock-search "" "*.txt")
(def searched-files {:matches
                     [(searched-data-file "/abcd.txt" "abcd.txt")
                      (searched-data-file "/abcd2.txt" "adbcd2.txt")
                      ],
                     :more false,
                     :start 2
                     })

(dropbox/get-path-display (mock-search "" "*.txt"))

;(dropbox/get-path-display (dropbox/search "" "*.txt"))
(def tag-file {:.tag "file",})
(def tag-folder {:.tag "folder"})

(defn mock-data [file-counts folder-counts has-more]
      {:entries (into (into [] (repeat file-counts tag-file)) (repeat folder-counts tag-folder)),
       :cursor "1234567", :has_more has-more })

(defn mock-datas [counts]
  (conj (into [] (repeat counts (mock-data 1 1 true))) (mock-data 1 1 false)))
  
(def mock-4files [(mock-data 2 1 true) (mock-data 1 1 true) (mock-data 1 1 false)])
(def mock-8files [(mock-data 2 1 true) (mock-data 5 1 true) (mock-data 1 1 false)])

(defn mock-get-data-fn [func]
  (fn [datas]
    (func datas)))

(def mock-get-data (mock-get-data-fn first))
(def mock-list-folder-continue (mock-get-data-fn rest))

;(dropbox/dropbox-list-folder test-access-token {:path "" :recursive true})

(deftest file-counts
  (is (= 1864 (dropbox/get-file-counts (mock-data 1864 136 false))))
  (is (= 2 ((dropbox/get-all-file-counts mock-get-data mock-list-folder-continue) (mock-datas 1))))
  (is (= 3 ((dropbox/get-all-file-counts mock-get-data mock-list-folder-continue) (mock-datas 2))))
  (is (= 4 ((dropbox/get-all-file-counts mock-get-data mock-list-folder-continue) mock-4files)))
  (is (= 8 ((dropbox/get-all-file-counts mock-get-data mock-list-folder-continue) mock-8files))))
