# FastTransX用前処理・後処理スクリプト
ナレッジグラフの埋め込み表現を獲得するTranslation-basedモデル(TransE等)のプログラム群[Fast-TransX](https://github.com/thunlp/Fast-TransX)用に、ナレッジグラフのURIをIDに置き換える前処理、IDからURIを復元する後処理スクリプトです。  
[ナレッジグラフ推論チャレンジTech Liv！](https://www.slideshare.net/KnowledgeGraph/part-2tech-live-238950488)で紹介  
  
## 2022/06/26更新
学習データ, 検証データ, テストデータの割合を指定してそれぞれファイルを生成する機能を追加  
与えるcsvデータの順序をランダムにする必要あり

## 用意するデータ
ナレッジグラフ推論チャレンジの[SPARQLエンドポイント](http://knowledge-graph.jp/sparql.html)からhead, relation, tailの組み合わせをCSVで取得  
下記SPARQLクエリを実行しCSVを取得  
```
#まだらの紐からtailがリテラルでないトリプルを取得(TransEはリテラルを扱えないため)
PREFIX kgc: <http://kgc.knowledge-graph.jp/ontology/kgc.owl#>
SELECT DISTINCT *
FROM <http://kgc.knowledge-graph.jp/data/SpeckledBand>
WHERE {
  ?s ?p ?o .
  filter(isURI(?o))
}
```

## 実行


`java -jar URI2ID.jar [csvファイルのパス] [学習データの割合(0〜10)] [検証データの割合(0〜10)] [テストデータの割合(0〜10)]`  
data/SpeckledBand.csvを読み込みexportフォルダにidリストのファイル（entity2id.txt, relation2id.txt, train2id.txt, valid2id.txt, test2id.txt）を作成  

`java -jar RestoreURI.jar [entity2id.txt|relation2id.txt] [entity2vec.vec|relation2vec.vec]`  
  
## 埋め込み表現獲得後のベクトルデータ
TransE(500次元, 100エポック)  
[こちら](https://github.com/KnowledgeGraphJapan/KGRC-Tools/tree/master/ToolsforFastTransX/vector)
