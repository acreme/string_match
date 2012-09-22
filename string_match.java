import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.*;


public class strMatch {
	
	//to count the comparisons!
	static long comps = 0;
	
	public static List<String> getInputs(String x) throws IOException{
		//get all the possible patterns from the input file as dilineated by &&&&'s
		List<String> patterns = new LinkedList<String>();
		FileInputStream inStream = new FileInputStream(x);
		String toAdd = "";
		int count = 1;
		while(true){
			char current = (char)inStream.read();
			if(current == (char)-1){
				//patterns.add(toAdd);
				break;
			}
			if(current == '&' && count == 2){
				count = 0;
				if(toAdd.charAt(0) == '\n'){
					toAdd = toAdd.substring(1);
				}
				patterns.add(toAdd);
				toAdd = "";
			}
			if(current == '&'){
				count++;
				current = (char)inStream.read();
			}	
			toAdd = toAdd + current;
			
		}
		return patterns;
	}		
	public static HashMap<Integer, String> hashit(int size, String x){
		//hashing using the ASCII method
		HashMap<Integer, String> full = new HashMap<Integer, String>();
		String test = "";
		int charVal = -1;
		for(int i = 0; i < x.length()-size-1; i++){
			test = x.substring(i, i+size);;
			int total = 0;
			for(int j = 0; j < test.length(); j++){
				charVal = (int)test.charAt(j);
				total = total+charVal;
			}
			comps = i;
			full.put(total, test);
		}
		return full;
	}
	public static HashMap<Integer, String> hashit2(int size, String x){
		//hashing using the prime/mod method
		HashMap<Integer, String> full = new HashMap<Integer, String>();
		String test = "";
		int charVal = -1;
		for(int i = 0; i < x.length()-size-1; i++){
			test = x.substring(i, i+size);
			int total = 0;
			//for(int j = 0; j < test.length(); j++){
				total = (256 * total + x.charAt(i)) %101;
			//}
			full.put(total, test);
		}
		return full;
	}
	public static int[] core(String x){
		//computing the cores and lengths of the cores for KMP
		char[] w = x.toCharArray();
		int[] t = new int[x.length()];
		int pos = 2;
		int cnd = 0;
		
		t[0] = -1;
		t[1] = 0;
		while(pos < w.length){
			if(w[pos-1] == w[cnd]){
				cnd = cnd + 1;
				t[pos] = cnd;
				pos = pos + 1;
			}
			else if(cnd > 0){
				cnd = t[cnd];
			}
			else{
				t[pos] = 0;
				pos = pos + 1;
			}
		}
		return t;
	}	
	public static ArrayList<String> makeText(String x, int size) throws IOException{
		
		FileInputStream inStream = new FileInputStream(x);
		ArrayList<String> all = new ArrayList<String>();
		String text = "";
		int i = 0;
		int blockSize = 2000;
//		char current = (char)inStream.read();
		while(true){
			//read in characters 2000 minus the length of the string...so we overlap by string length
			while(i < (blockSize-size)){
				char current = (char)inStream.read();
				if(current == (char)-1){
					if (i != (blockSize-size)-1){
						int left = (blockSize-i);
						//if the last block we get has less than 2000 chars, just populate it with 0's so we have a sufficient number
						for(int addit = 0; addit < left; addit++){
							text = text + 0;
						}

					}
					all.add(text);
					
					return all;
					//break;
				}
				i++;
				text = text + current;
			}
			all.add(text);
			text= text.substring((text.length()-size), text.length());
			i = size;
		}
	}
	public static int[] suffix(char[] x, int m, int[] suff){
		//algorithm to find a suffix
		int[] suffs = suff;
		int f = 0;
		int i = 0;
		int g = 0;
		suffs[m-1] = m;
		g = m - 1;
		for(i = m-2; i >= 0; --i){
			if (i > g && suffs[i + m - 1 - f] < i - g){
				suffs[i] = suffs[i + m - 1 - f];
			}
			else{
				if(i < g){
					g = i;
				}
				f = i;
				while (g >= 0 && x[g] == x[g + m - 1 - f]){
					--g;
				}
				suffs[i] = f - g;
			}
		}
		return suffs;
	}
	public static int[] bad(char[] pattern, int[] bad){
		//preprocessing for BM...getting all the bad char offsets
		char[] x = pattern;
		int m = x.length;
		int[] badchars = bad;
		int i;
		for(i = 0; i < m ; ++i){
			badchars[i] = m;
		}
		for(i = 0; i < m - 1; ++i){
			badchars[x[i]] = m - i - 1;
		}
		return badchars;
	}
	public static int[] good(char[] pattern, int[] good){
		
		//preprocessing for BM, computing all the good suffix lengths
		char[] x = pattern;
		int m = x.length;
		int[] goodchars = good;
		int i;
		int j;
		int[] suff = new int[m];
		suff = suffix(x, m, suff);
		
		for (i = 0; i < m; ++i){
			goodchars[i] = m;
		}
		j = 0;
		for(i = m - 1; i >= 0; --i){
			if (suff[i] == i + 1){
				for(; j < m - 1- i; ++j){
					if(goodchars[j] == m){
						goodchars[j] = m - 1 - i;
					}
				}
			}
		}
		for(i = 0; i <= m - 2; ++i){
			goodchars[m - 1 - suff[i]] = m - 1 - i;
		}
		return goodchars;
	}
	//mainly from the class lecture slides...
	public static int bruteForce(String pattern, String source){		
		int m = pattern.length();
		int n = source.length();
		if( m > n ){
			return -1;
		}
		for (int i = 0; i < n-m; i++){
			for (int j = 0; j < m-1; j++){
				comps++;
				if(pattern.charAt(j) != source.charAt(i+j)){
					break;
				}
				if(pattern.charAt(j) == source.charAt(m-1)){
					return i;
				}
			}
		}
		return -1;
	}
	public static int rk(String pattern, String source, int version){

		char[] pat = pattern.toCharArray();
		char[] src = source.toCharArray();
		//dump out if pattern is longer than source...no match possible
		if(pat.length > src.length){
			return -1;
		}
		int size = pat.length;
		int key = 0;
		HashMap<Integer, String> m;
		//version 1 = ascii...
		if(version == 1){
			for(int i = 0; i < size; i++){
				key = key + pat[i];
			}
		}
		//version 2 = prime manips
		else{
			key=0;
			for(int i = 0; i < size; i++){
				
				key = (256 * key + pat[i])%101;
				//System.out.println(key);
			}
		}
		if(version == 1){
			//fire off to hash alg1
			m = hashit(size, source);
		}
		else{
			//fire off to hash alg2
			m = hashit2(size, source);
		}
		String isit = m.get(key);
		//key is null, abort
		if(m.get(key) == null){
			return -1;
		}
		char[] itis = isit.toCharArray();
		//null value, abort
		if(itis == null){
			return -1;
		}
		int j = 0;
		//go through pattern/whatever value was returned from hash
		while(j < size){
			if(pat[j] != itis[j]){
				return -1;
			}
			j++;
		}
		//we know it is a match at this point.
		return 1;
	}
	public static int kmp(String pattern, String source){
		char[] s = source.toCharArray();
		char[] w = pattern.toCharArray();
		
		//dump out if the pattern is longer than the source...no match possible
		if (w.length > s.length){
			return -1;
		}
		if(w.length == 1){
			//just do bruteforce, since thats what'll happen anyway
			return bruteForce(pattern, source);
		}
		int m = 0;
		int i = 0;
		int[] t = core(pattern);
		
		while(m + i < s.length){
			if(w[i] == s[m + i]){
				if (i == w.length-1){
					return m;
				}
				comps++;
				i = i + 1;
			}
			else{
				m = m + i - t[i];
				if(t[i] > -1){
					i = t[i];
				}
				else{
					i = 0;
				}
			}
		}
		return -1;
	}
	
	public static int bm(String pattern, String source){
		//System.out.println(source);
		char[] x = pattern.toCharArray();
		char[] y = source.toCharArray();
		int m = x.length;
		int n = y.length;
		//automatically dump if pattern is longer than text
		if(m > n){
			return -1;
		}
		int i;
		int j = 0;
		int done = -1;
		//int array for good suffix
		int[] good = new int[m];
		//int array for bad char
		int[] bad = new int[n];
		
		//populate the arrays
		good = good(x, good);
		bad = bad(x, bad);
		
		//loop through ze elements
		while(j <= n - m){
			i = m - 1;
			while( i > 0 && x[i-1] == y[j + i - 1]){
				i--;
			}
			//find which value is greater...bad c or good pre
			if (i > 0){
				int k = bad[(int) y[j + i -1]];
				int z;
				//use bad char if appropriate
				if(k < i && k > good[i]){
					j = j + k;
					comps++;
				}
				//otherwise use good suffix
				else{
					comps++;
					j = j + good[i];
				}
			}
			else{
				return -1;
			}
		}
		return j;
	}
	//method to test java's indexOf routine
	public static int java(String pattern, String source){
		int loc = source.indexOf(pattern);
		return loc;
	}
	
	public static void main(String[] args) throws IOException {
	
		//list of all patterns that occur
		List<String> patterns = new LinkedList<String>();
		String pat = args[0];
		String txt = args[1];
		//String out = args[2];
		ArrayList<String> broken = new ArrayList<String>();
		//patterns is a list holding all the patterns to check against our text
		patterns = getInputs(pat);
		//fullText is the entire source text in string form...this prolly isn't
		//the best way to do this...
		
		int brute = -1;
		int rk = -1;
		int kmp = -1;
		int bm = -1;
		int java = -1;
		
		//run the tests for all patterns in pattern.txt
		for(int i = 0; i < patterns.size(); i++){
			String testPat = patterns.get(i);
			//send the source file to makeText to break it up
			broken = makeText(txt, testPat.length());
			//Stopwatch sw = new Stopwatch();
			long forceTime = 0;
			long rkTime = 0;
			long kmpTime = 0;
			long bmTime = 0;
			double javaTime = 0;
			
			
			//looping through for brute-force
			//I use the stopwatch.java program from mike scotts 307 course to track time
			//commented out the stopwatch so you can run it properly.
			for(int x = 0; x < broken.size(); x++){
				//sw.start();
				brute=bruteForce(testPat, broken.get(x));
				if(brute != -1){
					break;
				}
			}
			//sw.stop();
			//forceTime = sw.timeInNanoseconds();
			//System.out.println("Time for Brute = " + forceTime +" Comps for Brute = " + comps);
			comps = 0;
			
			
			//looping through for rk
			for(int x = 0; x < broken.size(); x++){
				//sw.start();
				//System.out.println(broken.get(x));
				//replace the 1 in the rk() call with another number to change the hash function
				//1 = ASCII
				//2 = prime/mod
				rk = rk(testPat, broken.get(x),1);
				if (rk != -1){
					break;
				}
			}
			//sw.stop();
			//rkTime = //sw.timeInNanoseconds();
			//System.out.println("Time for RK = " + rkTime +" Comps for RK = " + comps);
			comps = 0;
			
			
			//looping through for KMP
			for(int x = 0; x < broken.size(); x++){
				//sw.start();
				kmp = kmp(testPat, broken.get(x));
				if(kmp != -1){
					break;
				}
			}
			//sw.stop();
			//kmpTime = sw.timeInNanoseconds();
			//System.out.println("Time for KMP = " + kmpTime +" Comps for KMP = " + comps);
			comps = 0;
			
			
			//looping through for boyer-moore
			for(int x = 0; x < broken.size(); x++){
				//sw.start();
				bm = bm(testPat, broken.get(x));
				if(bm != -1){
					break;
				}
			}
			//sw.stop();
			//bmTime = sw.timeInNanoseconds();
			//System.out.println("Time for BM = " + bmTime +" Comps for BM = " + comps);
			comps = 0;
			
			
			//testing java's internal .indexOf function
//			for(int x = 0; x < broken.size(); x++){
//				sw.start();
//				java = java(testPat, broken.get(x));
//				if(java != -1){
//					break;
//				}
//			}
//			sw.stop();
//			javaTime = sw.time();
//			//System.out.println("Time for Java = " + javaTime);
//			comps = 0;
			
			
			OutputStreamWriter outputStreamWriter =
				 new OutputStreamWriter(new FileOutputStream(args[2]));
				 BufferedWriter bw = new BufferedWriter(outputStreamWriter);

			
			if(rk != -1){
				bw.write("RK PASSED: " + testPat);
				
			}
			else{
				bw.write("RK FAILED: " + testPat + "\n");
				
			}
			if(kmp != -1){
				bw.write("KMP PASSED: " + testPat + "\n");
				
			}
			else{
				bw.write("KMP FAILED: " + testPat + "\n");
				
			}
			if(bm != -1){
				bw.write("BM PASSED: " + testPat + "\n");
				
			}
			else{
				bw.write("BM FAILED: " + testPat + "\n");
				
			}
			if(brute != -1){
				bw.write("Brute Force PASSED: " + testPat + "\n");
				
			}
			else{
				
				bw.write("Brute Force FAILED: " + testPat + "\n");
				
			}
			bw.close();
		}
		
		
	}
}
