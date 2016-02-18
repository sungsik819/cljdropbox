(ns cljdropbox.core
  (:require [cheshire.core :as json]                       
            [clj-http.client :as httpclient]))

(def dropbox-url "https://api.dropboxapi.com/2")

;access-token 얻어오기
(def access-token (:access_token (load-file "./info.env")))  

(defn parse-oauth2 [method url params & path]
  (json/parse-string (:body (method url (merge params
                                               {:headers {"Authorization" (format "Bearer %s" access-token)}})))
  true))
  
(defn dropbox-usage []
  (:used (parse-oauth2 httpclient/post access-token (str dropbox-url "/users/get_space_usage") {})))

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

(defn dropbox-list-folder [params]
  (parse-oauth2 httpclient/post access-token (str dropbox-url "/files/list_folder") {:content-type :json :form-params params}))

;(defn dropbox-list-folder-continue [cursor]
;  (parse-oauth2 httpclient/post access-token (str dropbox-url "/files/list_folder/continue") {:content-type :json :form-params {:cursor (:cursor cursor)}}))

(defn get-file-counts [dropbox-files]
  (fn [params]
    (get-dropbox-files (fn [acc x] (+ acc 1)) (dropbox-files params))))  

;(defn create-folder [path]
;  (parse-oauth2 httpclient/post  access-token "https://api.dropboxapi.com/1/fileops/create_folder" {:query-params {:root "auto" :path path}}))

;(defn delete [path]
;  (parse-oauth2 httpclient/post access-token "https://api.dropboxapi.com/1/fileops/delete" {:query-params {:root "auto" :path path}}))

(defn -main []
  (println (dropbox-usage access-token)))
