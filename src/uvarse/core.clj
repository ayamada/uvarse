(ns uvarse.core)

;;; ----------------------------------------------------------------

(def uvarses (atom nil))

(def ^:const uvar-infix-str "-+*UVAR01*+-") ; must be safe from var namespace
(def ^:const uvar-infix-re
  (re-pattern (java.util.regex.Pattern/quote uvar-infix-str)))

;;; ----------------------------------------------------------------

(defmacro defuvarse [uvarse-sym]
  (assert (not @uvarses) "must be before (load-all-uvarse!)")
  `(def ~uvarse-sym
     (delay (if-let [u# @uvarses]
              (get u# '~uvarse-sym)
              (throw (ex-info "must be after load-all-uvarse!" {}))))))

(defn- encode-ns-name [ns-name-str]
  (if ns-name-str
    (clojure.string/escape ns-name-str {\- "-0", \. "-1"})
    ""))

(defn- decode-ns-name [encoded-ns-name-str]
  (if (empty? encoded-ns-name-str)
    nil
    (clojure.string/replace encoded-ns-name-str
                            #"\-(.)"
                            (fn [[_ x]]
                              ({"0" "-", "1" "."} x)))))

(defmacro defuvar [uvarse-sym uvar-key uvar-val]
  (assert (not @uvarses) "must be before (load-all-uvarse!)")
  (assert (symbol? uvarse-sym))
  (assert (resolve uvarse-sym) "must be after defuvarse")
  (assert (keyword? uvar-key))
  (let [sym (symbol (str (name uvarse-sym)
                         uvar-infix-str 
                         (encode-ns-name (namespace uvar-key))
                         uvar-infix-str 
                         (name uvar-key)))]
    (assert (not (resolve sym))
            (format "already defined (defuvar %s %s ...)"
                    uvarse-sym uvar-key))
    `(def ~sym ~uvar-val)))

(defmacro defuvar* [uvarse-sym uvar-key uvar-val]
  (assert (not @uvarses) "must be before (load-all-uvarse!)")
  (assert (symbol? uvarse-sym))
  (assert (resolve uvarse-sym) "must be after defuvarse")
  (assert (keyword? uvar-key))
  (let [sym (symbol (str (name uvarse-sym)
                         uvar-infix-str 
                         (encode-ns-name (namespace uvar-key))
                         uvar-infix-str 
                         (name uvar-key)
                         uvar-infix-str 
                         (name (gensym))))]
    `(def ~sym ~uvar-val)))



(defn load-all-uvarse! []
  (let [r (reduce
            (fn [seed [a-sym a-var]]
              (let [splitted (clojure.string/split (name a-sym) uvar-infix-re)]
                (if (= 1 (count splitted))
                  seed
                  (let [uvarse-name (symbol (first splitted))
                        uvar-key (keyword (decode-ns-name (second splitted))
                                          (nth splitted 2 nil))
                        append-mode? (nth splitted 3 nil)
                        old-uvarse (or (get seed uvarse-name) {})
                        a-val (var-get a-var)
                        uvar-val (if append-mode?
                                   (conj (old-uvarse uvar-key) a-val)
                                   a-val)
                        new-uvarse (assoc old-uvarse uvar-key uvar-val)]
                    (assoc seed uvarse-name new-uvarse)))))
            {}
            (mapcat ns-interns (all-ns)))]
    (reset! uvarses r)))

;;; ----------------------------------------------------------------



