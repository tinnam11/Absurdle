import java.util.*;
import java.io.*;

// This class is a program containing the game called Absurdle
// where users are able to pick a dictionary file that they would
// like to play from. Users will continue to make guesses of the word
// as the program tries to prolong the game by making the possibility of
// words as large as possible from the guess that the user has made. The
// program will end once the user has made the correct guess.

public class Main  {
    public static final String GREEN = "🟩";
    public static final String YELLOW = "🟨";
    public static final String GRAY = "⬜";

    public static void main(String[] args) throws FileNotFoundException {
        Scanner console = new Scanner(System.in);
        System.out.println("Welcome to the game of Absurdle.");

        System.out.print("What dictionary would you like to use? ");
        String dictName = console.next();

        System.out.print("What length word would you like to guess? ");
        int wordLength = console.nextInt();

        List<String> contents = loadFile(new Scanner(new File(dictName)));
        Set<String> words = pruneDictionary(contents, wordLength);

        List<String> guessedPatterns = new ArrayList<>();
        while (!isFinished(guessedPatterns)) {
            System.out.print("> ");
            String guess = console.next();
            String pattern = record(guess, words, wordLength);
            guessedPatterns.add(pattern);
            System.out.println(": " + pattern);
            System.out.println();
        }
        System.out.println("Absurdle " + guessedPatterns.size() + "/∞");
        System.out.println();
        printPatterns(guessedPatterns);
    }

    // Prints out the given list of patterns.
    // - List<String> patterns: list of patterns from the game
    public static void printPatterns(List<String> patterns) {
        for (String pattern : patterns) {
            System.out.println(pattern);
        }
    }

    // Returns true if the game is finished, meaning the user guessed the word. Returns
    // false otherwise.
    // - List<String> patterns: list of patterns from the game
    public static boolean isFinished(List<String> patterns) {
        if (patterns.isEmpty()) {
            return false;
        }
        String lastPattern = patterns.get(patterns.size() - 1);
        return !lastPattern.contains("⬜") && !lastPattern.contains("🟨");
    }

    // Loads the contents of a given file Scanner into a List<String> and returns it.
    // - Scanner dictScan: contains file contents
    public static List<String> loadFile(Scanner dictScan) {
        List<String> contents = new ArrayList<>();
        while (dictScan.hasNext()) {
            contents.add(dictScan.next());
        }
        return contents;
    }

    // the method takes in a list of words from a specified dictionary, as well
    // as the word length specified by the user. The method will filter words
    // of specified length by the user and get rid of any duplicate words in the
    // dictionary and adds it to the set.
    // Parameters:
    //    - contents (List<String>) - the words contained in the dictionary that will be used to
    //                     add to the set
    //    - wordLength (int) - the word length that will be used as a parameter to filter
    //                         out words from content and add it to the set.
    // Returns: this method will return a Set<String> containing the words from the
    //          dictionary that matches the word length that the user inputs. The Set
    //          should not contain any duplicate words. The method would also throw
    //          IllegalArgumentException if the word length that the user inputs
    //          is less than 1.
    public static Set<String> pruneDictionary(List<String> contents, int wordLength) {
        if (wordLength < 1) {
            throw new IllegalArgumentException("The given wordLength is less than 1");
        }
        Set<String> finalContents = new TreeSet<String>();
        Iterator<String> itr = contents.iterator();
        while (itr.hasNext()) {
            String word = itr.next();
            if (word.length() == wordLength) {
                finalContents.add(word);
            }
        }
        return finalContents;
    }

    // this method finds the most common pattern containing the largest
    // possibility of words from the setthat allows for the least
    // amount of pruning done to the dictionary. It takes in the guess from
    // the user, the set of words, and the word length. The method creates a new map,
    // containing the pattern as the key and the values of the words containing the pattern.
    // the method also calls on other methods like patternFor and findMostCommonPattern in
    // order to return the string of the most common pattern.
    // Parameters:
    //    - guess (String) - the word guessed by the user
    //    - wordLength (int) - the word length that the guess needs to contain
    //    - words (Set<String>) - the set containing of possible words that has
    //                            already been pruned from the dictionary
    // Returns: this method will return a String containing the most common pattern for users
    //          to visualize in the console. The following method will throw an
    //          IllegalArgumentException if the set of words is empty, or if the guess
    //          made by the user does not match the specified wordLength
    public static String record(String guess, Set<String> words, int wordLength) {
        if (words.isEmpty()) {
            throw new IllegalArgumentException("The set of words is empty ");
        }
        if (guess.length() != wordLength) {
            throw new IllegalArgumentException("The guess does not have the correct length");
        }
        Map<String, Set<String>> target = new TreeMap<String, Set<String>>();
        for (String getWord : words) {
            String word = getWord;
            String getGuess = patternFor(word, guess);
            if (!target.containsKey(getGuess)) {
                Set<String> patternWords = new HashSet<>();
                patternWords.add(word);
                target.put(getGuess, patternWords); //adding words to the target map
            } else {
                target.get(getGuess).add(word);
            }
        }
        String mostCommonPattern = findMostCommonPattern(target, words);
        return mostCommonPattern;
    }

    //the following method iterates through the target map and finds the largest set size
    // which will contain the words with the most common pattern. The method returns the key of
    // the most common pattern and updates the set of words with the values from the set with the
    // most common pattern.
    // Parameters:
    //    - target (Map<String, Set<String>>) - the target map which will be used to find the most common
    //                                          pattern from key in this map
    //    - words (Set<String>) - set containing the possible words that would be updated after the method
    //                            has been called
    // Returns: this method will return a String containing the most common pattern for users
    //          to visualize in the console. The following String would be used in the record method
    //          above.
    public static String findMostCommonPattern(Map<String, Set<String>> target, Set<String> words) {
        String mostCommonPattern = "";
        int size = 0;
        for (String key : target.keySet()) {
            Set<String> value = target.get(key);
            if (value.size() > size) { //finds the key with the largest size
                size = value.size();
                mostCommonPattern = key;
            }
        }
        words.clear();
        words.addAll(target.get(mostCommonPattern)); //updates the set
        return mostCommonPattern;
    }

    // the following method creates a pattern for each of the words in the set and the guess made
    // by the user. The pattern is created by assigning grey, yellow and green tiles.
    // The following pattern for each of the word would later on be used to determine the
    // most common pattern to prune the number of possibility down.
    // Parameters:
    //    - word (String) - word contained in the set which will be used to compare against the guess to create a pattern
    //    - guess (String) - guess made by the user, used to create the pattern
    // Returns: The method returns a String pattern for each of words within the set against the user's guess. The method
    //          would be called in method to later on find the most common pattern.
    public static String patternFor(String word, String guess) {
        List<String> pattern = new ArrayList<>();
        Map<Character, Integer> counts = new HashMap<>();
        for (int i = 0; i < word.length(); i++) { // iterates through the word to character count
            char c = word.charAt(i);
            if (counts.containsKey(c)) {
                counts.put(c, counts.get(c) + 1);
            } else {
                counts.put(c, 1);
            }
            pattern.add(String.valueOf(c));
        }
        for (int i = 0; i < guess.length(); i++) { //assigning exact matches
            char c = guess.charAt(i);
            if (word.charAt(i) == c) {
                pattern.set(i, "🟩"); //mark green tile for exact match
                counts.put(c, counts.get(c) - 1); //updates the character count in the map
            }
        }
        for (int i = 0; i < guess.length(); i++) { // assigning approximate matches
            char c = guess.charAt(i);
            if (!pattern.get(i).equals("🟩")) { //if no exact matches
                if (counts.containsKey(c) && counts.get(c) > 0) {
                    pattern.set(i, "🟨");
                    counts.put(c, counts.get(c) - 1);
                } else {
                    pattern.set(i, "⬜"); //mark rest of the pattern with gray tiles
                }
            }
        }
        String result = "";
        for (String s : pattern) {
            result += s; //concatenates the elements from pattern
        }
        return result;
    }
}