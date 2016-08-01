(ns chan-fp.core
  (:require [clojure.core.async :refer [>! <!! alt!! chan go go-loop]])
  (:import clojure.lang.IDeref))

;;; Future

(defrecord Comp [value ok]
  IDeref
  (deref [_]
    value))

(defn future [f]
  (let [fut (chan)]
    (go-loop [comp (f)]
      (>! fut comp)
      (recur comp))
    fut))

(defn get [fut]
  (<!! fut))

;;; Combinators

(defn on-success [fut callback]
  (go
    (let [comp (get fut)]
      (if (:ok comp)
        (callback (:value comp))))))

(defn on-failure [fut callback]
  (go
    (let [comp (get fut)]
      (if (not (:ok comp))
        (callback)))))

(defn or-else [fut-a fut-b]
  (future #(let [comp (get fut-a)]
            (if (:ok comp)
              comp
              (get fut-b)))))

(defn when [fut guard]
  (future #(let [comp (get fut)
                 value (:value comp)]
            (Comp. value (and (:ok comp) (guard value))))))

(defn then [fut f]
  (future #(let [comp (get fut)]
            (if (:ok comp)
              (f (:value comp))
              comp))))

;; FIXME Use alt!
(defn any [fut-a fut-b]
  (future #(letfn [(on-select [comp fut]
                     (if (:ok comp)
                       comp
                       (get fut)))]
            (alt!!
              fut-a ([comp] (on-select comp fut-b))
              fut-b ([comp] (on-select comp fut-a))))))