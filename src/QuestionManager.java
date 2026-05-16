import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class QuestionManager {

    public QuestionManager() {
        try {
            FileHandler.initDirectories();
        } catch (IOException e) {
            System.out.println("⚠️  Could not initialize data directories: " + e.getMessage());
        }
    }

    // ─── ADD QUESTION ───────────────────────────────────────────────────────────
    public void addQuestion(Scanner scanner) {
        System.out.println("\n╔══════════ ADD NEW QUESTION ══════════╗");

        try {
            System.out.print("  Question Title/Name: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) throw new InvalidInputException("Title cannot be empty.");

            System.out.print("  Topic (e.g. Arrays, Trees, DP): ");
            String topic = scanner.nextLine().trim();
            if (topic.isEmpty()) throw new InvalidInputException("Topic cannot be empty.");

            String difficulty = chooseDifficulty(scanner);

            System.out.print("  Tag (e.g. Two Pointer, BFS, Sliding Window): ");
            String tag = scanner.nextLine().trim();
            if (tag.isEmpty()) tag = "General";

            System.out.print("  Platform (e.g. LeetCode, HackerRank, Codeforces): ");
            String platform = scanner.nextLine().trim();
            if (platform.isEmpty()) platform = "Other";

            System.out.print("  Notes / Approach (optional, press Enter to skip): ");
            String notes = scanner.nextLine().trim();
            // Sanitize pipe characters to avoid breaking file format
            notes = notes.replace("|", ";");
            title = title.replace("|", ";");

            String date = LocalDate.now().toString();
            int id = FileHandler.getNextId();

            Question q = new Question(id, title, topic, difficulty, tag, platform, date, notes);
            FileHandler.appendQuestion(q.toFileLine());

            // Now get solution
            System.out.println("\n  ✏️  Enter your solution/code below.");
            System.out.println("  (Type END on a new line when done)");
            StringBuilder solution = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).equalsIgnoreCase("END")) {
                solution.append(line).append("\n");
            }

            if (!solution.isEmpty()) {
                FileHandler.writeSolution(q.getSolutionFileName(), solution.toString());
            }

            // Log streak entry
            StreakManager.logActivity(date);

            System.out.println("\n  ✅ Question #" + id + " saved successfully!");

        } catch (InvalidInputException e) {
            System.out.println("  ❌ Input Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("  ❌ File Error: " + e.getMessage());
        }
    }

    private String chooseDifficulty(Scanner scanner) throws InvalidInputException {
        System.out.println("  Difficulty: 1) Easy  2) Medium  3) Hard");
        System.out.print("  Enter choice (1/2/3): ");
        String d = scanner.nextLine().trim();
        return switch (d) {
            case "1" -> "Easy";
            case "2" -> "Medium";
            case "3" -> "Hard";
            default  -> throw new InvalidInputException("Invalid difficulty choice.");
        };
    }

    // ─── SEARCH BY TOPIC ────────────────────────────────────────────────────────
    public void searchByTopic(Scanner scanner) {
        System.out.println("\n╔══════════ SEARCH BY TOPIC ══════════╗");
        System.out.print("  Enter topic to search: ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        searchAndPrint(q -> q.getTopic().toLowerCase().contains(keyword), "topic: " + keyword);
    }

    // ─── SEARCH BY DIFFICULTY ───────────────────────────────────────────────────
    public void searchByDifficulty(Scanner scanner) {
        System.out.println("\n╔═══════ SEARCH BY DIFFICULTY ════════╗");
        System.out.println("  1) Easy  2) Medium  3) Hard");
        System.out.print("  Enter choice: ");
        String choice = scanner.nextLine().trim();
        String diff = switch (choice) {
            case "1" -> "easy";
            case "2" -> "medium";
            case "3" -> "hard";
            default  -> "";
        };
        if (diff.isEmpty()) { System.out.println("  ❌ Invalid choice."); return; }
        searchAndPrint(q -> q.getDifficulty().equalsIgnoreCase(diff), "difficulty: " + diff);
    }

    // ─── SEARCH BY TAG ──────────────────────────────────────────────────────────
    public void searchByTag(Scanner scanner) {
        System.out.println("\n╔══════════ SEARCH BY TAG ════════════╗");
        System.out.print("  Enter tag (e.g. BFS, DP, Binary Search): ");
        String keyword = scanner.nextLine().trim().toLowerCase();
        searchAndPrint(q -> q.getTag().toLowerCase().contains(keyword), "tag: " + keyword);
    }

    // ─── VIEW ALL QUESTIONS ─────────────────────────────────────────────────────
    public void viewAllQuestions() {
        System.out.println("\n╔══════════ ALL QUESTIONS ════════════╗");
        try {
            List<Question> questions = loadAllQuestions();
            if (questions.isEmpty()) {
                System.out.println("  📭 No questions added yet. Start by adding one!");
                return;
            }
            System.out.println("  Total: " + questions.size() + " question(s)\n");
            for (Question q : questions) {
                System.out.println(q);
                System.out.println("  ─────────────────────────────────────");
            }
        } catch (IOException e) {
            System.out.println("  ❌ Could not read questions: " + e.getMessage());
        }
    }

    // ─── VIEW SOLUTION ──────────────────────────────────────────────────────────
    public void viewSolution(Scanner scanner) {
        System.out.println("\n╔══════════ VIEW SOLUTION ════════════╗");
        System.out.print("  Enter Question ID: ");
        String input = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(input);
            Question q = findById(id);
            if (q == null) {
                System.out.println("  ❌ No question found with ID #" + id);
                return;
            }
            System.out.println("\n  Question: " + q.getTitle());
            System.out.println("  ─────────────────────────────────────");
            String solution = FileHandler.readSolution(q.getSolutionFileName());
            System.out.println(solution);
        } catch (NumberFormatException e) {
            System.out.println("  ❌ Please enter a valid number.");
        } catch (FileNotFoundException e) {
            System.out.println("  ⚠️  " + e.getMessage());
        } catch (IOException e) {
            System.out.println("  ❌ File Error: " + e.getMessage());
        }
    }

    // ─── DELETE QUESTION ────────────────────────────────────────────────────────
    public void deleteQuestion(Scanner scanner) {
        System.out.println("\n╔══════════ DELETE QUESTION ══════════╗");
        System.out.print("  Enter Question ID to delete: ");
        String input = scanner.nextLine().trim();
        try {
            int id = Integer.parseInt(input);
            List<String> allLines = FileHandler.readAllQuestionLines();
            boolean found = false;
            List<String> remaining = new ArrayList<>();

            for (String line : allLines) {
                try {
                    Question q = Question.fromFileLine(line);
                    if (q.getId() == id) {
                        found = true;
                        System.out.println("  Found: " + q.getTitle());
                        System.out.print("  ⚠️  Are you sure you want to delete? (yes/no): ");
                        String confirm = scanner.nextLine().trim();
                        if (confirm.equalsIgnoreCase("yes")) {
                            FileHandler.deleteSolution(q.getSolutionFileName());
                            System.out.println("  ✅ Question #" + id + " deleted.");
                        } else {
                            remaining.add(line);
                            System.out.println("  ↩️  Deletion cancelled.");
                        }
                    } else {
                        remaining.add(line);
                    }
                } catch (IllegalArgumentException e) {
                    remaining.add(line); // Keep unparseable lines
                }
            }

            if (!found) {
                System.out.println("  ❌ No question with ID #" + id + " found.");
                return;
            }
            FileHandler.writeAllQuestions(remaining);

        } catch (NumberFormatException e) {
            System.out.println("  ❌ Please enter a valid number.");
        } catch (IOException e) {
            System.out.println("  ❌ File Error: " + e.getMessage());
        }
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────────

    private void searchAndPrint(java.util.function.Predicate<Question> filter, String label) {
        try {
            List<Question> questions = loadAllQuestions();
            List<Question> results = new ArrayList<>();
            for (Question q : questions) {
                if (filter.test(q)) results.add(q);
            }
            if (results.isEmpty()) {
                System.out.println("  🔍 No questions found for " + label);
                return;
            }
            System.out.println("  Found " + results.size() + " result(s) for " + label + ":\n");
            for (Question q : results) {
                System.out.println(q);
                System.out.println("  ─────────────────────────────────────");
            }
        } catch (IOException e) {
            System.out.println("  ❌ File Error: " + e.getMessage());
        }
    }

    public List<Question> loadAllQuestions() throws IOException {
        List<String> lines = FileHandler.readAllQuestionLines();
        List<Question> questions = new ArrayList<>();
        for (String line : lines) {
            try {
                questions.add(Question.fromFileLine(line));
            } catch (IllegalArgumentException e) {
                System.out.println("  ⚠️  Skipping corrupt record: " + e.getMessage());
            }
        }
        return questions;
    }

    private Question findById(int id) throws IOException {
        for (Question q : loadAllQuestions()) {
            if (q.getId() == id) return q;
        }
        return null;
    }
}
