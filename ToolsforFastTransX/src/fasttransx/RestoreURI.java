package fasttransx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
 * nodeID, edgeIDをURIに戻す
 * 第1引数: entity2id.txt or relation2id.txt
 * 第2引数: entity2vec.vec or relation2vec.vec
 * 出力: uri2vec.txt
 */
public class RestoreURI {

	static Map<String,String> nodeMap = new HashMap<String,String>();

	private static void readId(String path) {
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			br.readLine();
			String line = "";
			while((line = br.readLine()) != null) {
				String[] array = line.split("\t");
				nodeMap.put(array[1], array[0]);
			}
			br.close();
			fr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<String> restoreId(String path) {
		ArrayList<String> list = new ArrayList<String>();
		try {
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int id=0;
			while((line = br.readLine()) != null) {
				String[] array = line.split("\t");
				String name = nodeMap.get(Integer.toString(id));
				if(name==null) {
					System.out.println(id);
					continue;
				}
				//cellリソースのみneural netに使うため
				System.out.println(array[0] + " " +name);
				//				

				String vector = "";
				vector += name;
				for(int i=0; i<array.length; i++) {
					vector += "," + array[i];
				}
				list.add(vector);
				id++;
			}
			br.close();
			fr.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void main(String[] args) {
		String id_list = args[0];	//entity2id.txt or relation2id.txt
		String emb_file = args[1];//entity2vec.vec or relation2vec.vec
		String out = "export/uri2vec.txt";
		readId(id_list);
		ArrayList<String> list = restoreId(emb_file);
		Collections.sort(list);
		try {
			FileWriter fw = new FileWriter(out, true);
			for(String vector: list) {
				fw.write(vector + "\n");
			}
			fw.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
