import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

public class StreakManager {

    // Called whenever a question is added; logs the date
    public static void logActivity(String date) {
        try {
            List<String> lines = FileHandler.readStreakLines();
            // Avoid duplicate entry for same day
            for (String l : lines) {
                if (l.startsWith(date)) {
                    // Already has an entry for today, increment count
                    List<String> updated = new ArrayList<>();
                    for (String entry : lines) {
                        if (entry.startsWith(date)) {
                            String[] parts = entry.split("\\|");
                            int count = (parts.length > 1) ? Integer.parseInt(parts[1].trim()) + 1 : 2;
                            updated.add(date + "|" + count);
                        } else {
                            updated.add(entry);
                        }
                    }
                    FileHandler.writeAllStreakEntries(updated);
                    return;
                }
            }
            // New day entry
            FileHandler.appendStreakEntry(date + "|1");
        } catch (IOException e) {
            System.out.println("⚠️  Could not log activity: " + e.getMessage());
        }
    }

    // ─── SHOW STREAK ────────────────────────────────────────────────────────────
    public void showStreak() {
        System.out.println("\n╔══════════ CODING STREAK 🔥 ══════════╗");
        try {
            Map<LocalDate, Integer> activityMap = loadActivity();
            if (activityMap.isEmpty()) {
                System.out.println("  📭 No activity recorded yet.");
                return;
            }

            LocalDate today = LocalDate.now();
            int currentStreak = 0;
            int longestStreak = 0;
            int tempStreak = 0;
            LocalDate check = today;

            // Current streak: count backwards from today
            while (activityMap.containsKey(check)) {
                currentStreak++;
                check = check.minusDays(1);
            }

            // Longest streak: iterate all sorted dates
            List<LocalDate> sortedDates = new ArrayList<>(activityMap.keySet());
            Collections.sort(sortedDates);
            LocalDate prev = null;
            for (LocalDate d : sortedDates) {
                if (prev == null || d.equals(prev.plusDays(1))) {
                    tempStreak++;
                } else {
                    tempStreak = 1;
                }
                if (tempStreak > longestStreak) longestStreak = tempStreak;
                prev = d;
            }

            int totalDays = activityMap.size();
            int totalQuestions = activityMap.values().stream().mapToInt(i -> i).sum();

            System.out.println("  🔥 Current Streak  : " + currentStreak + " day(s)");
            System.out.println("  🏆 Longest Streak  : " + longestStreak + " day(s)");
            System.out.println("  📅 Total Active Days: " + totalDays);
            System.out.println("  ✅ Total Questions  : " + totalQuestions);

            // Show last 7 days mini calendar
            System.out.println("\n  ── Last 7 Days ──");
            for (int i = 6; i >= 0; i--) {
                LocalDate d = today.minusDays(i);
                String symbol = activityMap.containsKey(d) ? "🟢" : "⬜";
                int count = activityMap.getOrDefault(d, 0);
                String label = (i == 0) ? " (today)" : "";
                System.out.printf("  %s %s %s%s%n", symbol, d,
                        count > 0 ? "— " + count + " question(s)" : "", label);
            }

        } catch (IOException e) {
            System.out.println("  ❌ Could not read streak data: " + e.getMessage());
        }
    }

    // ─── DAILY STATS ────────────────────────────────────────────────────────────
    public void showDailyStats() {
        System.out.println("\n╔══════════ DAILY PROGRESS 📊 ═════════╗");
        try {
            Map<LocalDate, Integer> activityMap = loadActivity();
            if (activityMap.isEmpty()) {
                System.out.println("  📭 No data yet. Add some questions first!");
                return;
            }

            // Questions per day for all recorded days
            List<LocalDate> sortedDates = new ArrayList<>(activityMap.keySet());
            Collections.sort(sortedDates);

            int total = activityMap.values().stream().mapToInt(i -> i).sum();
            double avg = (double) total / sortedDates.size();

            System.out.printf("  📈 Average per day : %.1f question(s)%n", avg);
            System.out.println("  📅 Active Days     : " + sortedDates.size());
            System.out.println("  ✅ Total Questions : " + total);

            System.out.println("\n  ── Full History ──");
            for (LocalDate d : sortedDates) {
                int count = activityMap.get(d);
                String bar = "█".repeat(Math.min(count, 20));
                System.out.printf("  %s | %s %d%n", d, bar, count);
            }

            // Best day
            LocalDate bestDay = Collections.max(activityMap.entrySet(),
                    Map.Entry.comparingByValue()).getKey();
            System.out.println("\n  🏅 Best Day: " + bestDay + " (" + activityMap.get(bestDay) + " questions)");

        } catch (IOException e) {
            System.out.println("  ❌ Could not read stats: " + e.getMessage());
        }
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────────

    private Map<LocalDate, Integer> loadActivity() throws IOException {
        Map<LocalDate, Integer> map = new LinkedHashMap<>();
        List<String> lines = FileHandler.readStreakLines();
        for (String line : lines) {
            try {
                String[] parts = line.split("\\|");
                LocalDate date = LocalDate.parse(parts[0].trim());
                int count = (parts.length > 1) ? Integer.parseInt(parts[1].trim()) : 1;
                map.put(date, count);
            } catch (DateTimeParseException | NumberFormatException e) {
                // Skip bad lines silently
            }
        }
        return map;
    }
}
