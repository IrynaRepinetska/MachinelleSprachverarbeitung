import java.io.*;

public class PubMed {

	public static void main(String[] args) throws Exception {
		String line;
		String words = "";
        String years = "";
        String journals = "";
		int document_count = 0;
        int year_count = 0;
		int word_count;
		int distinct_count;
		int output;
		int top;
        String[] words_array;
        String[] journals_array;
        String[] years_array;
		Token[] token_array;
        Token[] journals_distinct;
        Token[] years_distinct;
		MergeSort mergesort = new MergeSort();
		
		
		File file = new File("/home/irina/Documents/WS18_19/Machinelle Sprachverarbeitung/assignment1-corpus/pubmed_7.xml");
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		while ((line = br.readLine()) != null) {
			if ( line.contains("<PubmedArticle>") ) {
				document_count++;
				}
			else if (line.contains("<ArticleTitle>") ) {
                while ( !line.contains("</ArticleTitle>") ) {
                    words = words + line;
                    line = br.readLine();
                }
                words = words + line;
            }
			else if ( line.contains("<Abstract>") ) {
				while ( !line.contains("</Abstract>") ) {
					words = words + line;
					line = br.readLine();
				}
				words = words + line;
				}
            else if ( line.contains("<Title>") ) {
                while ( !line.contains("</Title>") ) {
                    journals = journals + line;
                    line = br.readLine();
                }
                journals = journals + line;;
            }
            else if ( line.contains("<PubDate>") ) {
                line = br.readLine();
                if ( line.contains("<Year>") ) {
                    years = years + line;
                    year_count++;
                }
            }
			}
        // create an array of words in the <ArticleTitle> and <AbstractText> Elements
		words = words.replaceAll("<ArticleTitle>|</ArticleTitle>|<Abstract>|</Abstract>|<AbstractText[^>]*>|</AbstractText>","").trim().toLowerCase();
		words_array = words.split("\\s+");
		word_count = words_array.length;
        // create an array of years and journals
		journals_array = journals.replaceAll("\\s*<Title>\\s*", "").split("\\s*</Title>\\s*");
		years_array = years.replaceAll("\\s*<Year>\\s*", "").split("\\s*</Year>\\s*");

        System.out.println("Journals:");
        for (int i = 0; i < journals_array.length; i++) {
            System.out.println(i + " " + journals_array[i]);
        }
        System.out.println("Years:");
        for (int i = 0; i < years_array.length; i++) {
            System.out.println(i + " " + years_array[i]);
        }

        //count and sort words in the <ArticleTitle> and <AbstractText> Elements
        token_array = new Token[word_count];
        distinct_count = countToken(words_array, token_array, word_count);
		mergesort.sort(token_array, distinct_count);
		System.out.println("Artikel: " + document_count + "\n");
		System.out.println("Wörter: " + word_count + " (" + distinct_count + " distinct)");
        output = 50;
		if ( output > distinct_count ) {
			output = distinct_count;
		}
		top = printTokens(token_array, word_count, distinct_count, output);
		System.out.println("Top 50: " + top + " (" + String.format("%.2f", (double)top/word_count) + ")");
        System.out.println();

        //count and sort articles for each journal
        journals_distinct = new Token[document_count];
        distinct_count = countToken(journals_array, journals_distinct, document_count);
        mergesort.sort(journals_distinct, distinct_count);
        System.out.println("Journale: " + document_count + " (" + distinct_count + " distinct)");
        output = 10;
        if ( output > distinct_count ) {
            output = distinct_count;
        }
        top = printTokens(journals_distinct, document_count, distinct_count, output);
        System.out.println("Top 10: " + top + " (" + String.format("%.2f", (double)top/document_count) + ")");
        System.out.println();


        //count and sort articles for each year
        years_distinct = new Token[year_count];
        distinct_count = countToken(years_array, years_distinct, year_count);
        mergesort.sort(years_distinct, distinct_count);
        System.out.println("Years: " + year_count + " (" + distinct_count + " distinct)");
        output = 10;
        if ( output > distinct_count ) {
            output = distinct_count;
        }
        top = printTokens(years_distinct, year_count, distinct_count, output);
        System.out.println("Top 10: " + top + " (" + String.format("%.2f", (double)top/year_count) + ")");
        System.out.println();
    }

		public static int countToken (String[] array, Token[] token_array, int length) {
	    boolean found;
	    int distinct_count = 0;
            for (int i = 0; i < length; i++) {
                found = false;
                for ( int j = 0; j < distinct_count; j++) {
                    if ( token_array[j] !=  null && token_array[j].word.equals(array[i]) ) {
                        token_array[j].count++;
                        found = true;
                        break;
                    }
                }
                if (found == false) {
                    token_array[distinct_count] = new Token( array[i], 1);
                    distinct_count++;
                }
            }
            return  distinct_count;
        }

        public static int printTokens (Token [] token_array, int length, int distinct_count, int output) {
	    int top = 0;
            for (int i = distinct_count - 1; i >= distinct_count - output; i--) {
                top = top + token_array[i].count;
                System.out.println(token_array[i].word +": " + token_array[i].count + " (" + String.format("%.2f", (double)token_array[i].count/length) + ")");
            }
            return top;
        }
	}
 