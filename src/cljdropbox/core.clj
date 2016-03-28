(ns cljdropbox.core
  (:require [cheshire.core :as json]                       
            [clj-http.client :as httpclient]))

(def dropbox-api-url "https://api.dropboxapi.com/2")
(def dropbox-content-url "https://content.dropboxapi.com/2")

;access-token 얻어오기
(def access-token (atom (:access_token (load-file "./info.env"))))

(defn dropbox-api-call [method url params]
  (method url (merge-with merge params {:headers {"Authorization" (format "Bearer %s" @access-token)}})))

(defn parse-oauth2 [method url params]
  (json/parse-string (:body (dropbox-api-call method url params))
                     true))

(defn dropbox-usage []
  (:used (parse-oauth2 httpclient/post (str dropbox-api-url "/users/get_space_usage") {})))

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

(defn dropbox-list-folder [params]
  (parse-oauth2 httpclient/post (str dropbox-api-url "/files/list_folder") {:content-type :json :form-params params}))

(defn dropbox-list-folder-continue [data]
  (parse-oauth2 httpclient/post (str dropbox-api-url "/files/list_folder/continue") {:content-type :json :form-params {:cursor (:cursor data)}}))


(defn get-file-counts [dropbox-files]
  (get-dropbox-files (fn [acc x] (+ acc 1)) dropbox-files))

(defn search-data [path query]
  (parse-oauth2 httpclient/post (str dropbox-api-url "/files/search") {:content-type :json :form-params {:path path :query query}}))

(defn get-all-file-counts [get-data list-folder-countinue]
  (fn recursive [dropbox-datas]
    (+ (get-file-counts (get-data dropbox-datas))
       (if (:has_more (get-data dropbox-datas)) (recursive (list-folder-countinue dropbox-datas))
           0))))

(defn display-searched [searched-data]
  (dorun (map #(println %) searched-data)))

(defn get-path-display [searched-data]
  (display-searched (map (fn [x] (:path_display (:metadata x))) (:matches searched-data))))

(defn search [path query]
  (get-path-display (search-data path query)))

(defn download [path local-path]
  (let [content (:body (dropbox-api-call httpclient/post
                                         (str dropbox-content-url "/files/download")
                                         {:headers {"Dropbox-API-Arg" (format "{\"path\": \"%s\"}" path)}}))]
    (clojure.java.io/make-parents local-path)
    (spit local-path content)))

(defn -main []
  ;(println "usage: " (dropbox-usage))
  ;(def dropbox-data (dropbox-list-folder {:path "" :recursive true}))
  ;(def all-file-counts ((get-all-file-counts (fn [x] x) dropbox-list-folder-continue) dropbox-data))
                                        ;(println "all-file-counts : " all-file-counts)
  (in-ns 'cljdropbox.core)
  (while true
    (print "=> ")
    (flush)
    (let [command (read-line)]
      (eval (read-string command)))))
