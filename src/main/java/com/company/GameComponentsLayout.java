package com.company;

import javax.swing.*;
import java.awt.*;

class GameComponentsLayout extends JPanel {
    private final MineSweeper mineSweeper;
    private final int buttonSize;
    private final JButton[][] buttonArray;
    private final JPanel panel;

    GameComponentsLayout(MineSweeper mineSweeper) {
        this.mineSweeper = mineSweeper;
        this.buttonArray = mineSweeper.buttonArray;
        this.buttonSize = mineSweeper.buttonSize;

        JPanel hint = new JPanel();
        hint.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets.top = buttonSize;
        constraints.insets.right = buttonSize / 2;
        constraints.insets.bottom = buttonSize;

        hint.add(mineSweeper.hintButton, constraints);
        hint.add(mineSweeper.hintLabel);

        JPanel gameField = new JPanel();
        gameField.setLayout(new BorderLayout());
        gameField.add(this, BorderLayout.CENTER);

        createButtonField(gameField);

        JPanel labelPanel = new JPanel();

        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.LINE_AXIS));
        labelPanel.add(mineSweeper.bombLabel);
        int distance = 0;
        switch (mineSweeper.difficulty) {
            case Beginner:
                distance = 60;
                break;
            case Amateur:
                distance = 200;
                break;
            case Professional:
                distance = 400;
                break;
        }
        labelPanel.add(Box.createHorizontalStrut(distance));
        labelPanel.add(MineSweeper.timeLabel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(hint, BorderLayout.PAGE_START);
        panel.add(gameField);
        panel.add(labelPanel);
    }

    JPanel getPanel() {
        return panel;
    }

    void createButtonField(Container container) {
        container.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();

        for (int i = 0; i < buttonArray.length; i++) {
            for (int j = 0; j < buttonArray[0].length; j++) {
                constraints.insets.top = (i == 0) ? buttonSize / 2 : 0;
                constraints.insets.left = (j == 0) ? buttonSize : 0;
                constraints.insets.right = (j == mineSweeper.fieldLength - 1) ? buttonSize : 0;

                constraints.gridy = i;
                constraints.gridx = j;

                JButton button = new JButton() {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(buttonSize, buttonSize);
                    }
                };
                container.add(button, constraints);
                buttonArray[i][j] = button;
            }
        }
    }

}