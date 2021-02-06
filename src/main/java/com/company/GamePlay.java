package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.*;

class GamePlay {
    private final MineSweeper mineSweeper;
    private final JButton[][] buttonArray;
    private ButtonCoordinate[] bombArray;
    private final JLabel bombLabel;
    private final int fieldLength;
    private final int fieldWidth;
    private final int bombCount;

    final Icon num1 = new ImageIcon("src/main/resources/buttons/1.jpg");
    final Icon num2 = new ImageIcon("src/main/resources/buttons/2.jpg");
    final Icon num3 = new ImageIcon("src/main/resources/buttons/3.jpg");
    final Icon num4 = new ImageIcon("src/main/resources/buttons/4.jpg");
    final Icon num5 = new ImageIcon("src/main/resources/buttons/5.jpg");
    final Icon num6 = new ImageIcon("src/main/resources/buttons/6.jpg");
    final Icon num7 = new ImageIcon("src/main/resources/buttons/7.jpg");
    final Icon num8 = new ImageIcon("src/main/resources/buttons/8.jpg");
    final Icon bomb = new ImageIcon("src/main/resources/buttons/bomb.jpg");
    final Icon explosion = new ImageIcon("src/main/resources/buttons/explosion.jpg");
    final Icon flag = new ImageIcon("src/main/resources/buttons/flag.jpg");

    GamePlay(MineSweeper mineSweeper) {
        this.mineSweeper = mineSweeper;
        this.buttonArray = mineSweeper.buttonArray;
        this.bombLabel = mineSweeper.bombLabel;
        this.fieldLength = mineSweeper.fieldLength;
        this.fieldWidth = mineSweeper.fieldWidth;
        this.bombCount = mineSweeper.bombCount;

        bombGenerator();
        setIcons(bombArray);
    }

    private void bombGenerator() {
        int lastCellCoordinate = fieldLength * fieldWidth;
        Set<Integer> bombCells = new TreeSet<>();
        bombArray = new ButtonCoordinate[bombCount];
        int i = 0;
        while(i < bombCount) {
            int num = randomBombCoordinate(lastCellCoordinate);
            if(bombCells.add(num)) {
                setBombCoordinate(i, num);
                i++;
            }
        }
        System.out.println(bombCells);
    }

    private int randomBombCoordinate(int lastCellCoordinate) {
        return (int) (Math.random() * lastCellCoordinate);
    }

    private void setBombCoordinate(int i, int cellNum) {
        ButtonCoordinate buttonCoordinate = new ButtonCoordinate().coordinateConverter(cellNum, fieldLength);
        int x = buttonCoordinate.getX();
        int y = buttonCoordinate.getY();
        buttonArray[y][x].setDisabledIcon(bomb);
        bombArray[i] = buttonCoordinate;
    }

    private void setIcons(ButtonCoordinate[] bombArray) {
        ArrayList<ButtonCoordinate> aroundBombCoordinates = new ArrayList<>(8);
        ArrayList<ButtonCoordinate> aroundNumsCoordinates = new ArrayList<>(8);
        for (ButtonCoordinate bombCell : bombArray) {
            getAroundCells(bombCell.getX(), bombCell.getY(), aroundBombCoordinates);
            //set num hints around bomb cell
            for (ButtonCoordinate buttonCoordinate : aroundBombCoordinates) {
                int x = buttonCoordinate.getX();
                int y = buttonCoordinate.getY();
                if (buttonArray[y][x].getDisabledIcon() != bomb) {
                    int bombCount = getAroundCells(x, y, aroundNumsCoordinates);
                    buttonArray[y][x].setDisabledIcon(getDisableIcon(bombCount));
                    aroundNumsCoordinates.clear();
                }
            }
        }
    }

    private int getAroundCells(int x, int y, ArrayList<ButtonCoordinate> aroundButtons) {
        int bombCount = 0;
        if (x > 0) {
            bombCount = getCellState(x - 1, y, aroundButtons, bombCount);
        }

        if (y > 0) {
            if (x > 0) {
                bombCount = getCellState(x - 1, y - 1, aroundButtons, bombCount);
            }
            bombCount = getCellState(x, y - 1, aroundButtons, bombCount);
            if (x < fieldLength - 1) {
                bombCount = getCellState(x + 1, y - 1, aroundButtons, bombCount);
            }
        }

        if (x < fieldLength - 1) {
            bombCount = getCellState(x + 1, y, aroundButtons, bombCount);
        }

        if (y < fieldWidth - 1) {
            if (x < fieldLength - 1) {
                bombCount = getCellState(x + 1, y + 1, aroundButtons, bombCount);
            }
            bombCount = getCellState(x, y + 1, aroundButtons, bombCount);
            if (x > 0) {
                bombCount = getCellState(x - 1, y + 1, aroundButtons, bombCount);
            }
        }
        return bombCount;
    }

    private int getCellState(int x, int y, ArrayList<ButtonCoordinate> aroundButtons, int bombCount) {
        if (buttonArray[y][x].getDisabledIcon() == bomb) {
            bombCount++;
        }
        aroundButtons.add(new ButtonCoordinate(x, y));
        return bombCount;
    }

    void buttonsAction() {
        final boolean[] timerStart = {false};
        for (int i = 0; i < fieldWidth; i++) {
            for (int j = 0; j < fieldLength; j++) {
                int y = i;
                int x = j;
                buttonArray[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent me) {
                        if (SwingUtilities.isLeftMouseButton(me)) {
                            if (!timerStart[0]) {
                                EventQueue.invokeLater(MineSweeper::createStopwatch);
                                timerStart[0] = true;
                            }
                            if (MineSweeper.myHint.cursor) {
                                if (buttonArray[y][x].isEnabled()) {
                                    MineSweeper.myHint.leftMouseClickListener(x, y);
                                }
                            } else {
                                leftMouseClickListener(x, y);
                            }
                            if (me.getClickCount() == 2
                                    && buttonArray[y][x].getDisabledIcon() != null
                                    && buttonArray[y][x].getDisabledIcon() != bomb) {
                                doubleClickAction(x, y);
                            }
                        }
                        if (SwingUtilities.isRightMouseButton(me) && buttonArray[y][x].isEnabled()) {
                            rightMouseClickListener(x, y);
                        }
                        try {
                            gameOverCheck();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    void rightMouseClickListener(int x, int y) {
        int actualBombCount = Integer.parseInt(bombLabel.getText());
        if (buttonArray[y][x].getIcon() == null) {
            buttonArray[y][x].setIcon(flag);
            bombLabel.setText(String.valueOf(actualBombCount - 1));
            return;
        }
        if (buttonArray[y][x].getIcon() == flag) {
            buttonArray[y][x].setIcon(null);
            bombLabel.setText(String.valueOf(actualBombCount + 1));
        }
    }

    void leftMouseClickListener(int x, int y) {
        if (buttonArray[y][x].getIcon() == flag) {
            buttonArray[y][x].setEnabled(true);
        } else {
            if (buttonArray[y][x].getDisabledIcon() == bomb) {
                showAllBombs(buttonArray[y][x]);
            } else if (buttonArray[y][x].getDisabledIcon() != null) {
                buttonArray[y][x].setEnabled(false);
                buttonArray[y][x].setIcon(buttonArray[y][x].getDisabledIcon());
            } else {
                disableHintsButtons(x, y);
                buttonArray[y][x].setEnabled(false);
                buttonArray[y][x].setIcon(buttonArray[y][x].getDisabledIcon());
            }
        }
    }

    void gameOverCheck() throws IOException {
        int allDisableCount = fieldLength * fieldWidth - bombCount;
        int actualDisableCount = 0;
        for (JButton[] buttonArrayI : buttonArray) {
            for (JButton button : buttonArrayI) {
                if (!button.isEnabled()) {
                    actualDisableCount++;
                }
            }
        }
        System.out.println(actualDisableCount);
        if (actualDisableCount == allDisableCount) {
            mineSweeper.gameOver("You win! :)");
        }
    }

    void showAllBombs(JButton button) {
        for (ButtonCoordinate bombCell : bombArray) {
            int x = bombCell.getX();
            int y = bombCell.getY();
            buttonArray[y][x].setEnabled(false);
            buttonArray[y][x].setIcon(bomb);
        }
        button.setDisabledIcon(explosion);
        try {
            mineSweeper.gameOver("You lose :(");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void disableHintsButtons(int x, int y) {
        ArrayList<ButtonCoordinate> buttonsAround = new ArrayList<>();
        buttonArray[y][x].setEnabled(false);
        getAroundCells(x, y, buttonsAround);
        for (ButtonCoordinate coordinate : buttonsAround) {
            x = coordinate.getX();
            y = coordinate.getY();
            if (buttonArray[y][x].isEnabled() && buttonArray[y][x].getIcon() != flag) {
                Icon icon = buttonArray[y][x].getDisabledIcon();
                if (icon == null) {
                    disableHintsButtons(x, y);
                } else {
                    buttonArray[y][x].setEnabled(false);
                    buttonArray[y][x].setIcon(icon);
                }
            }
        }
    }

    void doubleClickAction(int x, int y) {
        ArrayList<ButtonCoordinate> buttonsAround = new ArrayList<>();
        int bombCount = getAroundCells(x, y, buttonsAround);
        int flagCount = 0;
        for (ButtonCoordinate coordinate : buttonsAround) {
            JButton button = buttonArray[coordinate.getY()][coordinate.getX()];
            if (button.isEnabled() && button.getIcon() == flag) {
                if (button.getDisabledIcon() != bomb) {
                    showAllBombs(button);
                }
                flagCount++;
            }
        }
        if (bombCount == flagCount) {
            disableHintsButtons(x, y);
        }
    }

    Icon getDisableIcon(int expectedBombCount) {
        switch (expectedBombCount) {
            case 0:
                return null;
            case 1:
                return num1;
            case 2:
                return num2;
            case 3:
                return num3;
            case 4:
                return num4;
            case 5:
                return num5;
            case 6:
                return num6;
            case 7:
                return num7;
            case 8:
                return num8;
        }
        return null;
    }
}