package com.company;

import javax.swing.*;
import java.io.*;
import java.util.Scanner;

class IOManager {
    private static final File difficultySave = new File("src/main/resources/difficultySave.txt");
    private static final File usedWinsFile = new File("src/main/resources/usedWins.txt");
    private static final File unusedHintsFile = new File("src/main/resources/unusedHints.txt");
    private static final File statisticData = new File("src/main/resources/statisticData.txt");

    private static char difficultyChar;
    private static int difficultyIndex;
    private static final String[] statisticLines = new String[3];
    private static String[] modeStatistic = new String[5];

    private static int losses;
    private static final int[] modeWins = new int[3];

    private static final int[] usedWins = new int[3];
    private static final int[] unusedHints = new int[3];
    private static final int[] maxHintsCount = {1, 3, 5};

    static Difficulty readDifficultyFromFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(difficultySave))) {
            String line = br.readLine();
            difficultyChar = line.charAt(0);
        }
        switch (difficultyChar) {
            case 'B':
                return Difficulty.Beginner;
            case 'A':
                return Difficulty.Amateur;
            case 'P':
                return Difficulty.Professional;
        }
        return null;
    }

    static void readStatistic() {
        String[] statisticLine;
        try {
            Scanner statistic = new Scanner(statisticData);
            int i = 0;
            while (statistic.hasNextLine()) {
                statisticLines[i] = statistic.nextLine();
                statisticLine = statisticLines[i].split(" ");
                if (statisticLines[i].charAt(0) == difficultyChar) {
                    difficultyIndex = i;
                    modeStatistic = statisticLine;
                    losses = Integer.parseInt(modeStatistic[2]);
                }
                modeWins[i] = Integer.parseInt(statisticLine[4]);
                i++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void writeStatistic(JLabel gameOver) throws IOException {
        char over = gameOver.getText().charAt(4);
        if (over == 'l') {
            losses += 1;
            modeStatistic[2] = String.valueOf(losses);
        } else {
            modeWins[difficultyIndex] += 1;
            modeStatistic[4] = String.valueOf(modeWins[difficultyIndex]);
        }
        statisticLines[difficultyIndex] = String.join(" ", modeStatistic);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(statisticData))) {
            bw.write(String.join("\n", statisticLines));
        }
    }

    static void readHints() throws FileNotFoundException {
        Scanner used = new Scanner(usedWinsFile);
        Scanner unused = new Scanner(unusedHintsFile);
        int i = 0;
        while (used.hasNextLine()) {
            usedWins[i] = used.nextInt();
            unusedHints[i] = unused.nextInt();
            i++;
        }
    }

    static void setHint(int hintCount){
        unusedHints[difficultyIndex] = hintCount;
    }

    static int getHint() {
        return unusedHints[difficultyIndex];
    }

    static void writeDifficultyToFile(Difficulty difficulty) throws IOException {
        String difficultyString = difficulty.toString();
        difficultyChar = difficultyString.charAt(0);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(difficultySave))) {
            bw.write(difficultyString);
        }
        switch (difficultyChar) {
            case 'B':
                difficultyIndex = 0;
                break;
            case 'A':
                difficultyIndex = 1;
                break;
            case 'P':
                difficultyIndex = 2;
                break;
        }
    }

    static void writeHints() throws IOException {
        String[] used = new String[3];
        String[] unused = new String[3];
        int[] winsCount = {5, 3, 1};
        for (int i = 0; i < modeWins.length; i++) {
            used[i] = String.valueOf(usedWins[i]);
            unused[i] = String.valueOf(unusedHints[i]);
            int win = modeWins[i];
            if (win % winsCount[i] == 0) {
                if (usedWins[i] != win) {
                    if (unusedHints[i] < maxHintsCount[i]) {
                        unusedHints[i] += 1;
                        unused[i] = String.valueOf(unusedHints[i]);
                        used[i] = String.valueOf(win);
                    }
                }
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(usedWinsFile))) {
            bw.write(String.join("\n", used));
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(unusedHintsFile))) {
            bw.write(String.join("\n", unused));
        }
    }

}