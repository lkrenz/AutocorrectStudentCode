import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Autocorrect
 * <p>
 * A command-line tool to suggest similar words when given one not in the dictionary.
 * </p>
 * @author Zach Blick
 * @author YOUR NAME HERE
 */
public class Autocorrect {

    String[] words;
    int threshold;
    /**
     * Constucts an instance of the Autocorrect class.
     * @param words The dictionary of acceptable words.
     * @param threshold The maximum number of edits a suggestion can have.
     */
    public Autocorrect(String[] words, int threshold) {
        this.words = words;
        this.threshold = threshold;
    }

    public int findEditDistance(String word1, String word2) {
        int[][] paths = new int[word1.length() +1][word2.length()+1];
        word1 = " " + word1;
        word2 = " " + word2;

        for (int j = 0; j < word2.length(); j++) {
            paths[0][j] = j;
        }
        for (int i = 1; i < word1.length(); i++) {
            paths[i][0] = i;
            for (int j = 1; j < word2.length(); j++) {
                if (word1.charAt(i) == word2.charAt(j)) {
                    paths[i][j] = paths[i - 1][j -1];
                }
                else {
                    paths[i][j] = Math.min(Math.min(paths[i-1][j], paths[i][j-1]), paths[i-1][j-1]) + 1;
                }
            }
        }

        return paths[word1.length() -1][word2.length() -1];
    }

    public class WordPair {
        String word;
        int editDistance;

        public WordPair(String word, int editDistance) {
            this.word = word;
            this.editDistance = editDistance;
        }
    }

    /**
     * Runs a test from the tester file, AutocorrectTester.
     * @param typed The (potentially) misspelled word, provided by the user.
     * @return An array of all dictionary words with an edit distance less than or equal
     * to threshold, sorted by edit distnace, then sorted alphabetically.
     */
    public String[] runTest(String typed) {
        ArrayList<WordPair> closeWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            int editDistance = findEditDistance(typed, this.words[i]);
            if (editDistance <= threshold) {
                placeWord(closeWords, new WordPair(words[i], editDistance));
            }
        }

        String[] cleanedWords = cleanWords(closeWords);
        System.out.println(Arrays.toString(cleanedWords));

        return cleanedWords;
    }

    public void placeWord(ArrayList<WordPair> words, WordPair word) {
        int index = 0;
        int length = words.size();
        while (index < length && words.get(index).editDistance != word.editDistance) {
            index++;
        }
        while (index < length && words.get(index).editDistance == word.editDistance && word.word.compareTo(words.get(index).word) < 0) {
            index++;
        }
        words.add(index, word);
    }

    public String[] cleanWords(ArrayList<WordPair> words) {
        String[] cleanedWords = new String[words.size()];
        for (int i = 0; i < cleanedWords.length; i++) {
            cleanedWords[i] = words.get(i).word;
        }
        return cleanedWords;
    }

    /**
     * Loads a dictionary of words from the provided textfiles in the dictionaries directory.
     * @param dictionary The name of the textfile, [dictionary].txt, in the dictionaries directory.
     * @return An array of Strings containing all words in alphabetical order.
     */
    private static String[] loadDictionary(String dictionary)  {
        try {
            String line;
            BufferedReader dictReader = new BufferedReader(new FileReader("dictionaries/" + dictionary + ".txt"));
            line = dictReader.readLine();

            // Update instance variables with test data
            int n = Integer.parseInt(line);
            String[] words = new String[n];

            for (int i = 0; i < n; i++) {
                line = dictReader.readLine();
                words[i] = line;
            }
            return words;
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}