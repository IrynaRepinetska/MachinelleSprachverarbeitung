import java.io.*; 
import java.text.DecimalFormat;
import java.util.Arrays;

public class PubMed {

	public static void main(String[] args) throws Exception {
		String line;
		String words = "";
		int document_count = 0;
		int word_count = 0;
		int distinct_count = 0;
		int output = 50;
		int top_50 = 0;
		boolean found;
		String[] words_array;
		Token[] token_array;
		MergeSort mergesort = new MergeSort();
		
		
		File file = new File("/home/irina/Documents/WS18_19/Machinelle Sprachverarbeitung/assignment1-corpus/test.xml"); 
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		while ((line = br.readLine()) != null) {
			if ( line.contains("<PubmedArticle>") ) {
				document_count++;
				}
			else if (line.contains("<ArticleTitle>") ) {
				if (words.length() == 0) {
					words = line;
					}
				else {
					words = words + line;
					}
		        }
			else if ( line.contains("<Abstract>") ) {
				while ( !line.contains("</Abstract>") ) {
					words = words + line;
					line = br.readLine();
				}
				words = words + line;
				}
			}
		words = words.replaceAll("<ArticleTitle>|</ArticleTitle>|<Abstract>|</Abstract>|<AbstractText[^>]*>|</AbstractText>","").trim().toLowerCase();
		words_array = words.split("\\s+");
		word_count = words_array.length;
		
		token_array = new Token[word_count];
		for (int i = 0; i < word_count; i++) {
			found = false;
			for ( int j = 0; j < distinct_count; j++) {
				if ( token_array[j] !=  null && token_array[j].word.equals(words_array[i]) ) {
					 token_array[j].count++;
					 found = true;
					 break;
				}
			}
			if (found == false) {
				token_array[distinct_count] = new Token( words_array[i], 1);
				distinct_count++;
			}
    	}

		mergesort.sort(token_array, distinct_count);

		System.out.println("Artikel: " + document_count + "\n");
		System.out.println("Wörter: " + word_count + " (" + distinct_count + " distinct)");
		if ( output > distinct_count ) {
			output = distinct_count;
		}
		for (int i = distinct_count - 1; i >= distinct_count - output; i--) {
			top_50 = top_50 + token_array[i].count;
			System.out.println(token_array[i].word +": " + token_array[i].count + " (" + String.format("%.2f", (double)token_array[i].count/word_count) + ")");
			}
		System.out.println("Top 50: " + top_50 + " (" + String.format("%.2f", (double)top_50/word_count) + ")");
		
		}
	}
 