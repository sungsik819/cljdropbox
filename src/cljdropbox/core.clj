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


(json/parse-string (:body (oauth2/get "https://api.dropbox.com/1/account/info" {:oauth2 {:access-token (get-access-token) :token-type "bearer"}})) true)

(apply oauth2/get ["https://api.dropbox.com/1/account/info" (merge {} {:oauth2 {:access-token (get-access-token) :token-type "bearer"}})])
