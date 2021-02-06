package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.*;
import java.util.Timer;

class MineSweeper {
    JFrame mineFrame;
    int buttonSize = 18;
    JButton[][] buttonArray;
    JButton hintButton;
    JLabel hintLabel;
    JLabel bombLabel;

    int fieldLength;
    int fieldWidth;
    int bombCount;
    Difficulty difficulty;

    static JLabel timeLabel;
    static Timer timer;

    static MyHint myHint;

    static void createStopwatch() {
        try {
            timer.schedule(new UpdateUITask(), 0, 1000);
        } catch (IllegalStateException e) {
            e.getMessage();
        }
    }

    MineSweeper() throws IOException {
        createGameFrame();
        new MineMenu(this);

        timeLabel = new JLabel();
        timeLabel.setIcon(new ImageIcon("src/main/resources/buttons/timer.jpg"));
        bombLabel = new JLabel();
        bombLabel.setIcon(new ImageIcon("src/main/resources/buttons/bombCount.jpg"));
        hintButton = new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(buttonSize, buttonSize);
            }
        };
        Icon question = new ImageIcon("src/main/resources/buttons/question.jpg");
        hintButton.setIcon(question);
        hintLabel = new JLabel();

        IOManager.readHints();
        startNewGame();
        mineFrame.pack();
    }

    void createGameFrame() {
        mineFrame = new JFrame("Сапёрка");
        mineFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mineFrame.setLocationRelativeTo(null);
        mineFrame.setResizable(false);
        mineFrame.setVisible(true);
    }

    void startNewGame() throws IOException {
        difficulty = IOManager.readDifficultyFromFile();
        IOManager.readStatistic();

        timer = new Timer();
        setGameParameters();

        timeLabel.setText("0");
        bombLabel.setText(String.valueOf(bombCount));

        GameComponentsLayout components = new GameComponentsLayout(this);

        GamePlay gamePlay = new GamePlay(this);
        myHint = new MyHint(gamePlay, this);
        myHint.hintAction();

        gamePlay.buttonsAction();

        mineFrame.add(components.getPanel());
    }

    void gameOver(String gameOverLabel) throws IOException {
        try {
            timer.cancel();
        } catch (IllegalStateException e){
            e.getMessage();
        }

        JLabel gameOver = new JLabel(gameOverLabel);
        IOManager.writeDifficultyToFile(difficulty);
        IOManager.writeStatistic(gameOver);
        IOManager.writeHints();

        JDialog gameOverFrame = new JDialog(mineFrame, true);
        gameOverFrame.setLocation(mineFrame.getLocation().x + 50, mineFrame.getLocation().y + 50);
        gameOverFrame.setPreferredSize(new Dimension(150, 100));
        gameOverFrame.setResizable(false);

        gameOver.setVerticalAlignment(JLabel.CENTER);
        gameOver.setHorizontalAlignment(JLabel.CENTER);
        gameOver.setText("<html>" + gameOver.getText() + "<br> " + timeLabel.getText() + " сек</html>");
        gameOverFrame.getContentPane().add(gameOver);

        gameOverFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                mineFrame.getContentPane().removeAll();
                try {
                    startNewGame();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                mineFrame.revalidate();
                mineFrame.pack();
            }
        });
        gameOverFrame.pack();
        gameOverFrame.setVisible(true);
    }

    void setGameParameters() {
        switch (difficulty) {
            case Beginner:
                fieldWidth = 9;
                fieldLength = 9;
                bombCount = 10;
                break;
            case Amateur:
                fieldWidth = 16;
                fieldLength = 16;
                bombCount = 40;
                break;
            case Professional:
                fieldWidth = 16;
                fieldLength = 30;
                bombCount = 99;
                break;
        }
        buttonArray = new JButton[fieldWidth][fieldLength];
    }


    private static class UpdateUITask extends TimerTask {
        int nSeconds = 0;

        @Override
        public void run() {
            EventQueue.invokeLater(() -> timeLabel.setText(String.valueOf(nSeconds++)));
        }
    }

    public static void main(String[] args) throws IOException {
        new MineSweeper();
    }
}