import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String DATA_DIR = "tracker_data";
    private static final String QUESTIONS_FILE = DATA_DIR + "/questions.txt";
    private static final String SOLUTIONS_DIR = DATA_DIR + "/solutions";
    private static final String STREAK_FILE = DATA_DIR + "/streak.txt";

    // Initialize folders on first run
    public static void initDirectories() throws IOException {
        Files.createDirectories(Paths.get(DATA_DIR));
        Files.createDirectories(Paths.get(SOLUTIONS_DIR));
        if (!Files.exists(Paths.get(QUESTIONS_FILE))) {
            Files.createFile(Paths.get(QUESTIONS_FILE));
        }
        if (!Files.exists(Paths.get(STREAK_FILE))) {
            Files.createFile(Paths.get(STREAK_FILE));
        }
    }

    // Read all question lines from file
    public static List<String> readAllQuestionLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(QUESTIONS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        }
        return lines;
    }

    // Append a new question line
    public static void appendQuestion(String line) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(QUESTIONS_FILE, true))) {
            bw.write(line);
            bw.newLine();
        }
    }

    // Overwrite all questions (used for delete)
    public static void writeAllQuestions(List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(QUESTIONS_FILE, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // Write solution file
    public static void writeSolution(String fileName, String content) throws IOException {
        String path = SOLUTIONS_DIR + "/" + fileName;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path, false))) {
            bw.write(content);
        }
    }

    // Read solution file
    public static String readSolution(String fileName) throws IOException {
        String path = SOLUTIONS_DIR + "/" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException("No solution file found for this question.");
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    // Delete solution file
    public static void deleteSolution(String fileName) {
        File file = new File(SOLUTIONS_DIR + "/" + fileName);
        if (file.exists()) file.delete();
    }

    // Read streak file lines
    public static List<String> readStreakLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(STREAK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) lines.add(line);
            }
        }
        return lines;
    }

    // Append to streak file
    public static void appendStreakEntry(String entry) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STREAK_FILE, true))) {
            bw.write(entry);
            bw.newLine();
        }
    }

    // Overwrite streak file (used when updating counts)
    public static void writeAllStreakEntries(List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(STREAK_FILE, false))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }

    // Get next question ID
    public static int getNextId() throws IOException {
        List<String> lines = readAllQuestionLines();
        int maxId = 0;
        for (String line : lines) {
            try {
                int id = Integer.parseInt(line.split("\\|")[0].trim());
                if (id > maxId) maxId = id;
            } catch (Exception ignored) {}
        }
        return maxId + 1;
    }
}
