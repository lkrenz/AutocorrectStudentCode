import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

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

    // Finds the edit distance between two input strings through tabulation
    public int findEditDistance(String word1, String word2) {
        int[][] paths = new int[word1.length() +1][word2.length()+1];
        word1 = " " + word1;
        word2 = " " + word2;

        // Tabulates edit distance between word1 and word2
        for (int j = 0; j < word2.length(); j++) {
            paths[0][j] = j;
        }
        for (int i = 1; i < word1.length(); i++) {
            paths[i][0] = i;
            for (int j = 1; j < word2.length(); j++) {
                // Equal chars corresponds to no necessary edits
                // Other checks find the optimal path between substitution, addition, and subtraction
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

    // Class for holding word and edit distance for use in ordering
    public class WordPair {
        String word;
        int editDistance;

        public WordPair(String word, int editDistance) {
            this.word = word;
            this.editDistance = editDistance;
        }
    }

    // Class that contains methods for different types of pre edit distance removal
    public static class DictionaryChopper {
        String[] dict;
        String word;

        // Constructs a DictionaryChopper object given an initial dictionary and a word
        public DictionaryChopper(String[] dict, String word) {
            this.dict = dict;
            this.word = word;
        }

        // Returns the preened array using a tokenization approach
        public ArrayList<String> tokenizationArray(int n, int threshold) {

            // Finds the tokens
            String[] chops = tokenizeChop(word, n);

            // If length of each chop is longer than the word, returns null
            if (chops == null) {
                return null;
            }

            ArrayList<String> words = new ArrayList<>();

            ArrayList<String> newDict = new ArrayList<>();
            int wordLength = word.length();

            // Checks if the difference in word length between the misspelled word and candidate is > than the threshold
            for (int i = 0; i < dict.length; i++) {
                if (Math.abs(wordLength - dict[i].length()) <= threshold) {

                    // Adds words where the length difference is less than the threshold
                    newDict.add(dict[i]);
                }
            }

            // Adds words that contain the segment and removes them from dict to handle duplicates
            for (int i = 0; i < chops.length; i++) {
                for (int j = 0; j < newDict.size(); j++) {
                    if (containsSegment(newDict.get(j), chops[i])) {
                        words.add(newDict.remove(j));
                        j--;
                    }
                }
            }
            return words;
        }

        // If the segment is in the word, returns true
        public boolean containsSegment(String word, String segment) {
            if (segment.length() > word.length()) {
                return false;
            }

            int segLength = segment.length();

            // Iterates through word and compares to each segment
            for (int i = 0; i + segLength < word.length(); i++) {
                if (word.substring(i, i + segLength).compareTo(segment) == 0) {
                    return true;
                }
            }
            return false;
        }

        // Iterates through word and returns an array of substrings of inputted length
        public String[] tokenizeChop(String word, int length) {
            ArrayList<String> chops = new ArrayList<>();
            if (word.length() < length) {
                return null;
            }
            for (int i = 0; i + length < word.length(); i++) {
                chops.add(word.substring(i, i + length));
            }
            return chops.toArray(new String[0]);
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

        DictionaryChopper chopper = new DictionaryChopper(this.words, typed);

        // Basic edit distance approach
        String[] newDict = this.words;

        // Approach with tokenization
//        String[] newDict = chopper.tokenizationArray(2, threshold).toArray(new String[0]);



        // Iterates through dict and finds all words with edit distance less than the threshold
        // Returns words in order of edit distance and then alphabetical
        for (int i = 0; i < newDict.length; i++) {
            int editDistance = findEditDistance(typed, newDict[i]);
            if (editDistance <= threshold) {
                placeWord(closeWords, new WordPair(newDict[i], editDistance));
            }
        }

        return cleanWords(closeWords);
    }

    // Places the input word in the ArrayList based on edit distance and then alphabetical order
    public void placeWord(ArrayList<WordPair> words, WordPair word) {
        int index = 0;
        int length = words.size();

        // Iterates until the edit distance of the current index is less than or equal to the new word
        while (index < length && words.get(index).editDistance < word.editDistance) {
            index++;
        }
        // Places the new word in alphabetical order within words of the same edit distance
        while (index < length && words.get(index).editDistance == word.editDistance && word.word.compareTo(words.get(index).word) > 0) {
            index++;
        }
        words.add(index, word);
    }

    // Returns an array of strings given an ArrayList of WordPairs
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

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println("Input a word: ");
            String word = s.nextLine();

            Autocorrect corrector = new Autocorrect(loadDictionary("large"), 1);
            String[] answers = corrector.runTest(word);
            System.out.println("You typed: " + word);
            if (answers.length > 0) {
                System.out.println("Did you mean ...");
                for (int i = 0; i < answers.length; i++) {
                    System.out.println(answers[i]);
                }
            }
            else {
                System.out.println("No matches found.");
            }
            System.out.println("~~~~~~~~~~~~~~");
        }
    }
}