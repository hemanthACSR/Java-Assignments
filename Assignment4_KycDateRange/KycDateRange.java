package Assignment4_KycDateRange;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class KycDateRange {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Computes the allowable KYC date range based on the signup date and current date.
     *
     * @param signupDate  Date the customer registered.
     * @param currentDate Current reference date.
     * @return Formatted date range "dd-MM-yyyy dd-MM-yyyy" or "No range".
     */
    public static String getKycRange(LocalDate signupDate, LocalDate currentDate) {
        // If current date is strictly before the signup date, no anniversary exists.
        if (currentDate.isBefore(signupDate)) {
            return "No range";
        }

        // Project signup day and month to the current date's year
        LocalDate anniversary = signupDate.withYear(currentDate.getYear());

        // Calculate the allowable window: anniversary - 30 days to anniversary + 30 days
        LocalDate windowStart = anniversary.minusDays(30);
        LocalDate windowEnd = anniversary.plusDays(30);

        // If the current date hasn't yet reached this year's window, check the previous year's anniversary
        if (currentDate.isBefore(windowStart)) {
            anniversary = anniversary.minusYears(1);
            windowStart = anniversary.minusDays(30);
            windowEnd = anniversary.plusDays(30);
        }

        // If the anniversary year equals or precedes the signup year, no full anniversary has elapsed yet
        if (anniversary.getYear() <= signupDate.getYear()) {
            return "No range";
        }

        // You cannot use a future date for the form date: cap the end date at currentDate
        if (currentDate.isBefore(windowEnd)) {
            windowEnd = currentDate;
        }

        return windowStart.format(FORMATTER) + " " + windowEnd.format(FORMATTER);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) {
            scanner.close();
            return;
        }

        int n = scanner.nextInt();
        for (int i = 0; i < n; i++) {
            String signupStr = scanner.next();
            String currentStr = scanner.next();

            LocalDate signupDate = LocalDate.parse(signupStr, FORMATTER);
            LocalDate currentDate = LocalDate.parse(currentStr, FORMATTER);

            System.out.println(getKycRange(signupDate, currentDate));
        }
        scanner.close();
    }
}