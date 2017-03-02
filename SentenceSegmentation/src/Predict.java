import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import ca.uwo.csd.ai.nlp.common.SparseVector;
import ca.uwo.csd.ai.nlp.kernel.KernelManager;
import ca.uwo.csd.ai.nlp.kernel.LinearKernel;
import ca.uwo.csd.ai.nlp.libsvm.svm_model;
import ca.uwo.csd.ai.nlp.libsvm.ex.Instance;
import ca.uwo.csd.ai.nlp.libsvm.ex.SVMPredictor;

public class Predict {
	
	public static Map <String,Integer > featureMap = new HashMap<String,Integer> ();
	public static List<String> boundary = new ArrayList<String>();
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		String testString = "/home/bear/Desktop/serverguide.TXT";
		String testzh = "/home/work/Data/raw.para/judicial.zh.seg";
		Scanner zh = new Scanner(new File(testString));
		PrintWriter writer = new PrintWriter("/home/bear/Desktop/serverguide.split");
		while(zh.hasNext()){
			String l = zh.nextLine();
			if (l.trim().length()>0){
				List<String> ent = Predicten(l);
				for (String s:ent) {
				writer.println(s);
				}
			}
		}
		//List<String> en = Predicten(testString);
		//List<String> zh = Predictzh(testzh);
		//PrintWriter writer = new PrintWriter("/home/work/senData/cn/testres");
		//for (String s:en) {
		//	writer.println(s);
		//}
		writer.close();

	}

	public static void loadFeature(String table) throws FileNotFoundException {

		Scanner in = new Scanner(new File(table));
		while (in.hasNextLine()) {
			
			String[] f = in.nextLine().split(" ");
			
			featureMap.put(f[0], Integer.parseInt(f[1]));
		}
		in.close();
	}
	
	public static Instance toIns (String feature){
		String[] ts = feature.split("\\s+");
        ArrayList<Double> label = new ArrayList<Double>();
        SparseVector vector = new SparseVector(ts.length - 1);
		label.add(Double.parseDouble(ts[0]));
		for (int i = 1; i < ts.length; i++) {
            String[] fields = ts[i].split(":");
            int index = Integer.parseInt(fields[0]);
            double value = Double.parseDouble(fields[1]);
            vector.add(index, value);
        }
		Instance ins = new Instance(label.get(0),vector);
        return ins;
	}
	
	public static void loadB(String boundaries) throws FileNotFoundException{

		Scanner in = new Scanner(new File(boundaries));
		 while (in.hasNextLine()) {
			 boundary.add(in.nextLine().trim());
		 }
		 in.close();
	 }
	public static List<String> Predicten(String text ) throws ClassNotFoundException, IOException {
		//load feature map
		loadFeature("/home/work/senData/en/featureTable.en");
		//load boundary
		loadB("/home/work/senData/en/boundaries.en");
		//set model
		svm_model model = SVMPredictor.loadModel("/home/work/senData/en/en.model");
		KernelManager.setCustomKernel(new LinearKernel()); 
		//split sentence
		String[] tl = text.split("\\s+");
		//token array list
		ArrayList<String> tal = new ArrayList<String>();
		//final sentence list
		List<String> sents = new ArrayList<String>();
		//feature list of each token
		//get feature list
		for (int i = 0;i<tl.length;i++) {
			tal.add(tl[i]);
			FeatureEn.boundaryIndex.add(0);
			}
		FeatureEn.tokens = tal;
		FeatureEn.featureMap = featureMap;
		//predict
		int start = 0;
		//judge and rewrite
		for (int i= 1;i<tal.size()-1;i++) {
			String letters = tl[i].length() > 2 ? tl[i].substring(tl[i].length() - 2) : tl[i];
			if (boundary.contains(letters)) {
				//System.out.println(letters);
				ArrayList<String> f = FeatureEn.getEnFeature(i);
				Instance ins = toIns(f.get(0));
				double prediction = SVMPredictor.predict(ins, model, true);
				//System.out.println(letters+" "+prediction);
				if (prediction == 1.0) {
					//System.out.println("yes");
					List<String> newArray = tal.subList(start, i+1);
					//System.out.println(newArray.size());
					String res = String.join(" ", newArray);
					System.out.println(res);
					sents.add(res);
					start=i+1;
					}
			}
		}
		//System.out.println(start);
		//System.out.println(tal.size()-1);
		List<String> new1 = tal.subList(start, tal.size());
		//System.out.println(newArray.size());
		String res1 = String.join(" ", new1);
		System.out.println(res1);
		sents.add(res1);
		return sents;
	}
	
	public static List<String> Predictzh(String text) throws ClassNotFoundException, IOException {
		loadFeature( "/home/work/senData/cn/featureTable.zh");
		//load boundary
		loadB("/home/work/senData/cn/boundaries.zh");
		//set model
		svm_model model = SVMPredictor.loadModel("/home/work/senData/cn/zh.model");
		KernelManager.setCustomKernel(new LinearKernel()); 
		//split sentence
		String[] tl = text.split("\\s+");
		//token array list
		ArrayList<String> tal = new ArrayList<String>();
		//final sentence list
		List<String> sents = new ArrayList<String>();
		//feature list of each token
		//get feature list
		for (int i = 0;i<tl.length;i++) {
			tal.add(tl[i]);
			FeatureZh.boundaryIndex.add(0);
			}
		FeatureZh.tokens = tal;
		FeatureZh.featureMap = featureMap;
		//predict
		int start = 0;
		//judge and rewrite
		for (int i= 2;i<tal.size()-1;i++) {
			String letters = tl[i].length() > 2 ? tl[i].substring(tl[i].length() - 2) : tl[i];
			if (boundary.contains(letters)) {
				//System.out.println(letters);
				ArrayList<String> f = FeatureZh.getZhFeature(i);
				Instance ins = toIns(f.get(0));
				double prediction = SVMPredictor.predict(ins, model, true);
				//System.out.println(letters+" "+prediction);
				if (prediction == 1.0) {
					//System.out.println("yes");
					List<String> newArray = tal.subList(start, i+1);
					//System.out.println(newArray.size());
					String res = String.join(" ", newArray);
					System.out.println(res);
					sents.add(res);
					start=i+1;
					}
			}
		}
		//System.out.println(start);
		//System.out.println(tal.size()-1);
		List<String> new1 = tal.subList(start, tal.size());
		//System.out.println(newArray.size());
		String res1 = String.join(" ", new1);
		System.out.println(res1);
		sents.add(res1);
		return sents;
	}
	
	

}
