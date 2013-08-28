(ns leiningen.suck-deps
  (:require
    [clojure.java.io :as io]
    [clojure.java.shell :as shell]
    [leiningen.core.classpath :as cp]
    [leiningen.core.main :as main]))

(defn- ln
  "Creates a symbolic link from `target` to an existing file `source`. Equivalent
  to shell command `ln -s source target`. Return `true` on success and `false` on
  failure."
  [^java.io.File source ^java.io.File target]
  ;; NB: Could be done with java.nio.file.Files/createSymbolicLink on Java 7.
  (-> (shell/sh "ln" "-s" (.getPath source) (.getPath target)) :exit zero?))

(defn- cp
  "Copies the contents of the file `source` into the file `target`. If the file
  `target` did not exist then it is created. Equivalent to shell commad `cp
  source target`."
  [^java.io.File source ^java.io.File target]
  ;; NB: io/copy-file could possibly be used instead.
  (-> (shell/sh "cp" (.getPath source) (.getPath target)) :exit zero?))

(defn- get-linkings
  [project target-dir]
  (let [abs-target-dir (.getAbsoluteFile target-dir)]
    (letfn [(in-target-dir [f]
              (io/file abs-target-dir (.getName f)))]
      (for [dependency (cp/resolve-dependencies :dependencies project)]
        (let [abs-dependency (.getAbsoluteFile dependency)]
          [abs-dependency (in-target-dir dependency)])))))

(defn- execute-linkings
  [linkings method]
  (let [[cmd phrase] ({:link [ln "Linking"]
                       :copy [cp "Copying"]
                       :dry-run [(fn [_ _] true) "Pretending to link"]} method)]
    (doseq [[source target] linkings]
      (when-not (.. target getAbsoluteFile getParentFile isDirectory)
        (main/abort "Parent directory missing:" (.getPath target)))
      (main/info phrase (.getName source) "to" (.getPath target))
      (when-not (cmd source target)
        (main/abort "Failed to process" (.getName source))))))

(defn- resolve-method
  [method-str]
  (if-let [method (keyword method-str)]
    (or (#{:link :copy :dry-run} method)
        (main/abort "Unknown method:" method-str))
    :link))

(defn suck-deps
  "Link (or copy) project dependencies to a given directory.

  Links or copies all project dependencies under the given target directory.
  The target directory must exist. To select the method pass either `link`,
  `copy`, or `dry-run` through the optional argument `method`. By default
  linking is assumed."
  [project target-dir & [method]]
  (execute-linkings (get-linkings project (io/file target-dir))
                    (resolve-method method)))
