(ns cljdropbox.core
  (:require [cheshire.core :as json]                       
            [clj-http.client :as httpclient]))

;access-token 얻어오기
(defn get-access-token []
  (try
    (:access_token (load-file "./info.env"))
    (catch java.io.FileNotFoundException e
      "FileNotFoundException")))
  

(defn parse-oauth2 [method access-token url params]
  (json/parse-string
   (:body (method url (merge params
                      {:headers {"Authorization" (format "Bearer %s" access-token)}})))
   true))

(defn parse-upload-oauth2 [method access-token url path params]
  (json/parse-string
   (:body (method url (merge params
                             {:headers {"Authorization" (format "Bearer %s" access-token)
                                        "Dropbox-API-Arg" (format "{\"path\": \"%s\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}" path)}})))
   true))
  
  
(defn dropbox-usage [access-token]
  (:used (parse-oauth2 httpclient/post access-token "https://api.dropboxapi.com/2/users/get_space_usage" {})))

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

(defn dropbox-list-folder [access-token params]
  (parse-oauth2 httpclient/post access-token "https://api.dropboxapi.com/2/files/list_folder" {:content-type :json :form-params params}))

(defn upload-file [access-token local-file remote-path]
  (parse-upload-oauth2 httpclient/post access-token
                       "https://content.dropboxapi.com/2/files/upload" remote-path  
                 {:content-type "application/octet-stream" :form-params {:data-binary local-file}}))
                                                                            
(defn dropbox-list-folder-continue [access-token cursor]
  (parse-oauth2 httpclient/post access-token "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor cursor)}}))

(defn get-file-counts [access-token params]
  (get-dropbox-files (fn [acc x] (+ acc 1)) (dropbox-list-folder access-token params)))

(defn create-folder [access-token path]
  (parse-oauth2 httpclient/post  access-token "https://api.dropboxapi.com/1/fileops/create_folder" {:query-params {:root "auto" :path path}}))

(defn delete [access-token path]
  (parse-oauth2 httpclient/post access-token "https://api.dropboxapi.com/1/fileops/delete" {:query-params {:root "auto" :path path}}))

(defn -main []
  (println (dropbox-usage (get-access-token))))
