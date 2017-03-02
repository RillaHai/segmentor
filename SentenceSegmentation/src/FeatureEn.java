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

public class FeatureEn {
	public static ArrayList<String> tokens = new ArrayList<String>();
	public static ArrayList<Integer> boundaryIndex = new ArrayList<Integer>();
	public static Set<String> boundaryTokens  = new HashSet<String>();
	public static Map <String,Integer > featureMap = new HashMap<String,Integer> ();
	
	public static ArrayList<String> getEnFeature(int i) throws FileNotFoundException, UnsupportedEncodingException {
		
		ArrayList<String> totalFeas = new ArrayList<String>();
		ArrayList<Integer> featureIndex = new ArrayList<Integer>();
		ArrayList<String> features = new ArrayList<String>();
						
			//preWordDigit
			String preWordDigit = null;
			if (Character.isDigit(tokens.get(i-1).charAt(0))) {
				preWordDigit = "preWordDigit=1";
			}else {
				preWordDigit = "preWordDigit=0";
			}
			features.add(preWordDigit);
			
			//preWordFirstLetterCapital
			String preWordFirstLetterCapital = "preWordFirstLetterCapital=0";
			if (Character.isUpperCase(tokens.get(i-1).charAt(0))){
				preWordFirstLetterCapital = "preWordFirstLetterCapital=1";
			} 
			features.add(preWordFirstLetterCapital);
			
			//punctFirstLetterCapital
			String punctFirstLetterCapital = "punctFirstLetterCapital=0";
			if (Character.isUpperCase(tokens.get(i).charAt(0))){
				punctFirstLetterCapital = "punctFirstLetterCapital=1";
			}
			features.add(punctFirstLetterCapital);
			
			//nextWordFirstLetterCapital
			String nextWordFirstLetterCapital = "nextWordFirstLetterCapital=0";
			if (Character.isUpperCase(tokens.get(i+1).charAt(0))){
				nextWordFirstLetterCapital = "nextWordFirstLetterCapital=1";
			}
			features.add(nextWordFirstLetterCapital);
			
			//punctuation string
			String t = tokens.get(i);
			features.add("punct="+t);
			
			//preWord string
			String tb = tokens.get(i-1);
			features.add("preword="+tb);
			
			//next word
			String nt = tokens.get(i+1);
			//System.out.println(nt);
			features.add("nextword="+nt);
			
			//last letter of punctuation
			char lastletter = t.charAt(t.length()-1);
			String ll = Character.toString(lastletter);
			features.add("lastletter="+ll);
			
			//last two letter of punctuation
			String lasttwoletters = t.length() > 2 ? t.substring(t.length() - 2) : t;
			features.add("lasttwoletter="+lasttwoletters );

			for (String f: features){
				if (featureMap.get(f) != null){
					int index = featureMap.get(f);
					featureIndex.add(index);
					} 
				}
				
			
			Collections.sort(featureIndex);
			ArrayList<String> scale = new ArrayList<String>();
			//check and register label
			if (boundaryIndex.contains(i)) {
				scale.add("1 ");
			} else {
				scale.add("0 ");
			}
			for (Integer f:featureIndex) {
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
