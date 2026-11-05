package Assignment2_PangramChecker;

public class PangramChecker {

    /**
     * Checks if the given string contains all letters of the English alphabet (a-z),
     * ignoring case.
     *
     * Time Complexity: O(n)
     *   - We iterate through the string of length n at most once.
     *   - Each character check and bitwise operation takes O(1) time.
     *   - Early termination occurs as soon as all 26 letters are found.
     *
     * Space Complexity: O(1)
     *   - Uses a single 32-bit integer as a bitmask to track seen letters.
     *   - No additional dynamic memory or collections are allocated.
     *
     * @param str the input string to check
     * @return true if the string is a pangram, false otherwise
     */
    public static boolean containsAllLetters(String str) {
        if (str == null || str.length() < 26) {
            return false;
        }

        // A bitmask where each bit 0-25 represents the presence of 'a'-'z'.
        int seenMask = 0;
        
        // Target mask with the lowest 26 bits set to 1: (1 << 26) - 1 = 0x03FFFFFF
        final int targetMask = (1 << 26) - 1;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);

            // Map uppercase and lowercase English letters to index 0-25
            if (ch >= 'a' && ch <= 'z') {
                seenMask |= (1 << (ch - 'a'));
            } else if (ch >= 'A' && ch <= 'Z') {
                seenMask |= (1 << (ch - 'A'));
            }

            // Early exit if all 26 letters have been seen
            if (seenMask == targetMask) {
                return true;
            }
        }

        return seenMask == targetMask;
    }

    public static void main(String[] args) {
        String test1 = "The quick brown fox jumps over the lazy dog";
        String test2 = "Hello, World!";
        String test3 = "Pack my box with five dozen liquor jugs.";

        System.out.println("\"" + test1 + "\" is pangram: " + containsAllLetters(test1)); // true
        System.out.println("\"" + test2 + "\" is pangram: " + containsAllLetters(test2)); // false
        System.out.println("\"" + test3 + "\" is pangram: " + containsAllLetters(test3)); // true
    }
}