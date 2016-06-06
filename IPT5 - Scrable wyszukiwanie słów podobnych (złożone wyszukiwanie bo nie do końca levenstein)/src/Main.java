import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Main {
	public static final boolean DEBUG = true;
	public static final Charset CHARSET = Charset.forName("utf-8");
	public static final String WORDS_FILE = "slowa.txt";
	public static List<String> WORDS;
	public static String helper = "";
	public static long startTime;
	public static long stopTime;
	public static void initWords() throws IOException{
		WORDS = readLines(WORDS_FILE);
	}
	
	public static boolean existInWords(String word){
		return WORDS.contains(word.toLowerCase());
	}
	
	public static void main(String[] args) throws IOException {
		initWords();

		
		//losujemy s³ów 10 do testów wyszukiwania
		List<Integer> indeskyWyrazowDoTestow = new ArrayList<Integer>();
		Random generator = new Random();
		for(int i=0; i<10; i++) {
			int wylosowana = generator.nextInt(WORDS.size())+1;
			while(WORDS.get(wylosowana).length() < 5)
				wylosowana = generator.nextInt(WORDS.size())+1;
			indeskyWyrazowDoTestow.add(wylosowana);
		}
		
		//pobieramy z klawoatury 5 s³ów do testów poszukiwania
		List<String> wyrazyDoTestowUsera = new ArrayList<String>();
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner (System.in);
		System.out.println("Podaj wyrazy i zakoñcz \"qqq\"");
	    while (scanner.hasNext()) {
	    	String wyraz = scanner.nextLine();
	        if (wyraz.equals("qqq")){
	        	break;
	        }
	    	wyrazyDoTestowUsera.add(wyraz);
	    }
	    
	    
	    long czasLacznyAlg1 = 0;
	    long czasLacznyAlg2 = 0;
	    long czasLacznyAlg1DaneUsera = 0;
	    long czasLacznyAlg2DaneUsera = 0;
	    
	    for (String search : wyrazyDoTestowUsera){
	    	czasLacznyAlg1DaneUsera += algorytmZadanie1(search);
	    	czasLacznyAlg2DaneUsera += algorytmZadanie2(search);
	    }
	    List<Long> tablicaDoOdchyleniaAlg1 = new ArrayList<Long>();
	    List<Long> tablicaDoOdchyleniaAlg2 = new ArrayList<Long>();
	   
	    if (!DEBUG)
	    for (Integer searchIndex : indeskyWyrazowDoTestow){
	    	tablicaDoOdchyleniaAlg1.add(algorytmZadanie1(WORDS.get(searchIndex)));
	    	tablicaDoOdchyleniaAlg2.add(algorytmZadanie2(WORDS.get(searchIndex)));
	    }
	    if (!DEBUG)
	    for (int i=0; i<tablicaDoOdchyleniaAlg1.size(); i++){
	    	czasLacznyAlg1 += tablicaDoOdchyleniaAlg1.get(i);
	    	czasLacznyAlg2 += tablicaDoOdchyleniaAlg2.get(i);
	    }
	    
	    double sredniaAlg1 = czasLacznyAlg1/10;
	    double sredniaAlg2 = czasLacznyAlg2/10;
	    double odchylenieAlg1 = 0;
	    double odchylenieAlg2 = 0;
	    
	    if (!DEBUG)
	    for (int i=0; i<tablicaDoOdchyleniaAlg1.size(); i++){
	    	odchylenieAlg1 += Math.pow(tablicaDoOdchyleniaAlg1.get(i)-sredniaAlg1,2);
	    	odchylenieAlg2 += Math.pow(tablicaDoOdchyleniaAlg2.get(i)-sredniaAlg2,2);
	    }
	    if (!DEBUG)
	    odchylenieAlg1 =  Math.sqrt(odchylenieAlg1/tablicaDoOdchyleniaAlg1.size());
	    if (!DEBUG)
	    odchylenieAlg2 =  Math.sqrt(odchylenieAlg2/tablicaDoOdchyleniaAlg2.size());
	    
	    
	    System.out.println("Czas wykonania algorytmtm1 dla "+wyrazyDoTestowUsera.size()+" wyrazów z klawiatury: " + czasLacznyAlg1DaneUsera + "ms.");
	    System.out.println("Czas wykonania algorytmtm2 dla "+wyrazyDoTestowUsera.size()+" wyrazów z klawiatury: " + czasLacznyAlg2DaneUsera + "ms.");
	    
	    if (!DEBUG)
	    System.out.println("Czas wykonania algorytmtm1 dla "+tablicaDoOdchyleniaAlg1.size()+" wyrazów z tekstu: " + czasLacznyAlg1 
	    		+ "ms, czas œredni: "+sredniaAlg1+"ms, odchylenie: "+odchylenieAlg1+".");
	    if (!DEBUG)
	    System.out.println("Czas wykonania algorytmtm2 dla "+tablicaDoOdchyleniaAlg1.size()+" wyrazów z tekstu: " + czasLacznyAlg2
	    		+ "ms, czas œredni: "+sredniaAlg2+"ms, odchylenie: "+odchylenieAlg2+".");
	}
	
	public static long algorytmZadanie1(String search){
		helperCzyIstniejeDokladny(search);
		startTime = System.nanoTime();
    	for (String s : WORDS){
    		czySlowoNadajeSieDoZasad(existSimilarInWords(search,s), s, search);
    	}
    	stopTime = (System.nanoTime() - startTime) / 1000000;
    	System.out.println("Zadanie 1, s³owo: "+search+" Czas (ms): " + stopTime+helper);
    	return stopTime;
	}
	
	public static long algorytmZadanie2(String search){
		helperCzyIstniejeDokladny(search);
    	startTime = System.nanoTime();
    	for (String s : WORDS){
    		//przyspieszenie czy d³ogoœæ s³owa w ogóle odpowiada
    		if (search.length() > s.length()+1 || search.length() < s.length()-1 )
    			continue;
    		//przyspieszenie czy zawiera chocia¿ po³owê identyczn¹
    		if (!s.contains(search.substring(0,search.length()/2)) && !s.contains(search.substring(search.length()/2,search.length())))
    			continue;
    		czySlowoNadajeSieDoZasad(existSimilarInWords(search,s), s, search);
    	}
    	stopTime = (System.nanoTime() - startTime) / 1000000;
    	System.out.println("Zadanie 2, s³owo: "+search+" Czas (ms): " + stopTime+helper);
    	return stopTime;
	}
	
	public static void helperCzyIstniejeDokladny(String search){
    	if (existInWords(search))
    		helper = ", s³owo istnieje";
    	else 
    		helper = ", s³owo nie istnieje";
	}
	public static void czySlowoNadajeSieDoZasad(int wynikAlgorytmu, String slowo, String szukane){
    	if ((wynikAlgorytmu >= 0 && slowo.length()>5) || (wynikAlgorytmu == 0 && slowo.length() == 5))
    		if(DEBUG)
    			System.out.println("Podobne s³owo: " + slowo);	
		if(wynikAlgorytmu == 1 && slowo.length() == 5)
			if (existSimilarInWords(slowo,szukane) == 0)
				if(DEBUG)
					System.out.println("Podobne s³owo: " + slowo);
	}
	
	public static int existSimilarInWords(String pattern, String content) {
		int K=1;
		int previous = 0;
		int lowest = K+1;
		char[] patternChar = pattern.toCharArray();
		char[] contentChar = content.toCharArray();
		int dlugosc_tekstu = content.length();
		int dlugosc_wzorca = pattern.length();
		int p;
		int table[][] = new int[dlugosc_wzorca + 1][dlugosc_tekstu + 1];
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
			//pozwala wyleminowaæ s³owa podobne z przedrostkami np 
			//pobiadoli³a nie pasuje do s³owa biadoli³a
			if (j>0){
				if(table[dlugosc_wzorca][j] == table[dlugosc_wzorca][j-1]){
					if((j==0 || j == 1))
						previous++;
					previous++;
					}
				if(table[dlugosc_wzorca][j] > table[dlugosc_wzorca][j-1] &&  table[dlugosc_wzorca][j-1] !=0)
					previous++;
			}	
			//zapisuje czy w tekœcie znalaz³o siê dopasowanie dok³adne
			//np œnieg jest dopasowaniem dok³adnym œniegu
			//i mo¿na go wypisaæ bo jakby nie by³ to s³owa 5-znakowe s¹ pomijane
			if (lowest > table[dlugosc_wzorca][j])
				lowest = table[dlugosc_wzorca][j];
			if (previous > 2)
				return -1;
		}
		//je¿eli wzorzec zgodny lub podobny
		if (table[dlugosc_wzorca][dlugosc_tekstu] <= K){
			/*System.out.println("----------");
			for (int tt=0; tt<=dlugosc_tekstu; tt++)
				System.out.println(table[dlugosc_wzorca][tt]);
			*/
			return lowest;
		}
		return -1;
	}
	public static int minimum(int a, int b, int c) {
		if (a <= b && a <= c)
			return a;
		if (b <= a && b <= c)
			return b;
		return c;
	}
	public static List<String> readLines(String filename) throws IOException {
		return Files.readAllLines(Paths.get(filename), CHARSET);
	}
}
