package com.company;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MyHint {
    private final GamePlay game;
    private final JFrame mineFrame;
    private final JButton[][] buttonArray;
    private final JButton hintButton;
    private final JLabel hintLabel;
    private int hintCount;

    boolean cursor;

    MyHint(GamePlay game, MineSweeper mineSweeper) {
        this.game = game;
        this.mineFrame = mineSweeper.mineFrame;
        this.buttonArray = mineSweeper.buttonArray;
        this.hintButton = mineSweeper.hintButton;
        this.hintLabel = mineSweeper.hintLabel;
    }

    void hintAction() {
        hintCount = IOManager.getHint();
        hintButton.setEnabled(hintCount > 0);
        hintLabel.setText(String.valueOf(hintCount));
        hintButton.addActionListener(e -> {
            cursor = true;
            mineFrame.setCursor(new Cursor(Cursor.HAND_CURSOR));
            hintCount -= 1;
            hintLabel.setText(String.valueOf(hintCount));
            IOManager.setHint(hintCount);
            if (hintCount == 0) {
                hintButton.setEnabled(false);
            }
        });
    }

    void leftMouseClickListener(int x, int y) {
        if (buttonArray[y][x].getIcon() == game.flag) {
            buttonArray[y][x].setEnabled(true);
        } else {
            if (buttonArray[y][x].getDisabledIcon() == game.bomb) {
                if (cursor) {
                    game.rightMouseClickListener(x, y);
                    cursor = false;
                }
            } else if (buttonArray[y][x].getDisabledIcon() != null) {
                buttonArray[y][x].setEnabled(false);
                buttonArray[y][x].setIcon(buttonArray[y][x].getDisabledIcon());
                try {
                    game.gameOverCheck();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            } else {
                game.disableHintsButtons(x, y);
                buttonArray[y][x].setEnabled(false);
                buttonArray[y][x].setIcon(buttonArray[y][x].getDisabledIcon());
            }
            mineFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            cursor = false;
        }
    }

}