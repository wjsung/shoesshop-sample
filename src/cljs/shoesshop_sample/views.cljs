(ns shoesshop-sample.views
  (:require [reagent.core  :refer [atom]  :as r]
            [reagent.session :as session]
            [re-frame.core :refer [subscribe dispatch] :as rf]
            [markdown.core :refer [md->html]]
            [shoesshop-sample.util.slurp :include-macros true :refer [slurp]]
            [shoesshop-sample.validation :refer [registration-errors]]
            [ajax.core :as ajax]
            [goog.crypt.base64 :as b64]
            [clojure.string :as string]

            ))
;; test pages
(defn docs-page []
  [:div.container
   (when-let [docs @(rf/subscribe [:docs])]
     [:div.row>div.col-sm-12
      [:div {:dangerouslySetInnerHTML
             {:__html (md->html docs)}}]])])


(defn docslup-page []
  [:div.container
   [:div.row>div.col-sm-12
    [:div {:dangerouslySetInnerHTML
           {:__html (md->html (slurp "resources/docs/docs.md" ) )}
           }
     ]]]
  )
;;about page
(defn about-page []
  [:div.container

   [:div.row
    "This project is a sample of shoes-shop."
    ]

   [:div.row
    "   &   "
    ]
   [:div.row
    [:div.col-md-12
     [:img {:src (str js/context "/img/warning_clojure.png")}]]]])

;;Home page Common
(defn modal [header body footer]
  [:div
   [:div.modal-dialog
    [:div.modal-content
     [:div.modal-header [:h3 header]]
     [:div.modal-body body]
     [:div.modal-footer
      [:div.bootstrap-dialog-footer
       footer]]]]
   [:div.modal-backdrop.fade.in]])

(defn error-modal []
  (when-let [error (session/get :error)]
    [modal
     (:message error)
     [:div.container-fluid
      [:div.form-group
       [:div.alert.alert-danger (:cause error)]]]
     [:div.form-group
      [:button.btn.btn-danger.btn-lg.btn-block
       {:type     "submit"
        :on-click #(session/remove! :error)}
       "OK"]]]))


;;ID/PW Components
(defn input [type id placeholder fields]
  [:input.form-control.input-lg
   {:type        type
    :placeholder placeholder
    :value       (id @fields)
    :on-change   #(swap! fields assoc id (-> % .-target .-value))}])

(defn form-input [type label id placeholder fields optional?]
  [:div.form-group
   [:label label]
   (if optional?
     [input type id placeholder fields]
     [:div.input-group
      [input type id placeholder fields]
      [:span.input-group-addon
       "✱"]])])

(defn text-input [label id placeholder fields & [optional?]]
  (form-input :text label id placeholder fields optional?))

(defn password-input [label id placeholder fields & [optional?]]
  (form-input :password label id placeholder fields optional?))

;;Register modal

(defn register! [fields errors]
  (reset! errors (registration-errors @fields))
  (when-not @errors
    (ajax/POST "/register"
               {:params @fields
                :handler
                        #(do
                           (session/put! :identity (:id @fields))
                           (reset! fields {})
                           (session/remove! :modal))
                :error-handler
                        #(reset!
                           errors
                           {:server-error (get-in % [:response :message])})})))
(defn registration-form []
  (let [fields (atom {})
        error  (atom nil)]
    (fn []
      [modal
       [:div "Shoes shop Registration"]
       [:div
        [:div.well.well-sm
         [:strong "✱ required field"]]
        [text-input "name" :id "enter a user name" fields]
        (when-let [error (first (:id @error))]
          [:div.alert.alert-danger error])
        [password-input "password" :pass "enter a password" fields]
        (when-let [error (first (:pass @error))]
          [:div.alert.alert-danger error])
        [password-input "password" :pass-confirm "re-enter the password" fields]
        (when-let [error (:server-error @error)]
          [:div.alert.alert-danger error])]
       [:div
        [:button.btn.btn-primary
         {:on-click #(register! fields error)}
         "Register"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn registration-button []
  [:a.btn
   {:on-click #(session/put! :modal registration-form)}
   "Register"])

;;Login
(def timeout-ms (* 1000 60 30))

(defn session-timer []
  (when (session/get :identity)
    (if (session/get :user-event)
      (do
        (session/remove! :user-event)
        (js/setTimeout #(session-timer) timeout-ms))
      (session/remove! :identity))))

(defn encode-auth [user pass]
  (->> (str user ":" pass) (b64/encodeString) (str "Basic ")))

(defn login! [fields error]
  (let [{:keys [id pass]} @fields]
    (reset! error nil)
    (ajax/POST "/login"
               {:headers       {"Authorization"
                                (encode-auth (when id (string/trim id)) pass)}
                :handler       #(do
                                  (session/remove! :modal)
                                  (session/put! :identity id)
                                  (js/setTimeout session-timer timeout-ms)
                                  (reset! fields nil))
                :error-handler #(reset! error (get-in % [:response :message]))})))

(defn login-form []
  (let [fields (atom {})
        error (atom nil)]
    (fn []
      [modal
       [:div "Shoes Shop Login"]
       [:div
        [:div.well.well-sm
         [:strong "✱ required field"]]
        [text-input "name" :id "enter a user name" fields]
        [password-input "password" :pass "enter a password" fields]
        (when-let [error @error]
          [:div.alert.alert-danger error])]
       [:div
        [:button.btn.btn-primary
         {:on-click #(login! fields error)}
         "Login"]
        [:button.btn.btn-danger
         {:on-click #(session/remove! :modal)}
         "Cancel"]]])))

(defn login-button []
  [:a.btn
   {:on-click #(session/put! :modal login-form)}
   "Login"])


(defn upload-button []
  [:a.dropdown-item.btn
   {:on-click #(session/put! :modal login-form)}
   "Upload"])

;;Home page
(defn home-page []
  [:div.container
   "Home Page"
   ])





