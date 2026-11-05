package Assignment1_FileRegexSearcher;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * FileRegexSearcher
 *
 * Recursively searches through the user's home directory for files matching
 * a given regular expression against their file names.
 * Runs in a loop taking inputs repeatedly until 'exit' or 'quit' is entered.
 */
public class FileRegexSearcher {

    public static void main(String[] args) {
        String userHome = System.getProperty("user.home");
        if (userHome == null || userHome.isBlank()) {
            System.err.println("Error: Unable to locate the user's home directory.");
            return;
        }

        Path homePath = Paths.get(userHome);
        System.out.println("==================================================");
        System.out.println("  Home Directory Regex File Searcher");
        System.out.println("  Search Root: " + homePath.toAbsolutePath());
        System.out.println("  Type 'exit' or 'quit' to terminate.");
        System.out.println("==================================================");

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("\nEnter regex pattern to search: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                    System.out.println("Exiting searcher. Goodbye!");
                    break;
                }

                if (input.isEmpty()) {
                    System.out.println("Pattern cannot be empty. Please enter a valid regex.");
                    continue;
                }

                // Verify valid regex syntax before starting traversal
                try {
                    Pattern.compile(input);
                } catch (PatternSyntaxException e) {
                    System.err.println("Invalid regular expression syntax: " + e.getDescription());
                    continue;
                }

                System.out.println("Searching...");
                long start = System.currentTimeMillis();
                int matchesFound = searchFilesByRegex(homePath, input);
                long elapsed = System.currentTimeMillis() - start;

                System.out.printf("Found %d matching file(s) in %d ms.%n", matchesFound, elapsed);
            }
        }
    }

    /**
     * Walks the directory tree from rootDirectory and prints the absolute path
     * of every file matching regexPattern.
     */
    public static int searchFilesByRegex(Path rootDirectory, String regexPattern) {
        final int[] count = new int[1];
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("regex:" + regexPattern);

        try {
            Files.walkFileTree(rootDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    Path fileName = file.getFileName();
                    if (fileName != null && matcher.matches(fileName)) {
                        System.out.println(file.toAbsolutePath());
                        count[0]++;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    // Skip inaccessible / permission-denied directories gracefully
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Traversal error: " + e.getMessage());
        }

        return count[0];
    }
}