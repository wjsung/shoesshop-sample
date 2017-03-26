(ns shoesshop-sample.routes.services.upload
  (:require [shoesshop-sample.db.core :as db]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]
            [clojure.java [io :as io]]
            )
  (:import [java.awt.image AffineTransformOp BufferedImage]
           [java.io ByteArrayOutputStream FileInputStream File]
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO
           java.net.URLEncoder))

(def uploaddir "./upload/")

(def thumb-size 150)

(def thumb-prefix "thumb_")

(defn scale [img ratio width height]
  (let [scale        (AffineTransform/getScaleInstance
                       (double ratio) (double ratio))

        transform-op (AffineTransformOp.
                       scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file thumb-size]
  (let [img        (ImageIO/read file)
        img-width  (.getWidth img)
        img-height (.getHeight img)
        ratio      (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn image->file [image file]
  (ImageIO/write image "png" (File. file))
    )

(defn url-encode [s]
  (URLEncoder/encode s "UTF-8"))

(defn upload-product! [user {:keys [tempfile filename content-type]} name description price]
  (try
    (let [db-file-name (str user "_" (System/currentTimeMillis) "_" filename)]
      (io/copy tempfile (io/as-file (str uploaddir db-file-name)))
      (image->file (scale-image tempfile thumb-size) (str uploaddir thumb-prefix db-file-name))
      (db/product-upload! {:owner user
                      :name  name
                      :description  description
                      :price  price
                      :imgpath db-file-name    })
    )
  (ok {:result :ok})
  (catch Exception e
    (log/error e)
    (internal-server-error "error"))))
