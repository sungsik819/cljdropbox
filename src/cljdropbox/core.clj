(ns cljdropbox.core
  (:require [cheshire.core :as json]                       
            [clj-http.client :as httpclient]))

(def dropbox-url "https://api.dropboxapi.com/2")

;access-token 얻어오기
(def access-token (:access_token (load-file "./info.env")))  

(defn parse-oauth2 [method url params]
  (json/parse-string (:body (method url (merge params
                                               {:headers {"Authorization" (format "Bearer %s" access-token)}})))
  true))
  
(defn dropbox-usage []
  (:used (parse-oauth2 httpclient/post (str dropbox-url "/users/get_space_usage") {})))

(defn get-dropbox-files [func files]
  (reduce func 0 (filter (fn [x] (= (:.tag x) "file")) (:entries files))))

(defn dropbox-list-folder [params]
  (parse-oauth2 httpclient/post (str dropbox-url "/files/list_folder") {:content-type :json :form-params params}))

;(defn dropbox-list-folder-continue [data]
;  (parse-oauth2 httpclient/post access-token (str dropbox-url "/files/list_folder/continue") {:content-type :json :form-params {:cursor (:cursor data)}}))

(defn get-file-counts [dropbox-files]
  (get-dropbox-files (fn [acc x] (+ acc 1)) dropbox-files))


(defn get-all-file-counts [list-folder-countinue]
  (fn recursive [dropbox-datas]
    (+ (get-file-counts (first dropbox-datas))
       (if (:has_more (first dropbox-datas)) (recursive (list-folder-countinue dropbox-datas))
           0))))
  

(defn -main []
  (println (dropbox-usage access-token)))
