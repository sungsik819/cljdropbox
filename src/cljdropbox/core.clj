(ns cljdropbox.core
  (:require [clj-dropbox-oauth2.dropbox :as auth2dropbox]
            [clj-oauth2.client :as oauth2]
            [cheshire.core :as json]))

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

(defn create-folder [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/create_folder" {:query-params {:root "auto" :path path}}))

(defn delete [access-token path]
  (parse-oauth2 oauth2/post access-token "https://api.dropboxapi.com/1/fileops/delete" {:query-params {:root "auto" :path path}}))

(apply oauth2/get ["https://api.dropbox.com/1/account/info" (merge {} {:oauth2 {:access-token (get-access-token) :token-type "bearer"}})])
