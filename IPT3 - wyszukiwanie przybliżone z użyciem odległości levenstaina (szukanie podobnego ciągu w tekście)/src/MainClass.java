import java.awt.dnd.DnDConstants;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * Wyszukiwanie dynamiczne i k+1
 * czyli wyszukiwanie w tekœcie s³ów któe s¹ podobne do szukanego s³owa
 * porównanie czasów dn (brute force) i k+1 
 * @author pawel
 *
 */
public class MainClass {
	public static final boolean DEBUG = false;
	public static final String FILE_DNA = "dna";
	public static final String FILE_ENGLISH = "english";
	public static final String FILE_PROTEINS = "proteins";
	public static final String FILE_MAIN = ".prepared";
	public static final String FILE_PATTERN_4_L1 = "_pattern_4_L1";
	public static final String FILE_PATTERN_4_L2 = "_pattern_4_L2";
	public static final String FILE_PATTERN_4_L3 = "_pattern_4_L3";
	public static final String FILE_PATTERN_8_L1 = "_pattern_8_L1";
	public static final String FILE_PATTERN_8_L2 = "_pattern_8_L2";
	public static final String FILE_PATTERN_8_L3 = "_pattern_8_L3";
	public static final String FILE_PATTERN_16_L1 = "_pattern_16_L1";
	public static final String FILE_PATTERN_16_L2 = "_pattern_16_L2";
	public static final String FILE_PATTERN_16_L3 = "_pattern_16_L3";
	public static final String FILE_PATTERN_32_L1 = "_pattern_32_L1";
	public static final String FILE_PATTERN_32_L2 = "_pattern_32_L2";
	public static final String FILE_PATTERN_32_L3 = "_pattern_32_L3";
	public static final String FILE_PATTERN_1024_L1 = "_pattern_1024_L1";
	public static final String FILE_PATTERN_1024_L2 = "_pattern_1024_L2";
	public static final String FILE_PATTERN_1024_L3 = "_pattern_1024_L3";
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

	public static void init(List<String> mainFiles, List<String> patternsList) {
		if (DEBUG){
		mainFiles.add("test");
		patternsList.add("_pattern_x_xx");
		}else{
		
		mainFiles.add(FILE_PROTEINS);
		mainFiles.add(FILE_ENGLISH);
		mainFiles.add(FILE_DNA);
		patternsList.add(FILE_PATTERN_8_L1);
		patternsList.add(FILE_PATTERN_8_L2);
		patternsList.add(FILE_PATTERN_8_L3);
		patternsList.add(FILE_PATTERN_16_L1);
		patternsList.add(FILE_PATTERN_16_L2);
		patternsList.add(FILE_PATTERN_16_L3);
		patternsList.add(FILE_PATTERN_32_L1);
		patternsList.add(FILE_PATTERN_32_L2);
		patternsList.add(FILE_PATTERN_32_L3);
		}
	}

	public static void main(String[] args) throws IOException {
		long startTime;
		List<String> mainFiles = new ArrayList<String>();
		List<String> patternsList = new ArrayList<String>();
		init(mainFiles, patternsList);
		
		//Liczniki wygranych
		int DP_win = 0;
		int K1_win = 0;
		
		//Liczniki czasu
		long DP_time = 0;
		long K1_time = 0;
		long K2_time = 0;
		long K3_time = 0;

		//Dla kazdego pliku g³ównego: dna, english, proteins
		for (String mainFile : mainFiles) {
			System.out.println("PLIK: " + mainFile);

			//Dla kazdego pliku wzorca: 4L1, 4L2, 4L3, 8L1...
			for (String patternFile : patternsList) {
				String mainFileContent = readFile(mainFile + FILE_MAIN);
				List<String> paternFileContent = readLines(mainFile + patternFile);

				//Liczniki trafieñ do porównania poprawnoœæi dzia³ania algorytmów
				int trafieniaDP_1 = 0;
				int trafieniaK1 = 0;
				int trafieniaDP_2 = 0;
				int trafieniaDP_3 = 0;	
				
				//Wykonanie dla algorytmu DP brute force 
				startTime = System.nanoTime();
				//Dla ka¿dego wzorca w pliku
				for (String pattern : paternFileContent) {
					//System.out.println("Trafienia DP: "+DP(pattern, mainFileContent, 1,0).toString());
					trafieniaDP_1 += DP(pattern, mainFileContent, 1,0).size();
				}
				//Dziel ¿eby ustawiæ ms
				DP_time = (System.nanoTime() - startTime) / 1000000;
				
				
				//wykonanie dla algorytmu K+1 dla K=1
				startTime = System.nanoTime();
				for (String pattern : paternFileContent) {//wykonaj wyszukiwanie wszystkich patternów danego typu i zmierz czas metod¹ K+1
					trafieniaK1 += K1(pattern, mainFileContent);
				}
				K1_time = (System.nanoTime() - startTime) / 1000000;
				
				//Dodaj do licznika punkt za szybsze wykonanie
				if (DP_time>K1_time)
					K1_win++;
				if (DP_time<K1_time)
					DP_win++;
				

				System.out.println("   PATTERN: d³ugoœæ: " + patternFile.substring(9).replace("_", ", typ: ") + ", czas DP: " + DP_time + ", czas K1: " + K1_time);
				
				
				//wykonanie dla algorytmu K+1 dla K=2 oraz K=3 bez sumowania wygranej
				startTime = System.nanoTime();
				for (String pattern : paternFileContent) {//wykonaj wyszukiwanie wszystkich patternów danego typu i zmierz czas metoda DP K=2
					trafieniaDP_2+=DP(pattern, mainFileContent, 2,0).size();
				}
				K2_time = (System.nanoTime() - startTime) / 1000000;
				System.out.println("      PATTERN: d³ugoœæ: " + patternFile.substring(9).replace("_", ", typ: ") + ", czas K2: " + K2_time);
				startTime = System.nanoTime();
				for (String pattern : paternFileContent) {//wykonaj wyszukiwanie wszystkich patternów danego typu i zmierz czas metoda DP K=3
					trafieniaDP_3+=DP(pattern, mainFileContent, 3,0).size();
				}
				K3_time = (System.nanoTime() - startTime) / 1000000;
				System.out.println("      PATTERN: d³ugoœæ: " + patternFile.substring(9).replace("_", ", typ: ") + ", czas K3: " + K3_time);

				//podsumowanie trafieñ DP_1 powinno byæ równe K+1
				if (DEBUG){
					System.out.println("      Trafienia DP_1: "+trafieniaDP_1+", trafienia K+1: "+trafieniaK1+", trafienia DP_2: "+trafieniaDP_2+", trafienia DP_3: "+trafieniaDP_3);
					break;
				}
			}
		}
		System.out.println("Algorytm DP (brute force) wygra³ "+DP_win+" razy a algorytm K+1 "+K1_win+" razy.");
	}

	
	public static int K1(String pattern, String content) {	
		
		//Dzielenie wzorca na 2 czêœci
		String lewaCzescWzorca = pattern.substring(0,pattern.length()/2);
		String prawaCzescWzorca = pattern.substring(pattern.length()/2,pattern.length());

		//Lista trafieñ dok³adnych - indeks ostatniej litery w tekœcie pasuj¹cego ci¹gu
		List<Integer> trafieniaDokladneDlaLewejCzesci = SO(lewaCzescWzorca,content);
		Set<Integer> zbiorWszystkichTrafien = new HashSet<Integer>();
		
		//Dla ka¿dego trafienia dok³adnego lewej czêœci sprawdŸ trafienie PODOBNE dla prawej
		for (int trafienieDokladneLewej : trafieniaDokladneDlaLewejCzesci){
			String prawaCzescTekstu = "";
			//Je¿eli trafienie lewe bêdzie na koñcu listy ¿eby nie wyjœæ poza zakres
			int poczatekPrawegoWzorca = trafienieDokladneLewej + 1;
			if (poczatekPrawegoWzorca + prawaCzescWzorca.length() > content.length()-2)//+1 bo prawa czêœæ wzorca zaczyna siê po lewej, -1 bo size jest o 1 wiêkszy od rozmiaru tablicy
				prawaCzescTekstu = content.substring(poczatekPrawegoWzorca,content.length()-1);
			else 
				prawaCzescTekstu = content.substring(poczatekPrawegoWzorca+1,poczatekPrawegoWzorca + prawaCzescWzorca.length() + 2);//od pocz¹tku do koñca prawego teksti do poszukiwañ +2 bo K=1
			if (DEBUG)
			System.out.println("WZORZEC: "+prawaCzescWzorca+",  TEKST: "+ prawaCzescTekstu+ ", Trafiona lewa: "+lewaCzescWzorca  +", trafienia: "+DP(prawaCzescWzorca, prawaCzescTekstu,1,trafienieDokladneLewej+1).size()  );
			
			//System.out.println("Lewy wzorzec: " + lewaCzescWzorca);
			//System.out.println("PrawyTekst: " + prawaCzescTekstu + ", prawy wzorzec: " +prawaCzescWzorca+ ", ilosc trafien: "+DP(prawaCzescWzorca, prawaCzescTekstu,1,trafienieDokladneLewej+1) );
			//Dodaj pocz¹tek
			zbiorWszystkichTrafien.addAll(DP(prawaCzescWzorca, prawaCzescTekstu,1,trafienieDokladneLewej+1));		
		}
		
		//Dla ka¿dego trafienia dok³adnego prawej czêœci sprawdŸ trafienie PODOBNE dla lewej
		List<Integer> trafienieDokladneDlaPrawejCzesci = SO(prawaCzescWzorca,content);
		
		for (int trafienieDokladnePrawej : trafienieDokladneDlaPrawejCzesci){
			String lewaCzescTekstu = "";
			//Je¿eli trafienie prawe bêdzie na pocz¹tku mo¿e siê okazaæ ¿e zacznie szukaæ przed pocz¹tkiem listy lewego

			if(trafienieDokladnePrawej-lewaCzescWzorca.length()-prawaCzescWzorca.length() < 1) //-1 bo mo¿e byæ 1 liczba wiêcej
				lewaCzescTekstu = content.substring(0,trafienieDokladnePrawej-prawaCzescWzorca.length()+2);
			else
				lewaCzescTekstu = content.substring(trafienieDokladnePrawej-prawaCzescWzorca.length()-lewaCzescWzorca.length()+1,trafienieDokladnePrawej-prawaCzescWzorca.length()+2);
			
			
			//System.out.println("WZORZEC: "+lewaCzescWzorca+",  TEKST: "+ lewaCzescTekstu+ ", Trafiona prawa: "+prawaCzescWzorca    + "    prawy wzorzec:" + lewaCzescWzorca);
			
			//Set<Integer> zbiorZodwrobonymiIndeksamiTrafien = DP(new StringBuilder(lewaCzescWzorca).reverse().toString(), new StringBuilder(lewaCzescTekstu).reverse().toString(),1,trafienieDokladnePrawej);
			Set<Integer> zbiorZodwrobonymiIndeksamiTrafien = DP(lewaCzescWzorca, lewaCzescTekstu,1,0);
			if (zbiorZodwrobonymiIndeksamiTrafien.size()>0){
				zbiorWszystkichTrafien.add(trafienieDokladnePrawej+1);
				
			}		
		}
		//System.out.println("Trafienia K: "+zbiorWszystkichTrafien.toString());
		return zbiorWszystkichTrafien.size();	
	
	}

	/**
	 * Zwraca minimum dla 3 wartoœci 
	 * @param a
	 * @param b
	 * @param c
	 * @return
	 */
	public static int minimum(int a, int b, int c) {
		if (a <= b && a <= c)
			return a;
		if (b <= a && b <= c)
			return b;
		return c;
	}

	/**
	 * Wyszukiwanie metod¹ DP dynamic programming
	 */
	public static Set<Integer> DP(String pattern, String content, int k, int offset) {
		Set<Integer> zbior = new HashSet<Integer>();
		char[] patternChar = pattern.toCharArray();
		char[] contentChar = content.toCharArray();
		int dlugosc_tekstu = content.length();
		int dlugosc_wzorca = pattern.length();
		int p;
		int table[][] = new int[dlugosc_wzorca + 1][dlugosc_tekstu + 1];
		int trafienie = 0;
		
		//inicjalizujepierwszy wiersz i kolumnê zerami
		for (int j = 0; j <= dlugosc_tekstu; j++)
			table[0][j] = 0;
		for (int i = 0; i <= dlugosc_wzorca; i++)
			table[i][0] = i;
		
		//dla ca³ego tekstu
		for (int j = 1; j <= dlugosc_tekstu; j++) {
			//dla ca³ego wzorca
			for (int i = 1; i <= dlugosc_wzorca; i++) {
				//je¿eli zgodne
				if (patternChar[i - 1] == contentChar[j - 1])
					p = 0;
				else {
					p = 1;
				}
				table[i][j] = minimum(table[i - 1][j] + 1, table[i][j - 1] + 1, table[i - 1][j - 1] + p);
			}
			//je¿eli wzorzec zgodny lub podobny 
			if (table[dlugosc_wzorca][j] <= k){
				zbior.add(j - 1+offset);
				//trafienie++;
				//System.out.println(j - 1+offset);
				}
		}
		
		return zbior;
	}
	
	/**
	 * Algorytm SO wyszukiwania ca³kowitego (zgodnoœæ 100% z poprzedniego zadania bo okaza³ siê najszybszy)
	 * Znajduje wyst¹pienia dok³adne wzorca w tekœcie
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
				foundIndexes.add(j-1);
		}
		return foundIndexes;
		
	}
}
