(ns cljdropbox.core
  (:require [clj-dropbox-oauth2.dropbox :as auth2dropbox]))

(def access-token (:access_token (load-file "./info.env")))

; 계정 정보 얻어 오기
(auth2dropbox/account-info access-token)

; 현재 폴더의 파일 전체 크기
(reduce (fn [acc x] (+ acc (:bytes x))) 0 (:contents (auth2dropbox/metadata access-token ""))) 

; 현재 폴더의 파일 갯수
(count (filter (fn [x] (false? (:is_dir x))) (:contents (auth2dropbox/metadata access-token ""))))
