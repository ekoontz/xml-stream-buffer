# xml-stream-buffer

Shows the fixed length 8192-character limit problem when parsing XML documents
using [data.xml](https://github.com/clojure/data.xml) with the JDK's bundled
com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl, and then
how I solved it by building data.xml with [woodstox](https://github.com/FasterXML/woodstox).


## The Problem

```
$ cd data.xml
$ git remote -v
origin	git@github.com:clojure/data.xml.git (fetch)
origin	git@github.com:clojure/data.xml.git (push)
$ git log -1
commit 564bec24c4bb6dc70f7e45ba76b60bc2d35ff09d (HEAD -> master, origin/master, origin/HEAD)
Author: Alex Miller <alex.miller@cognitect.com>
Date:   Thu Jun 18 13:05:06 2020 -0500

    add LICENSE text file

$ lein install
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
Created /Users/ekoontz/data.xml/target/data.xml-0-UE-DEVELOPMENT.jar
Wrote /Users/ekoontz/data.xml/pom.xml
Installed jar and pom into local repo.

$ cd ../xml-stream-buffer
$ lein repl
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
nREPL server started on port 50306 on host 127.0.0.1 - nrepl://127.0.0.1:50306
REPL-y 0.4.4, nREPL 0.6.0
Clojure 1.10.1
OpenJDK 64-Bit Server VM 14.0.1+7
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

xml-stream-buffer.core=> (load "core")
nil
xml-stream-buffer.core=> (small)
#xml/element{:tag :a}
xml-stream-buffer.core=> (too-big)
Error printing return value (IOException) at java.io.FileInputStream/readBytes (FileInputStream.java:-2).
Stream Closed
xml-stream-buffer.core=> *e
#error {
 :cause "ParseError at [row,col]:[2,8192]\nMessage: Stream Closed"
 :via
 [{:type clojure.lang.ExceptionInfo
   :message nil
   :data #:clojure.error{:phase :print-eval-result}
   :at [clojure.main$repl$read_eval_print__9086 invoke "main.clj" 442]}
  {:type javax.xml.stream.XMLStreamException
   :message "ParseError at [row,col]:[2,8192]\nMessage: Stream Closed"
   :at [com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl next "XMLStreamReaderImpl.java" 647]}]
 :trace
 [[com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl next "XMLStreamReaderImpl.java" 647]
  [clojure.data.xml.jvm.parse$pull_seq$fn__1030 invoke "parse.clj" 78]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.RT seq "RT.java" 535]
  [clojure.core$seq__5402 invokeStatic "core.clj" 137]
  [clojure.core$seq__5402 invoke "core.clj" 137]
  [clojure.data.xml.tree$seq_tree$fn__863 invoke "tree.clj" 39]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.LazySeq first "LazySeq.java" 73]
  [clojure.lang.RT first "RT.java" 692]
  [clojure.core$first__5384 invokeStatic "core.clj" 55]
  [clojure.core$first__5384 invoke "core.clj" 55]
  [clojure.data.xml.tree$seq_tree$fn__863$fn__875 invoke "tree.clj" 48]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.RT seq "RT.java" 535]
  [clojure.core$seq__5402 invokeStatic "core.clj" 137]
  [clojure.core$filter$fn__5893 invoke "core.clj" 2809]
  [clojure.lang.LazySeq sval "LazySeq.java" 42]
  [clojure.lang.LazySeq seq "LazySeq.java" 51]
  [clojure.lang.Cons next "Cons.java" 39]
  [clojure.lang.LazySeq next "LazySeq.java" 83]
  [clojure.lang.RT next "RT.java" 713]
  [clojure.core$next__5386 invokeStatic "core.clj" 64]
  [clojure.core$next__5386 invoke "core.clj" 64]
  [clojure.data.xml.node$eval523$fn__525 invoke "node.cljc" 176]
  [clojure.lang.MultiFn invoke "MultiFn.java" 234]
  [nrepl.middleware.print$pr_on invokeStatic "print.clj" 21]
  [nrepl.middleware.print$pr_on invoke "print.clj" 17]
  [nrepl.middleware.print$send_nonstreamed$print_key__1924$fn__1925 invoke "print.clj" 148]
  [nrepl.middleware.print$send_nonstreamed$print_key__1924 invoke "print.clj" 147]
  [clojure.core$map$fn__5862$fn__5863 invoke "core.clj" 2742]
  [clojure.core.protocols$iter_reduce invokeStatic "protocols.clj" 49]
  [clojure.core.protocols$fn__8140 invokeStatic "protocols.clj" 75]
  [clojure.core.protocols$fn__8140 invoke "protocols.clj" 75]
  [clojure.core.protocols$fn__8088$G__8083__8101 invoke "protocols.clj" 13]
  [clojure.core$transduce invokeStatic "core.clj" 6884]
  [clojure.core$transduce invoke "core.clj" 6870]
  [nrepl.middleware.print$send_nonstreamed invokeStatic "print.clj" 156]
  [nrepl.middleware.print$send_nonstreamed invoke "print.clj" 138]
  [nrepl.middleware.print$printing_transport$reify__1939 send "print.clj" 174]
  [nrepl.middleware.caught$caught_transport$reify__1974 send "caught.clj" 58]
  [nrepl.middleware.interruptible_eval$evaluate$fn__2028 invoke "interruptible_eval.clj" 114]
  [clojure.main$repl$read_eval_print__9086 invoke "main.clj" 442]
  [clojure.main$repl$fn__9095 invoke "main.clj" 458]
  [clojure.main$repl invokeStatic "main.clj" 458]
  [clojure.main$repl doInvoke "main.clj" 368]
  [clojure.lang.RestFn invoke "RestFn.java" 1523]
  [nrepl.middleware.interruptible_eval$evaluate invokeStatic "interruptible_eval.clj" 79]
  [nrepl.middleware.interruptible_eval$evaluate invoke "interruptible_eval.clj" 55]
  [nrepl.middleware.interruptible_eval$interruptible_eval$fn__2037$fn__2041 invoke "interruptible_eval.clj" 142]
  [clojure.lang.AFn run "AFn.java" 22]
  [nrepl.middleware.session$session_exec$main_loop__2138$fn__2142 invoke "session.clj" 171]
  [nrepl.middleware.session$session_exec$main_loop__2138 invoke "session.clj" 170]
  [clojure.lang.AFn run "AFn.java" 22]
  [java.lang.Thread run "Thread.java" 832]]}
xml-stream-buffer.core=>
```


## The solution

```
$ cd ../data.xml
$ git remote add ekoontz git@github.com:ekoontz/data.xml.git
$ git fetch ekoontz
$ git checkout woodstox
$ lein install
$ cd ../xml-stream-buffer
$ lein repl
OpenJDK 64-Bit Server VM warning: Options -Xverify:none and -noverify were deprecated in JDK 13 and will likely be removed in a future release.
...
xml-stream-buffer.core=> (load "core")
nil
xml-stream-buffer.core=> (small)
#xml/element{:tag :a}
xml-stream-buffer.core=> (too-big)
Error printing return value (IOException) at java.io.FileInputStream/readBytes (FileInputStream.java:-2).
Stream Closed
xml-stream-buffer.core=> (parseable)
#xml/element{:tag :a}
xml-stream-buffer.core=>
```

## The diff for `data.xml`


```
$ cd ../data.xml
$ git diff master..woodstox project.clj src | cat
diff --git a/project.clj b/project.clj
index eb91d2b..cf6a03f 100644
--- a/project.clj
+++ b/project.clj
@@ -8,5 +8,6 @@
                  [org.clojure/tools.nrepl "0.2.13"]
                  [org.clojure/test.check "0.9.0"]
                  [figwheel-sidecar "0.5.17"]
+                 [com.fasterxml.woodstox/woodstox-core "6.2.1"]
                  [binaryage/devtools "0.9.10"]]
   :repl-options {:nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]})
diff --git a/src/main/clojure/clojure/data/xml/jvm/parse.clj b/src/main/clojure/clojure/data/xml/jvm/parse.clj
index 8b631bc..aa06c0e 100644
--- a/src/main/clojure/clojure/data/xml/jvm/parse.clj
+++ b/src/main/clojure/clojure/data/xml/jvm/parse.clj
@@ -20,6 +20,7 @@
    (java.io InputStream Reader)
    (javax.xml.stream
     XMLInputFactory XMLStreamReader XMLStreamConstants)
+   (com.ctc.wstx.api WstxInputProperties)
    (clojure.data.xml.event EndElementEvent)))

 (def ^{:private true} input-factory-props
@@ -31,7 +32,8 @@
    :validating XMLInputFactory/IS_VALIDATING
    :reporter XMLInputFactory/REPORTER
    :resolver XMLInputFactory/RESOLVER
-   :support-dtd XMLInputFactory/SUPPORT_DTD})
+   :support-dtd XMLInputFactory/SUPPORT_DTD
+   :input-buffer-length WstxInputProperties/P_INPUT_BUFFER_LENGTH})

 (defn- attr-prefix [^XMLStreamReader sreader index]
   (let [p (.getAttributePrefix sreader index)]
```
