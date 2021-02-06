package com.company;

import javax.swing.*;
import java.io.IOException;

public class MineMenu {
    private final MineSweeper mineSweeper;
    private final JFrame mineFrame;

    public MineMenu(MineSweeper mineSweeper) {
        this.mineSweeper = mineSweeper;
        this.mineFrame = mineSweeper.mineFrame;

        JMenuBar menuBar = new JMenuBar();
        mineFrame.setJMenuBar(menuBar);
        JMenu gameMenu = new JMenu("Game");
        menuBar.add(gameMenu);

        JMenuItem newGameAction = new JMenuItem("New game");
        gameMenu.add(newGameAction);
        JMenuItem difficultyAction = new JMenu("Difficulty");
        gameMenu.add(difficultyAction);
        gameMenu.addSeparator();
        JMenuItem exitAction = new JMenuItem("Exit");
        gameMenu.add(exitAction);

        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem beginner = new JRadioButtonMenuItem("Beginner", true);
        difficultyAction.add(beginner);
        group.add(beginner);
        JRadioButtonMenuItem amateur = new JRadioButtonMenuItem("Amateur");
        difficultyAction.add(amateur);
        group.add(amateur);
        JRadioButtonMenuItem professional = new JRadioButtonMenuItem("Professional");
        difficultyAction.add(professional);
        group.add(professional);

        newGameAction.addActionListener(e -> {
            try {
                menuItemListener(mineSweeper.difficulty);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        beginner.addActionListener(e -> {
            try {
                menuItemListener(Difficulty.Beginner);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        amateur.addActionListener(e -> {
            try {
                menuItemListener(Difficulty.Amateur);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        professional.addActionListener(e -> {
            try {
                menuItemListener(Difficulty.Professional);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        exitAction.addActionListener(e -> System.exit(0));
    }

    void menuItemListener(Difficulty difficulty) throws IOException {
        mineFrame.getContentPane().removeAll();
        IOManager.writeDifficultyToFile(difficulty);
        mineSweeper.startNewGame();
        mineFrame.revalidate();
        mineFrame.pack();
    }

}