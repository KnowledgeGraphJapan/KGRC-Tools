package fasttransx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * Fast-TransX用にURIをIDに置き換える
 * 第1引数: s, p, oのカンマ区切りファイル
 * 出力: train2id.txt, entity2id.txt, relation2id.txt
 */

public class URI2ID {

	static Map<String,Integer> nodeMap = new HashMap<String,Integer>();
	static Map<String,Integer> edgeMap = new HashMap<String,Integer>();

	private static ArrayList<String> createEdgeList(String path) {
		ArrayList<String> edge_list = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			String array[] = new String[3];
			String property = "";
			String s = "";
			String o = "";
			int source = 0;
			int target = 0;
			int i = 0;	//nodeId
			int j = 0;	//edgeId
			br.readLine();	//最初の行を飛ばす
			while((line = br.readLine()) != null) {
				array = line.split("\t");
				s = array[0].replaceAll("\"", "");
				//URIと連番IDの対応表作成
				if(nodeMap.containsKey(s)) {
					source = nodeMap.get(s);
				} else {
					source = i;
					nodeMap.put(s, i);
					i++;
				}

				property = array[1].replaceAll("\"", "");
				if(!edgeMap.containsKey(property)) {
					edgeMap.put(property, j);
					j++;
				}

				o = array[2].replaceAll("\"", "");

				if(nodeMap.containsKey(o)) {
					target = nodeMap.get(o);
				} else {
					target = i;
					nodeMap.put(o, i);
					i++;
				}

				edge_list.add(Integer.toString(source) + "\t" + Integer.toString(target) + "\t" + Integer.toString(edgeMap.get(property)));	//trainXの場合
			}
			br.close();
			fr.close();

		} catch(Exception e) {
			e.printStackTrace();
		}
		return edge_list;
	}

	public static void main(String[] args) {
		String path = args[0];
		String train_size = args[1];
		String valid_size = args[2];
		String test_size = args[3];
		String train2id = "export/train2id.txt";
		String valid2id = "export/valid2id.txt";
		String test2id = "export/test2id.txt";
		String node_id_list = "export/entity2id.txt";
		String edge_id_list = "export/relation2id.txt";

		System.out.println("start");
		ArrayList<String> edge_list = createEdgeList(path);
		try {

			int triple_size = edge_list.size();
			Double train_ratio = Double.parseDouble(train_size)/10;
			Double valid_ratio = Double.parseDouble(valid_size)/10;
			Double test_ratio = Double.parseDouble(test_size)/10;
			int train_triple_size = (int) Math.round(triple_size * train_ratio);
			int valid_triple_size = (int) Math.round(triple_size * valid_ratio);
			int test_triple_size = (int) Math.round(triple_size * test_ratio);
			
			System.out.println("Total: " + triple_size);
			int triple_size2 = train_triple_size + valid_triple_size + test_triple_size;
			System.out.println("Sum of train, valid, and test: " + triple_size2);
			if (triple_size != triple_size2) {
				boolean flag = true;
				if (triple_size2 < triple_size) {
					while (flag) {
						if (triple_size2 < triple_size) {
							train_triple_size++;
							triple_size2 = train_triple_size + valid_triple_size + test_triple_size;
							System.out.println("train++");
						} else {
							flag = false;
						}
					}
				} else {
					while (flag) {
						if (triple_size2 > triple_size) {
							train_triple_size--;
							triple_size2 = train_triple_size + valid_triple_size + test_triple_size;
							System.out.println("train--");
						} else {
							flag = false;
						}
					}
				}
			}
			
			/* 
			 * head relation tailをタブ区切りで
			 */
			
			System.out.println("train:" + train_triple_size + ", validation: " + valid_triple_size + ", test: " + test_triple_size);
			
			// train
			FileWriter fw = new FileWriter(train2id, true);
			fw.write(Integer.toString(train_triple_size) + "\n");
			for(int i=0; i<train_triple_size; i++) {
				fw.write(edge_list.get(i) + "\n");
			}
			fw.close();
			
			// valid
			FileWriter fw_v = new FileWriter(valid2id, true);
			fw_v.write(Integer.toString(valid_triple_size) + "\n");
			for(int i=train_triple_size; i<(train_triple_size + valid_triple_size); i++) {
				fw_v.write(edge_list.get(i) + "\n");
			}
			fw_v.close();
			
			// test
			FileWriter fw_t = new FileWriter(test2id, true);
			fw_t.write(Integer.toString(test_triple_size) + "\n");
			for(int i=(train_triple_size + valid_triple_size); i<(train_triple_size + valid_triple_size + test_triple_size); i++) {
				fw_t.write(edge_list.get(i) + "\n");
			}
			fw_t.close();
			
			/* ID 対応表*/

			//node idリスト
			
			List<Entry<String, Integer>> list_nodeMap = new ArrayList<Entry<String, Integer>>(nodeMap.entrySet());
	        // idの値で昇順に並び替え
	        Collections.sort(list_nodeMap, new Comparator<Entry<String, Integer>>() {
	            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
	                // 昇順
	                return obj1.getValue().compareTo(obj2.getValue());
	            }
	        });
			
			FileWriter fw2 = new FileWriter(node_id_list, true);
			fw2.write(Integer.toString(nodeMap.size()) + "\n");
			for(Map.Entry<String, Integer> e : list_nodeMap) {
				fw2.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
			fw2.close();

			//edge idリスト
			
			List<Entry<String, Integer>> list_edgeMap = new ArrayList<Entry<String, Integer>>(edgeMap.entrySet());
			// idの値で昇順に並び替え
	        Collections.sort(list_edgeMap, new Comparator<Entry<String, Integer>>() {
	            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
	                // 昇順
	                return obj1.getValue().compareTo(obj2.getValue());
	            }
	        });
			
			FileWriter fw3 = new FileWriter(edge_id_list, true);
			fw3.write(Integer.toString(edgeMap.size()) + "\n");
			for(Map.Entry<String, Integer> e : list_edgeMap) {
				fw3.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
			fw3.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish");
	}

}
