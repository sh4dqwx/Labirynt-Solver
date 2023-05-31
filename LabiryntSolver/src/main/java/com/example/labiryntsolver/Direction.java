package com.example.labiryntsolver;

public enum Direction {
    UP("góra"),
    DOWN("dół"),
    LEFT("lewo"),
    RIGHT("prawo");

    private final String displayName;
    Direction(String displayName) {
        this.displayName = displayName;
    }

    public int toInt() {
        int returnValue;
        switch (this) {
            case UP -> returnValue = 1;
            case DOWN -> returnValue = 2;
            case LEFT -> returnValue = 3;
            case RIGHT -> returnValue = 4;
            default -> returnValue = 0;
        }
        return returnValue;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
