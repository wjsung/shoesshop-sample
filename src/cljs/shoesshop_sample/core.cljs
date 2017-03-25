(ns shoesshop-sample.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [re-frame.core :as rf]
            [secretary.core :as secretary]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [ajax.core :refer [GET POST] :as ajax]
            [shoesshop-sample.ajax :refer [load-interceptors!]]
            [shoesshop-sample.handlers]
            [shoesshop-sample.subscriptions]
            [shoesshop-sample.views :as v]
            )
  (:import goog.History))

;; -------------------------
;; nav & pages

(defn nav-link [uri title page collapsed?]
  (let [selected-page (rf/subscribe [:page])]
    [:li.nav-item
     {:class (when (= page @selected-page) "active")}
     [:a.nav-link
      {:href uri
       :on-click #(reset! collapsed? true)} title]]))

(defn user-menu []
  (if-let [id (session/get :identity)]
    [:ul.nav.navbar-nav.pull-xs-right
     [:li.nav-item [v/upload-button]]
     [:li.nav-item
      [:a.dropdown-item.btn
       {:on-click #(ajax/POST
                     "/logout"
                     {:handler (fn [] (session/remove! :identity))})}
       [:i.fa.fa-user] " " id " | sign out"]]]
    [:ul.nav.navbar-nav.pull-xs-right
     [:li.nav-item [v/login-button]]
     [:li.nav-item [v/registration-button]]]))

(defn navbar []
  (r/with-let [collapsed? (r/atom true)]
              [:nav.navbar.navbar-dark.bg-primary
               [:button.navbar-toggler.hidden-sm-up
                {:on-click #(swap! collapsed? not)} "☰"]
               [:div.collapse.navbar-toggleable-xs
                (when-not @collapsed? {:class "in"})
                  [:a.navbar-brand {:href "/"} "shoesshop-sample"]
                  [:ul.nav.navbar-nav
                   [nav-link "#/" "Home" :home collapsed?]
                   [nav-link "#/about" "About" :about collapsed?]
                   ;[nav-link "#/docs" "Docs" :docs collapsed?]
                   ;[nav-link "#/docslup" "Docs Slub" :docslup collapsed?]
                   ]

                [:div.navright
                 [user-menu]
                 ]
                ]

               ]))


(defn modal []
  (when-let [session-modal (session/get :modal)]
    [session-modal]))

(def pages
  {:home #'v/home-page
   :about #'v/about-page
   ;:docs #'v/docs-page
   ;:docslup #'v/docslup-page
   })

(defn page []
  [:div
   [modal]
   [navbar]
   [(pages @(rf/subscribe [:page]))]])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (rf/dispatch [:set-active-page :home]))

(secretary/defroute "/about" []
  (rf/dispatch [:set-active-page :about]))

(secretary/defroute "/docs" []
                    (rf/dispatch [:set-active-page :docs]))

(secretary/defroute "/docslup" []
                    (rf/dispatch [:set-active-page :docslup]))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET "/docs" {:handler #(rf/dispatch [:set-docs %])}))

(defn mount-components []
  (rf/clear-subscription-cache!)
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (rf/dispatch-sync [:initialize-db])
  (load-interceptors!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (session/put! :identity js/identity)
  (mount-components))
