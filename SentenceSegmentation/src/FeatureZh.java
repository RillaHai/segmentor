import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FeatureZh {
	public static ArrayList<String> tokens = new ArrayList<String>();
	public static Set<String> boundaryTokens  = new HashSet<String>();
	public static Map <String,Integer > featureMap = new HashMap<String,Integer> ();
	public static Set<Integer> boundaryIndex = new HashSet<Integer>();
	
	public static void registFeature(String t){
		Integer result = featureMap.get(t);
		if (result == null) {
	    	 featureMap.put(t, featureMap.size()+1);
	     }
	}

	public static ArrayList<String> getZhFeature(int i) throws FileNotFoundException, UnsupportedEncodingException {
		ArrayList<String> totalFeas = new ArrayList<String>();
		ArrayList<Integer> features = new ArrayList<Integer>();
		//	ArrayList<Integer> features = new ArrayList<Integer>();
						
			//nextWordDigit
			String preWordDigit = null;
			if (Character.isDigit(tokens.get(i+1).charAt(0))) {
				preWordDigit = "preWordDigit=1";
			}else {
				preWordDigit = "preWordDigit=0";
			}
			registFeature(preWordDigit);
			features.add(featureMap.get(preWordDigit));
			
			//pre2word
			String ttb = tokens.get(i-2);
			registFeature("pre2word="+ttb);
			features.add(featureMap.get("pre2word="+ttb));
			
			//punctuation string
			String t = tokens.get(i);
			registFeature("punct"+t);
			features.add(featureMap.get("punct"+t));
			
			//preWord string
			String tb = tokens.get(i-1);
			registFeature("preword"+tb);
			features.add(featureMap.get("preword"+tb));
			
			//next word
			String nt = tokens.get(i+1);
			//System.out.println(nt);
			registFeature("nextword"+nt);
			features.add(featureMap.get("nextword"+nt));
			
			Collections.sort(features);
			ArrayList<String> scale = new ArrayList<String>();
			//check and register label
			if (boundaryIndex.contains(i)) {
				scale.add("1 ");
			} else {
				scale.add("0 ");
			}
			for (Integer f:features) {
				scale.add(f+":1 ");
				
			}
			String line = String.join("",scale);
			totalFeas.add(line);
			
	 
		return totalFeas;
	}
	
	public static void writeLibsvm (ArrayList<String> features,String out) throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(out,true));
		for (String f:features) {
			writer.println(f);
		writer.close();
			
		}

	}
}
