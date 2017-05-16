package search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class LittleSearchEngineTest {

	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	
	public static void main(String[] args) throws IOException {
		System.out.print("Enter noise file name => ");
		String noiseWordsFile = keyboard.readLine();
		
		System.out.print("\nEnter document file name => ");
		String docsFile = keyboard.readLine();
		
		LittleSearchEngine engine = new LittleSearchEngine();
		engine.makeIndex(docsFile, noiseWordsFile);
		
//		System.out.println("Alice" + engine.keywordsIndex.get("alice"));
		
//		for(String x : engine.keywordsIndex.keySet())
//			System.out.println(x + engine.keywordsIndex.get(x));
		
		System.out.print("\nEnter two words (e.g. deep or world), quit to stop => ");
		String words = keyboard.readLine();
		while(!words.equals("quit")){
			String wrd1 = words.substring(0, words.indexOf(" or "));
			String wrd2 = words.substring(words.indexOf(" or ")+4);
			
			ArrayList<String> list = engine.top5search(wrd1, wrd2);
			
			System.out.println(list.toString());
			
			System.out.print("\nEnter two words (e.g. deep or world), quit to stop => ");
			words = keyboard.readLine();
		}
	}

}
