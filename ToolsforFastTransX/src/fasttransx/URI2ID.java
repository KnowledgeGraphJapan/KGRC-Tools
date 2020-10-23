package fasttransx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
				array = line.split(",");
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
		String out = "export/train2id.txt";
		String node_id_list = "export/entity2id.txt";
		String edge_id_list = "export/relation2id.txt";

		System.out.println("start");
		ArrayList<String> edge_list = createEdgeList(path);
		try {

			/* 
			 * head relation tailをタブ区切りで
			 */
			FileWriter fw = new FileWriter(out, true);
			fw.write(Integer.toString(edge_list.size()) + "\n");
			for(String edge : edge_list) {
				fw.write(edge + "\n");
			}
			fw.close();

			//node idリスト
			FileWriter fw2 = new FileWriter(node_id_list, true);
			fw2.write(Integer.toString(nodeMap.size()) + "\n");
			for(Map.Entry<String, Integer> e : nodeMap.entrySet()) {
				fw2.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
			fw2.close();

			//edge idリスト
			FileWriter fw3 = new FileWriter(edge_id_list, true);
			fw3.write(Integer.toString(edgeMap.size()) + "\n");
			for(Map.Entry<String, Integer> e : edgeMap.entrySet()) {
				fw3.write(e.getKey() + "\t" + e.getValue() + "\n");
			}
			fw3.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("finish");
	}

}
