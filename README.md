# Coding Practice Tracker 🚀

A terminal-based Java app to track your coding journey.

## Project Structure
```
CodingTracker/
├── src/
│   ├── Main.java              ← Entry point + menu
│   ├── Question.java          ← Question model
│   ├── QuestionManager.java   ← Add/search/delete questions
│   ├── StreakManager.java      ← Streak & daily stats
│   ├── FileHandler.java       ← All file I/O
│   └── InvalidInputException.java  ← Custom exception
├── CodingTracker.jar          ← Runnable JAR
└── README.md
```

## How to Compile & Run

### Option A – Run the JAR directly
```bash
java -jar CodingTracker.jar
```

### Option B – Compile from source
```bash
mkdir out
javac -d out src/*.java
java -cp out Main
```

## Data Storage
All data is saved in a `tracker_data/` folder created in the **same directory you run the JAR from**:
- `tracker_data/questions.txt`  — your question records
- `tracker_data/streak.txt`     — daily activity log
- `tracker_data/solutions/`     — one .txt file per solution

## Features
| Option | Feature |
|--------|---------|
| 1 | Add a question with title, topic, difficulty, tag, platform, notes, and full solution |
| 2 | Search by topic |
| 3 | Search by difficulty (Easy / Medium / Hard) |
| 4 | Search by tag (e.g. Two Pointer, BFS, DP) |
| 5 | View all questions |
| 6 | View current streak + last 7 days calendar |
| 7 | Daily stats — history bar chart, average, best day |
| 8 | View saved solution for any question by ID |
| 9 | Delete a question (with confirmation) |
