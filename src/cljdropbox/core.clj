(ns cljdropbox.core
  (:require [clj-oauth2.client :as oauth2]
            [cheshire.core :as json]
            [clj-http.client :as httpclient]))

; access-token 얻어오기
(defn get-access-token []
  (:access_token (load-file "./info.env")))

(defn parse-oauth2 [method access-token url params]
  (json/parse-string
   (:body (method url (merge params 
                      {:oauth2 {:access-token access-token :token-type "bearer"}})))
   true))

(defn dropbox-usage [access-token]
  (:used (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/users/get_space_usage" {})))

;(parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/create_folder" {:content-type :json :form-params {:path "/aaaa"}})

;(:has_more dropbox-contents)
;(:has_more dropbox-continue-contents5)

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

;(count (:entries dropbox-contents))

;(get-dropbox-files (fn [acc x] (+ acc (:size x))) dropbox-contents)
;(get-dropbox-files (fn [acc x] (+ acc 1)) dropbox-contents)

(defn dropbox-list-folder [access-token params]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/files/list_folder" {:content-type :json :form-params params}))

;(dropbox-list-folder (get-access-token) {:path "" :recursive true :include_media_info true})
   
(defn dropbox-list-folder-continue [access-token cursor]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor cursor)}}))

;(dropbox-list-folder-continue (get-access-token) (:cursor (dropbox-list-folder (get-access-token) {:path "" :recursive true :include_media_info true})))
;(dropbox-file-total (fn [acc x] (+ acc (:size x))))

(defn create-folder [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/create_folder" {:query-params {:root "auto" :path path}}))

(defn delete [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/delete" {:query-params {:root "auto" :path path}}))

;(apply oauth2/get ["https://api.dropbox.com/1/account/info" (merge {} {:oauth2 {:access-token (get-access-token) :token-type "bearer"}})])

;(json/parse-string (:body (httpclient/post "https://api.dropboxapi.com/2/users/get_space_usage" {:headers {"Authorization" (format "Bearer %s" (get-access-token))}})))

;(json/parse-string (:body (httpclient/post "https://api.dropboxapi.com/2/files/create_folder" {:headers {"Authorization" (format "Bearer %s" (get-access-token))} :content-type :json :form-params { :path "/aaaa"}})))

(defn -main []
  (println (dropbox-usage (get-access-token))))
