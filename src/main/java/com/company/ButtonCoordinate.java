package com.company;

class ButtonCoordinate {
    private int x;
    private int y;

    ButtonCoordinate() {
        this.x = -1;
        this.y = -1;
    }

    ButtonCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    ButtonCoordinate coordinateConverter(int cellIndex, int fieldLength) {
        this.x = cellIndex % fieldLength;
        this.y = cellIndex / fieldLength;
        return this;
    }
}