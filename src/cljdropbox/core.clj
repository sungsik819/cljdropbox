(ns cljdropbox.core
  (:require [clj-dropbox-oauth2.dropbox :as auth2dropbox]
            [clj-oauth2.client :as oauth2]
            [cheshire.core :as json]
            [clj-http.client :as httpclient]))

; access-token 얻어오기
(defn get-access-token []
  (:access_token (load-file "./info.env")))

; 현재 폴더의 파일 갯수
(defn file-counts [access-token path]
  (count (filter (fn [x] (false? (:is_dir x)))
                 (:contents (auth2dropbox/metadata access-token path)))))

; 현재 폴더의 파일 전체 크기
(defn folder-file-size [access-token path]
  (reduce (fn [acc x] (+ acc (:bytes x))) 0
          (:contents (auth2dropbox/metadata access-token ""))))

(auth2dropbox/metadata (get-access-token) "")


(defn parse-oauth2 [method access-token url params]
  (json/parse-string
   (:body (method url (merge params 
                      {:oauth2 {:access-token access-token :token-type "bearer"}})))
   true))

(defn dropbox-usage [access-token]
  (:used (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/users/get_space_usage" {})))

(parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/create_folder" {:content-type :json :form-params {:path "/aaaa"}})

(def dropbox-contents (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder" {:content-type :json :form-params {:path "" :recursive true :include_media_info true}}))

(def dropbox-continue-contents (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor dropbox-contents)}}))

(def dropbox-continue-contents2 (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor dropbox-continue-contents)}}))

(def dropbox-continue-contents3 (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor dropbox-continue-contents2)}}))

(def dropbox-continue-contents4 (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor dropbox-continue-contents3)}}))

(def dropbox-continue-contents5 (parse-oauth2 oauth2/post (get-access-token) "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor dropbox-continue-contents4)}}))

(:has_more dropbox-contents)
(:has_more dropbox-continue-contents5)

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

(count (:entries dropbox-contents))

(get-dropbox-files (fn [acc x] (+ acc (:size x))) dropbox-contents)
(get-dropbox-files (fn [acc x] (+ acc 1)) dropbox-contents)

(defn dropbox-list-folder [access-token params]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/files/list_folder" {:content-type :json :form-params params}))

(dropbox-list-folder (get-access-token) {:path "" :recursive true :include_media_info true})
  
(defn dropbox-list-folder-recursive [access-token has-more cursor]
  (if (true? has-more) 
(defn dropbox-list-folder-continue [access-token cursor]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/2/files/list_folder/continue" {:content-type :json :form-params {:cursor (:cursor cursor)}}))
  
(defn dropbox-file-total [func]
  (+ (get-dropbox-files func dropbox-contents)
   (get-dropbox-files func dropbox-continue-contents)
   (get-dropbox-files func dropbox-continue-contents2)
   (get-dropbox-files func dropbox-continue-contents3)
   (get-dropbox-files func dropbox-continue-contents4)
   (get-dropbox-files func dropbox-continue-contents5)))

(dropbox-file-total (fn [acc x] (+ acc (:size x))))

(defn create-folder [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/create_folder" {:query-params {:root "auto" :path path}}))

(defn delete [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/delete" {:query-params {:root "auto" :path path}}))

(apply oauth2/get ["https://api.dropbox.com/1/account/info" (merge {} {:oauth2 {:access-token (get-access-token) :token-type "bearer"}})])

(json/parse-string (:body (httpclient/post "https://api.dropboxapi.com/2/users/get_space_usage" {:headers {"Authorization" (format "Bearer %s" (get-access-token))}})))

(json/parse-string (:body (httpclient/post "https://api.dropboxapi.com/2/files/create_folder"
                                           {:headers {"Authorization" (format "Bearer %s" (get-access-token))} :content-type :json :form-params { :path "/aaaa"}})))

