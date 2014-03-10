<div align="center"><img src="https://github.com/ayamada/uvarse/raw/master/logo.png" /></div>


# uvarse

universal var module


## What is this

「複数のモジュール内に(defhoge hoge ...)を書き、
後で、defhogeしたものを全て一括でなめて処理したい」
という処理を書く為の補助モジュール。

コンパイルタイムに生成した情報を特殊な名前空間に保持し、
後から参照する事ができるようにする。

defhogeを利用するモジュールが一つだけもしくは固定であるなら、
defmacroで適当に実装できる。
しかし、defhogeを行うモジュールが不特定多数になった場合、
defmacroでは、コンパイルを挟んだ場合に、全てのモジュール空間を
検索してくれなくなるので、この種の工夫が必要になってしまった。

尚、現在は「symbolを生成し、そのvarに情報を保存する」という仕様になっている。
この生成したsymbolが衝突する可能性が一応ある。


## Glossary

- uvarse
    - 後述のuvarが保存される特殊な名前空間。名前指定により複数保持可能。
      基本的には、後からの追加変更はできない。
      読みはウヴァースもしくはユヴァースもしくは湯葉酢。

- uvar
    - 情報実体。大体何でも保存可能。
      基本的には、後からの追加変更はできない。
      読みはウヴァーもしくはユヴァーもしくは湯葉。


## Usage

安定版だがロードに時間がかかる`load-all-uvarse!`方式と、
alpha版の`load-all-uvarse-on-compile-time!`方式とがある。

- `load-all-uvarse!`方式

~~~
(require '[uvarse.core :refer [defuvarse defuvar defuvar*] :as uvarse])
(defuvarse svar-defs)
(defuvar svar-defs :hoge "abc") ; Can put in either current or another modules
(defuvar* svar-defs :fuge "def")
(defuvar* svar-defs :fuge "123")

;;; - compile -

(uvarse/load-all-uvarse!) ; NB: It is too slow
@svar-defs ; => {:hoge "abc" :fuge ["def" "123"]}
(:hoge @svar-defs) ; => "abc"
(:fuge @svar-defs) ; => ["def" "123"]
~~~

- `load-all-uvarse-on-compile-time!`方式

~~~
(未実装)
~~~


## TODO

- テスト書き
- ドキュメントの英語化


## License

Copyright c 2014 ayamada

Distributed under the Eclipse Public License version 1.0


