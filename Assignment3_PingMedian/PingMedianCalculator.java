package Assignment3_PingMedian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PingMedianCalculator {

    // Regex pattern matching ping latency outputs across Linux, macOS, and Windows:
    // Linux/macOS: "time=14.2 ms" or "time=14.2ms"
    // Windows: "time=14ms" or "time<1ms"
    private static final Pattern PING_TIME_PATTERN = 
            Pattern.compile("time[=<]([0-9]+(?:\\.[0-9]+)?)\\s*ms", Pattern.CASE_INSENSITIVE);

    /**
     * Pings the specified host a given number of times using the OS native ping utility
     * and computes the median round-trip time in milliseconds.
     *
     * @param host  The target IP address or hostname to ping.
     * @param count The number of ping packets to send.
     * @return OptionalDouble containing the median RTT in milliseconds, or empty if ping fails.
     * @throws IOException          If an I/O error occurs starting the process.
     * @throws InterruptedException If the process execution is interrupted.
     */
    public static OptionalDouble calculateMedianPing(String host, int count) 
            throws IOException, InterruptedException {
        
        if (host == null || host.isBlank() || count <= 0) {
            throw new IllegalArgumentException("Host must be non-empty and count must be positive.");
        }

        // Detect OS to pass proper command arguments
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        List<String> command = new ArrayList<>();
        command.add("ping");

        if (os.contains("win")) {
            command.add("-n"); // Windows count flag
            command.add(String.valueOf(count));
        } else {
            command.add("-c"); // Linux / macOS count flag
            command.add(String.valueOf(count));
        }
        command.add(host);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        List<Double> rttList = new ArrayList<>();

        // Read ping output stream
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = PING_TIME_PATTERN.matcher(line);
                if (matcher.find()) {
                    double rtt = Double.parseDouble(matcher.group(1));
                    rttList.add(rtt);
                }
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 && rttList.isEmpty()) {
            return OptionalDouble.empty();
        }

        if (rttList.isEmpty()) {
            return OptionalDouble.empty();
        }

        // Sort to determine the median value
        Collections.sort(rttList);
        int size = rttList.size();
        double median;

        if (size % 2 == 1) {
            median = rttList.get(size / 2);
        } else {
            median = (rttList.get((size / 2) - 1) + rttList.get(size / 2)) / 2.0;
        }

        return OptionalDouble.of(median);
    }

    public static void main(String[] args) {
        String targetHost = "8.8.8.8";
        int packetCount = 5;

        try {
            System.out.printf("Pinging %s %d times...%n", targetHost, packetCount);
            OptionalDouble median = calculateMedianPing(targetHost, packetCount);

            if (median.isPresent()) {
                System.out.printf("Median Ping Time: %.2f ms%n", median.getAsDouble());
            } else {
                System.out.println("Host unreachable or no response packets received.");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ping execution failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}