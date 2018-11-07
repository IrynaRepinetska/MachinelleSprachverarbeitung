import java.io.*;

public class PubMed {

    public static void main(String[] args) throws Exception {

        String line;
        String words = "";
        String years = "";
        String journals = "";
        String mesh = "";
        String DescriptorName = "";
        String QualifierName = "";
        int document_count = 0;
        int year_count = 0;
        int word_count;
        int distinct_count;
        int mesh_count = 0;
        int output;
        int top;
        boolean found;
        String[] words_array;
        String[] journals_array;
        String[] years_array;
        String[] mesh_array;
        Token[] token_array;
        Token[] journals_distinct;
        Token[] years_distinct;
        Token[] mesh_distinct;
        MergeSort mergesort = new MergeSort();

        if (args.length != 1) {
            System.out.println("argument should be a folder");
            System.exit(1);
        }

        File folder = new File(args[0]);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {

            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                if (line.contains("<PubmedArticle>")) {
                    document_count++;
                } else if (line.contains("<ArticleTitle>")) {
                    while (!line.contains("</ArticleTitle>")) {
                        words = words + line;
                        line = br.readLine();
                    }
                    words = words + line;
                } else if (line.contains("</AbstractText>")) {
                    while (!line.contains("</AbstractText>")) {
                        words = words + line;
                        line = br.readLine();
                    }
                    words = words + line;
                } else if (line.contains("<Title>")) {
                    while (!line.contains("</Title>")) {
                        journals = journals + line;
                        line = br.readLine();
                    }
                    journals = journals + line;
                    ;
                } else if (line.contains("<PubDate>")) {
                    line = br.readLine();
                    if (line.contains("<Year>")) {
                        years = years + line;
                        year_count++;
                    }
                } else if (line.contains("<MeshHeading>")) {
                    line = br.readLine();
                    found = false;
                    while (!line.contains("</MeshHeading>")) {
                        if (line.contains("<DescriptorName")) {
                            DescriptorName = line.replaceAll("\\s*<DescriptorName[^>]*>([^<]*)</DescriptorName>", "$1");
                            line = br.readLine();
                        } else if (line.contains("<QualifierName")) {
                            QualifierName = line.replaceAll("\\s*<QualifierName[^>]*>([^<]*)</QualifierName>", "$1");
                            mesh = mesh + DescriptorName + "//" + QualifierName + "@@";
                            mesh_count++;
                            line = br.readLine();
                            found = true;
                        } else line = br.readLine();
                    }
                    if (found == false) {
                        mesh = mesh + DescriptorName + "@@";
                        mesh_count++;
                    }
                }
            }
        }
        // create an array of words
        words = words.replaceAll("<ArticleTitle>|</ArticleTitle>|<AbstractText[^>]*>|</AbstractText>", "").trim().toLowerCase();
        words_array = words.split("\\s+");
        word_count = words_array.length;
        // create an array of years and journals
        journals_array = journals.replaceAll("\\s*<Title>\\s*", "").split("\\s*</Title>\\s*");
        years_array = years.replaceAll("\\s*<Year>\\s*", "").split("\\s*</Year>\\s*");
        // create an array of meshHeadings
        mesh_array = mesh.split("@@");

        //count and sort words in the <ArticleTitle> and <AbstractText> Elements
        token_array = new Token[word_count];
        distinct_count = countToken(words_array, token_array, word_count);
        mergesort.sort(token_array, distinct_count);
        System.out.println("Artikel: " + document_count + "\n");
        System.out.println("Wörter: " + word_count + " (" + distinct_count + " distinct)");
        output = 50;
        if (output > distinct_count) {
            output = distinct_count;
        }
        top = printTokens(token_array, word_count, distinct_count, output);
        System.out.println("Top 50: " + top + " (" + String.format("%.2f", (double) top / word_count) + ")");
        System.out.println();

        //count and sort articles for each journal
        journals_distinct = new Token[document_count];
        distinct_count = countToken(journals_array, journals_distinct, document_count);
        mergesort.sort(journals_distinct, distinct_count);
        System.out.println("Journale: " + document_count + " (" + distinct_count + " distinct)");
        output = 10;
        if (output > distinct_count) {
            output = distinct_count;
        }
        top = printTokens(journals_distinct, document_count, distinct_count, output);
        System.out.println("Top 10: " + top + " (" + String.format("%.2f", (double) top / document_count) + ")");
        System.out.println();


        //count and sort articles for each year
        years_distinct = new Token[year_count];
        distinct_count = countToken(years_array, years_distinct, year_count);
        mergesort.sort(years_distinct, distinct_count);
        System.out.println("Years: " + year_count + " (" + distinct_count + " distinct)");
        output = 10;
        if (output > distinct_count) {
            output = distinct_count;
        }
        top = printTokens(years_distinct, year_count, distinct_count, output);
        System.out.println("Top 10: " + top + " (" + String.format("%.2f", (double) top / year_count) + ")");
        System.out.println();

        //count and sort meshHeadings
        mesh_distinct = new Token[mesh_count];
        distinct_count = countToken(mesh_array, mesh_distinct, mesh_count);
        mergesort.sort(mesh_distinct, distinct_count);
        System.out.println("MeSH: " + mesh_count + " (" + distinct_count + " distinct)");
        output = 10;
        if (output > distinct_count) {
            output = distinct_count;
        }
        top = printTokens(mesh_distinct, mesh_count, distinct_count, output);
        System.out.println("Top 10: " + top + " (" + String.format("%.2f", (double) top / mesh_count) + ")");
        System.out.println();
    }

    public static int countToken(String[] array, Token[] token_array, int length) {
        boolean found;
        int distinct_count = 0;
        for (int i = 0; i < length; i++) {
            found = false;
            for (int j = 0; j < distinct_count; j++) {
                if (token_array[j] != null && token_array[j].word.equals(array[i])) {
                    token_array[j].count++;
                    found = true;
                    break;
                }
            }
            if (found == false) {
                token_array[distinct_count] = new Token(array[i], 1);
                distinct_count++;
            }
        }
        return distinct_count;
    }

    public static int printTokens(Token[] token_array, int length, int distinct_count, int output) {
        int top = 0;
        for (int i = distinct_count - 1; i >= distinct_count - output; i--) {
            top = top + token_array[i].count;
            System.out.println(token_array[i].word + ": " + token_array[i].count + " (" + String.format("%.2f", (double) token_array[i].count / length) + ")");
        }
        return top;
    }
}
 