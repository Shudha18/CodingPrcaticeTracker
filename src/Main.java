import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        QuestionManager qm = new QuestionManager();
        StreakManager sm = new StreakManager();

        System.out.println("==============================================");
        System.out.println("||       CODING PRACTICE TRACKER            ||");
        System.out.println("==============================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice: ");
            String input = scanner.nextLine().trim();

            try {
                int choice = Integer.parseInt(input);
                switch (choice) {
                    case 1 -> qm.addQuestion(scanner);
                    case 2 -> qm.searchByTopic(scanner);
                    case 3 -> qm.searchByDifficulty(scanner);
                    case 4 -> qm.searchByTag(scanner);
                    case 5 -> qm.viewAllQuestions();
                    case 6 -> sm.showStreak();
                    case 7 -> sm.showDailyStats();
                    case 8 -> qm.viewSolution(scanner);
                    case 9 -> qm.deleteQuestion(scanner);
                    case 0 -> {
                        System.out.println("\n👋 Keep coding every day! Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("❌ Invalid choice. Please enter a number from the menu.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("⚠️  Something went wrong: " + e.getMessage());
            }

            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }

        scanner.close();
    }

    static void printMenu() {
        System.out.println("\n══════════════ MAIN MENU ══════════════");
        System.out.println("  1. Add New Question & Solution");
        System.out.println("  2. Search by Topic");
        System.out.println("  3. Search by Difficulty");
        System.out.println("  4. Search by Tag");
        System.out.println("  5. View All Questions");
        System.out.println("  6. View Current Streak");
        System.out.println("  7. Daily Stats & Progress");
        System.out.println("  8. View Solution for a Question");
        System.out.println("  9. Delete a Question");
        System.out.println("  0. Exit");
        System.out.println("═══════════════════════════════════════");
    }
}
