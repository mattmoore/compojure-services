(ns services.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response :refer [response]]
            [clojure.data.json :as json]
            [clojure.java.io :as io]))

(defn data-file [file]
  (->> (io/resource file)
       (slurp)))

(def people
  #(as-> (data-file "people.json") data
         (json/read-str data :key-fn keyword)))

(defn find-person [id]
  (as-> (Integer/parseInt id) v
        (filter #(= v (% :id)) (people))
        (first v)))

(defroutes app-routes
  (GET "/people/:id" [id]
       (->> (find-person id)
            (response)))
  (GET "/people" []
       (->> (people)
            (response)))
  (route/not-found "Not Found"))

(def app
  (wrap-json-response app-routes site-defaults))
