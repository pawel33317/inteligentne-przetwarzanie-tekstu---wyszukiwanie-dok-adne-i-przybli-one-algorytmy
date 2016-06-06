package zad4;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.omg.Messaging.SyncScopeHelper;

public class Main2 {

	public static char wejscie[];
	public static List<String> listaWzorcow;
	public static final Charset CHARSET = Charset.forName("ISO-8859-1");
	public static final boolean DEBUG = true;
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
	public static final String FILE_DEBUG = "aaa";
	public static final int ASIZE = 1024;

	public static void main(String[] args) throws IOException {

		List<String> mainFiles = new ArrayList<String>();
		if (DEBUG) {
			mainFiles.add(FILE_DEBUG);
		} else {
			mainFiles.add(FILE_DNA);
			mainFiles.add(FILE_ENGLISH);
			mainFiles.add(FILE_PROTEINS);
		}
		List<String> patternsList = new ArrayList<String>();
		if (DEBUG) {
			patternsList.add(FILE_PATTERN4);
		} else {
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
		}

		int HPWIN = 0;
		int SOWIN = 0;
		int BFWIN = 0;
		int SUFWIN = 0;

		long startTime;
		long HPtime;
		long BFtime;
		long SOtime;
		long SUFtime;
		long SUFtime_SEARCH_ONLY;

		int trafieniaHP;
		int trafieniaBF;
		int trafieniaSO;
		int trafieniaSUF;

		for (String mainFile : mainFiles) {// dla kazdego pliku g³ównego: dna,
			// english, proteins
			System.out.println("PLIK: " + mainFile);
			for (String patternFile : patternsList) {// dla kazdego wzorca:
				// 4,8,4rev...
				System.out.println("   PATTERN: " + patternFile);

				if (DEBUG) {
					System.out.println("START");
					System.out.println("Wczytujê pliki");
				}

				String mainFileContent = readFile(mainFile + FILE_MAIN);
				List<String> paternFileContent = readLines(mainFile + patternFile);

				////////////////////////////////////////////////////////////////////
				////////////// KOD G£ÓWNY DLA SUFFIX
				//////////////////////////////////////////////////////////////////// TABLE//////////////////////////
				////////////////////////////////////////////////////////////////////
				wejscie = mainFileContent.toCharArray();
				if (DEBUG) {
					System.out.println("Wczyta³em pliki");
					System.out.println("Robiê obiekty sufiksów");
				}
				startTime = System.nanoTime();
				sufix suffuxTable[] = new sufix[wejscie.length];
				for (int i = 0; i < wejscie.length; i++) {
					suffuxTable[i] = new sufix(i);
				}
				if (DEBUG) {
					System.out.println("Zrobi³em obiekty sufixów");
					for (int i = 0; i < wejscie.length; i++) {
						System.out.println(String.valueOf(wejscie).substring(suffuxTable[i].value, wejscie.length));
					}
					System.out.println("Zaczynam sortowanie");
				}

				Arrays.sort(suffuxTable);

				if (DEBUG) {
					System.out.println("Skoñczy³em sortowanie");
					System.out.println("Poszukujê wzorców");
					for (int i = 0; i < wejscie.length; i++) {
						System.out.println(i + "-->" + String.valueOf(wejscie).substring(suffuxTable[i].value, wejscie.length));
					}
				}
				long startTimeSearchOnly = System.nanoTime();
				trafieniaSUF = 0;
				// zaczynamy szukaæ od pocz¹tku do koñca i co iteracje
				// zmniejszamy zakres o po³owê
				int startSearch = 0;
				int stopSearch = wejscie.length - 1;
				// aktualne miejsce poszukiwania to œrodek zakresu
				int currentSearch = (stopSearch + startSearch) / 2;
				int previousCurrentSearch = 0;

				// je¿eli aktualne miejsce wyszukiwania i poprzedenie s¹ takie
				// same
				// to znaczy, ¿e nie znaleziono i stanê³o w miejscu
				while (previousCurrentSearch != currentSearch) {

					// ograniczenie porównywania liter je¿eli ci¹g wzorca
					// wiêkszy od
					// porównywanego tekstu
					int ileLiterSprawdzicMaksymalnie = ileLiterSprawdzicMaksymalnieFun(patternFile.length(), suffuxTable[currentSearch].value);
					int wynikPorownania = czyWzorzecJestRowny(patternFile, ileLiterSprawdzicMaksymalnie, suffuxTable[currentSearch].value);
					System.out.println(wynikPorownania);
					// Trafienie
					if (wynikPorownania == 0) {
						trafieniaSUF++;
						// SprawdŸ czy kolejne elementy w tablicy te¿ nie
						// zawieraj¹
						// tego podci¹gu
						int tmpCurrentSearch = currentSearch + 1;
						int tmp2CurrentSearch = currentSearch - 1;
						while (tmpCurrentSearch < wejscie.length && czyWzorzecJestRowny(patternFile, ileLiterSprawdzicMaksymalnieFun(patternFile.length(), suffuxTable[tmpCurrentSearch].value), suffuxTable[tmpCurrentSearch].value) == 0) {
							trafieniaSUF++;
							tmpCurrentSearch++;
						}
						while (tmp2CurrentSearch >= 0 && czyWzorzecJestRowny(patternFile, ileLiterSprawdzicMaksymalnieFun(patternFile.length(), suffuxTable[tmp2CurrentSearch].value), suffuxTable[tmp2CurrentSearch].value) == 0) {
							trafieniaSUF++;
							tmp2CurrentSearch--;
						}
						break;
					}
					// zagnie¿d¿a o po³owê
					else if (wynikPorownania == 1)
						startSearch = currentSearch;
					else
						stopSearch = currentSearch;

					previousCurrentSearch = currentSearch;
					currentSearch = (stopSearch + startSearch) / 2;
				}
				SUFtime = (System.nanoTime() - startTime) / 1000000;
				SUFtime_SEARCH_ONLY = (System.nanoTime() - startTimeSearchOnly) / 1000000;
				// BRAK TRAFIENIA

				////////////////////////////////////////////////////////////////////
				////////////// KONIEC KOD G£ÓWNY DLA SUFFIX
				//////////////////////////////////////////////////////////////////// TABLE////////////////////
				////////////////////////////////////////////////////////////////////

				////////////////////////////////////////////////////////////
				///////// STARE ALGORYTMY Z ZADANIA 2 I PODSUMOWANIE/////////
				////////////////////////////////////////////////////////////
				startTime = System.nanoTime();
				trafieniaBF = 0;
				for (String pattern : paternFileContent) {
					trafieniaBF += BF(pattern, mainFileContent).size();
				}
				BFtime = (System.nanoTime() - startTime) / 1000000;
				startTime = System.nanoTime();
				trafieniaHP = 0;
				for (String pattern : paternFileContent) {
					trafieniaHP += HORSPOOL(pattern, mainFileContent).size();
				}
				HPtime = (System.nanoTime() - startTime) / 1000000;
				startTime = System.nanoTime();
				trafieniaSO = 0;
				for (String pattern : paternFileContent) {
					trafieniaSO += SO(pattern, mainFileContent).size();
				}
				SOtime = (System.nanoTime() - startTime) / 1000000;

				if (HPtime < BFtime && HPtime < SOtime && HPtime < SUFtime) {
					HPWIN++;
				} else if (BFtime < HPtime && BFtime < SOtime && BFtime < SUFtime) {
					BFWIN++;
				} else if (SOtime < BFtime && SOtime < HPtime && SOtime < SUFtime) {
					SOWIN++;
				} else if (SUFtime < BFtime && SUFtime < HPtime && SUFtime < SOtime) {
					SUFWIN++;
				}
				System.out.format("      CZAS >>> SUF_search_only: %d, SUF: %d, HP: %d, BF %d, SO %d\n", SUFtime_SEARCH_ONLY, SUFtime, HPtime, BFtime, SOtime);
				System.out.format("      TRAFIENIA >>>SUF: %d, HP: %d, BF %d, SO %d\n", trafieniaSUF, trafieniaHP, trafieniaBF, trafieniaSO);
			}
		}
		System.out.format("Wygrane: HP-%d, BF-%d, SO-%d, SUF-%d\n", HPWIN, BFWIN, SOWIN, SUFWIN);

		if (DEBUG)
			System.out.println("KONIEC PROGRAMU");
	}

	/*
	 * Sprzwdza tyle liter ile ma wzorzec lub wszystkie litery od danego indeksu
	 * je¿eli jest ich mniej ni¿ d³ugoœæ wzorca
	 */
	public static int ileLiterSprawdzicMaksymalnieFun(int dlugoscWzorca, int indexPozycjiWciaguDoPoszukiwan) {
		int ileLiterSprawdzicMaksymalnie = dlugoscWzorca - 1;
		if (czyWzorzecJestDluzszyOdTekstu(ileLiterSprawdzicMaksymalnie, indexPozycjiWciaguDoPoszukiwan))
			ileLiterSprawdzicMaksymalnie = wejscie.length - indexPozycjiWciaguDoPoszukiwan;
		return ileLiterSprawdzicMaksymalnie;
	}

	public static boolean czyWzorzecJestDluzszyOdTekstu(int dlugoscWzorca, int startTekstu) {
		if (wejscie.length - startTekstu < dlugoscWzorca)
			return true;
		return false;
	}

	/**
	 * Czy wzorzec jest wiêkszy mniejszy lub równy np aa<bg<z -1 mniejszy 0
	 * równy 1 wiêkszy
	 */
	public static int czyWzorzecJestRowny(String wzorzec, int dlugoscSprawdzania, int pozycjaWtekscie) {
		for (int i = 0; i <= dlugoscSprawdzania; i++) {
			if (wzorzec.charAt(i) > wejscie[pozycjaWtekscie + i])
				return 1;
			if (wzorzec.charAt(i) < wejscie[pozycjaWtekscie + i])
				return -1;
		}
		return 0;
	}

	/**
	 * klasa s³u¿y do porównywania czêœci tablicy char[] z inn¹ jej czêœci¹[]
	 * gdzie value okreœla miejsce od którego zaczyna siê porównanie
	 */
	public static class sufix implements Comparable<sufix> {
		public int value;

		public sufix(int a) {
			value = a;
		}

		@Override
		public int compareTo(sufix s) {
			for (int i = this.value, i2 = s.value; i < wejscie.length - 1 && i2 < wejscie.length - 1; i++, i2++) {
				if (wejscie[i] == wejscie[i2])
					continue;
				if (wejscie[i] < wejscie[i2])
					return -1;
				else
					return 1;
			}
			if (this.value < s.value)
				return 1;
			else
				return -1;
		}
	}

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

	// algorytmy wyszukiwañ z zadania 2 do porównania
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
