import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainClass {
	public static final String FILE_DNA = "dna";
	public static final String FILE_ENGLISH = "english";
	public static final String FILE_PROTEINS = "proteins";
	public static final String FILE_MAIN = ".prepared";
	public static final String FILE_PATTERN4 = "_pattern_4";
	public static final String FILE_PATTERN4REV = "_pattern_rev4";
	public static final String FILE_PATTERN8 = "_pattern_8";
	public static final String FILE_PATTERN8REV = "_pattern_rev8";
	public static final String FILE_PATTERN16 = "_pattern_16";
	public static final String FILE_PATTERN16REV = "_pattern_rev16";
	public static final String FILE_PATTERN32 = "_pattern_32";
	public static final String FILE_PATTERN32REV = "_pattern_rev32";
	public static final String FILE_PATTERN1024 = "_pattern_1024";
	public static final String FILE_PATTERN1024REV = "_pattern_rev1024";
	public static final Charset CHARSET = Charset.forName("ISO-8859-1");
	public static final int ASIZE = 1024;

	public static String readFile(String fileName) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(fileName), CHARSET);
		StringBuilder sb = new StringBuilder();
		for (String s : lines) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static List<String> readLines(String filename) throws IOException {
		return Files.readAllLines(Paths.get(filename), CHARSET);
	}

	public static void main(String[] args) throws IOException {
		long startTime;
		long HPtime;
		long BFtime;
		long SOtime;
		/*
		 * String fileDnaMain = readFile(FILE_DNA + FILE_MAIN); List<String>
		 * fileDnaPattern4 = readLines(FILE_DNA + FILE_PATTERN4); List<String>
		 * fileDnaPattern1024 = readLines(FILE_DNA + FILE_PATTERN1024);
		 * List<String> fileDnaPattern4Rev = readLines(FILE_DNA +
		 * FILE_PATTERN4REV);
		 */
		List<String> mainFiles = new ArrayList<String>();
		mainFiles.add(FILE_DNA);
		mainFiles.add(FILE_ENGLISH);
		mainFiles.add(FILE_PROTEINS);
		List<String> patternsList = new ArrayList<String>();
		patternsList.add(FILE_PATTERN4);
		patternsList.add(FILE_PATTERN4REV);
		patternsList.add(FILE_PATTERN8);
		patternsList.add(FILE_PATTERN8REV);
		patternsList.add(FILE_PATTERN16);
		patternsList.add(FILE_PATTERN16REV);
		patternsList.add(FILE_PATTERN32);
		patternsList.add(FILE_PATTERN32REV);
		patternsList.add(FILE_PATTERN1024);
		patternsList.add(FILE_PATTERN1024REV);

		int HPWIN = 0;
		int SOWIN = 0;
		int BFWIN = 0;
		for (String mainFile : mainFiles) {// dla kazdego pliku g³ównego: dna,
											// english, proteins
			System.out.println("PLIK: " + mainFile);
			for (String patternFile : patternsList) {// dla kazdego wzorca:
												// 4,8,4rev...
				System.out.println("   PATTERN: " + patternFile);
				String mainFileContent = readFile(mainFile + FILE_MAIN);
				List<String> paternFileContent = readLines(mainFile + patternFile);
				startTime = System.nanoTime();
				int trafienia = 0;	
				for (String pattern : paternFileContent) {
					trafienia+=BF(pattern, mainFileContent).size();
					
				}
				System.out.println("Trafienia: "+ trafienia);
				//System.out.println("      .");
				BFtime = (System.nanoTime() - startTime) / 1000000;
				startTime = System.nanoTime();
				trafienia = 0;	
				for (String pattern : paternFileContent) {
					trafienia+=HORSPOOL(pattern, mainFileContent).size();
				}
				System.out.println("Trafienia: "+ trafienia);
				//System.out.println("      .");
				HPtime = (System.nanoTime() - startTime) / 1000000;

				startTime = System.nanoTime();
				trafienia = 0;	
				for (String pattern : paternFileContent) {
					
					trafienia+=SO(pattern, mainFileContent).size();
				}
				System.out.println("Trafienia: "+ trafienia);
				//System.out.println("      .");
				SOtime = (System.nanoTime() - startTime) / 1000000;

				String patternType = "normalnych";
				if (patternFile.contains("rev"))
					patternType = "odwróconych";
				// System.out.format(" Czas (ms) wykonania poszukiwania %d
				// wzorców %s o d³ugoœci %d w pliku %s\n",
				// paternFileContent.size(),patternType,
				// paternFileContent.get(0).length(),mainFile);
				if (HPtime < BFtime && HPtime < SOtime) {
					System.out.format("      WIN: HP");
					HPWIN++;
				}
				else if (BFtime < HPtime && BFtime < SOtime) {
					System.out.format("      WIN: BF");
					BFWIN++;
				}
				else if (SOtime < BFtime && SOtime < HPtime) {
					System.out.format("      WIN: SO");
					SOWIN++;
				}else{
					System.out.format("      DROW   ");
				}
				System.out.format("  HP: %d, BF %d, SO %d\n", HPtime, BFtime, SOtime);
				
			}
		}
		System.out.format("Wygrane: HP-%d, BF-%d, SO-%d\n", HPWIN, BFWIN, SOWIN);
	}

	/**
	 * Wyszukiwanie wzorca metod¹ Boyera-Moore’a-Horspoola (BMH)
	 * 
	 * @param pattern
	 * @param content
	 * @return
	 */
	public static List<Integer> SO(String pattern, String content) {
		List<Integer> foundIndexes = new ArrayList<Integer>();
		int m = pattern.length();
		int n = content.length();
		char[] wzorzecArray = pattern.toCharArray();
		char[] contentArray = content.toCharArray();
		int lim, state;
		int S[] = new int[ASIZE];
		int j;
		int jj, limm;
		int i;
		for (i = 0; i < ASIZE; ++i)
			S[i] = ~0;
		for (limm = i = 0, jj = 1; i < m; ++i, jj <<= 1) {
			S[wzorzecArray[i]] &= ~jj;
			limm |= jj;
		}
		limm = ~(limm >> 1);
		lim = limm;
		for (state = ~0, j = 0; j < n; ++j) {
			state = (state << 1) | S[contentArray[j]];
			if (state < lim)
				foundIndexes.add(j);
		}
		return foundIndexes;
	}

	/**
	 * Wyszukiwanie wzorca metod¹ HorsPool
	 * 
	 * @param pattern
	 * @param content
	 * @return
	 */
	public static List<Integer> HORSPOOL(String pattern, String content) {
		List<Integer> foundIndexes = new ArrayList<Integer>();
		int m = pattern.length();
		int n = content.length();
		char[] wzorzecArray = pattern.toCharArray();
		char[] contentArray = content.toCharArray();
		int j;
		int[] bmBc = new int[10000];
		char c;
		int i;
		for (i = 0; i < ASIZE; ++i)
			bmBc[i] = m;
		for (i = 0; i < m - 1; ++i)
			bmBc[wzorzecArray[i]] = m - i - 1;
		j = 0;
		while (j <= n - m) {
			c = contentArray[j + m - 1];
			char[] TMPwzorzecArray = new char[m - 1];
			char[] TMPcontentArray = new char[m - 1];
			System.arraycopy(wzorzecArray, 0, TMPwzorzecArray, 0, m - 1);
			System.arraycopy(contentArray, j, TMPcontentArray, 0, m - 1);
			if (wzorzecArray[m - 1] == c && Arrays.equals(TMPwzorzecArray, TMPcontentArray))
				foundIndexes.add(j);
			j += bmBc[c];
		}
		return foundIndexes;
	}

	/**
	 * Wyszukiwanie wzorca metod¹ Brute Force
	 * 
	 * @param wzorzec
	 * @param content
	 * @return
	 */
	public static List<Integer> BF(String pattern, String content) {
		List<Integer> foundIndexes = new ArrayList<Integer>();
		int m = pattern.length();
		int n = content.length();
		char[] wzorzecArray = pattern.toCharArray();
		char[] contentArray = content.toCharArray();
		int i, j;
		for (j = 0; j <= n - m; ++j) {
			for (i = 0; i < m && wzorzecArray[i] == contentArray[i + j]; ++i)
				;
			if (i >= m) {
				foundIndexes.add(j);
			}
		}
		return foundIndexes;
	}
}
