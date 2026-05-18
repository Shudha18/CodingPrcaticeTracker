import java.time.LocalDate;

public class Question {
    private int id;
    private String title;
    private String topic;
    private String difficulty;  // Easy, Medium, Hard
    private String tag;         // e.g. Array, DP, Graph, etc.
    private String platform;    // LeetCode, HackerRank, etc.
    private String date;        // yyyy-MM-dd
    private String notes;

    public Question(int id, String title, String topic, String difficulty,
                    String tag, String platform, String date, String notes) {
        this.id = id;
        this.title = title;
        this.topic = topic;
        this.difficulty = difficulty;
        this.tag = tag;
        this.platform = platform;
        this.date = date;
        this.notes = notes;
    }

    // Parse from a CSV-like line stored in file
    // Format: id|title|topic|difficulty|tag|platform|date|notes
    public static Question fromFileLine(String line) throws IllegalArgumentException {
        String[] parts = line.split("\\|", -1);
        if (parts.length != 8) {
            throw new IllegalArgumentException("Corrupt record: " + line);
        }
        return new Question(
            Integer.parseInt(parts[0].trim()),
            parts[1].trim(),
            parts[2].trim(),
            parts[3].trim(),
            parts[4].trim(),
            parts[5].trim(),
            parts[6].trim(),
            parts[7].trim()
        );
    }
     
    // Serialize to file line 
    public String toFileLine() {
        return id + "|" + title + "|" + topic + "|" + difficulty + "|"
                + tag + "|" + platform + "|" + date + "|" + notes;
    }

    public String getSolutionFileName() {
        return "solution_" + id + ".txt";
    }

    // Getters 
    // using all Method to retun 
    public int getId()           { return id; }
    public String getTitle()     { return title; }
    public String getTopic()     { return topic; }
    public String getDifficulty(){ return difficulty; }
    public String getTag()       { return tag; }
    public String getPlatform()  { return platform; }
    public String getDate()      { return date; }
    public String getNotes()     { return notes; }

    @Override
    public String toString() {
        String diff = switch (difficulty.toLowerCase()) {
            case "easy"   -> "🟢 Easy";
            case "medium" -> "🟡 Medium";
            case "hard"   -> "🔴 Hard";
            default       -> difficulty;
        };
        return String.format(
            "  [#%d] %s\n       Topic: %s | Difficulty: %s | Tag: %s\n       Platform: %s | Date: %s\n       Notes: %s",
            id, title, topic, diff, tag, platform, date,
            notes.isEmpty() ? "-" : notes
        );
    }
}
