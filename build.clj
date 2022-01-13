(ns build
  (:require [clojure.tools.build.api :as b]))

(def basis (b/create-basis nil))
(def lib 'com.cognitect/transit-java)
(def version (str (slurp "VERSION_PREFIX") "." (b/git-count-revs nil)))
(def class-dir "target/classes")

(defn jar
  [_]
  (b/delete {:path "target"})
  (b/javac {:src-dirs ["src/main/java"] :class-dir class-dir :basis basis})
  (b/jar {:class-dir class-dir :jar-file (format "target/tools.build-%s.jar" version)}))
