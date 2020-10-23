# FastTransX用前処理・後処理スクリプト
ナレッジグラフの埋め込み表現を獲得するTranslation-basedモデル(TransE等)のプログラム群[Fast-TransX](https://github.com/thunlp/Fast-TransX)用に、ナレッジグラフのURIをIDに置き換える前処理、IDからURIを復元する後処理スクリプトです。  
  
##用意するデータ
ナレッジグラフ推論チャレンジの[SPARQLエンドポイント](http://knowledge-graph.jp/sparql.html)からhead, relation, tailの組み合わせをCSVで取得  
```
PREFIX kgc: <http://kgc.knowledge-graph.jp/ontology/kgc.owl#>
SELECT DISTINCT *
FROM <http://kgc.knowledge-graph.jp/data/SpeckledBand>
WHERE {
  ?s ?p ?o .
  filter(isURI(?o))
}
```
