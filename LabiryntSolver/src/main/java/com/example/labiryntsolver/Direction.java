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

    @Override
    public String toString() {
        return displayName;
    }
}
