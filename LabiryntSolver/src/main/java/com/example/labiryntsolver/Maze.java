package com.example.labiryntsolver;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private int maxDistanceFromEnd;
    private ArrayList<ArrayList<MazeElement>> mazeElements;
    public Maze() {
        mazeElements = new ArrayList<>();
        maxDistanceFromEnd = 0;
    }

    public int getDistanceFromStartToEnd() {
        return getDistanceToEnd(0, 0);
    }

    public ArrayList<Direction> getPossibleDirections(int elementI, int elementJ) {
        if (elementI < 0 || elementJ < 0)
            return new ArrayList<Direction>();
        else
            return mazeElements.get(elementI).get(elementJ).getPossibleDirections();
    }

    public int getDistanceToEnd(int elementI, int elementJ) {
        return mazeElements.get(elementI).get(elementJ).getDistanceFromEnd();
    }

    public void generateMaze(int size) {
        mazeElements.clear();

        for (int i = 0; i < size; i++) {
            ArrayList<MazeElement> mazeRow = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                MazeElement element = new MazeElement();
                element.setBorders(true, true, true, true);
                if (i == 0 && j == 0) element.setUpBorder(false);
                if (i == size - 1 && j == size - 1) element.setDownBorder(false);
                mazeRow.add(element);
            }
            mazeElements.add(mazeRow);
        }

        DFS(mazeElements, mazeElements.size() - 1, mazeElements.get(0).size() - 1, 0);
    }

    public Canvas getMazeInCanvas() {
        int mazeSize = mazeElements.size();
        int cellSize = 100;
        int wallThickness = 5;
        int canvasSize = mazeSize * cellSize + wallThickness * (mazeSize + 1);

        Canvas canvas = new Canvas(canvasSize, canvasSize);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvasSize, canvasSize);

        for (int i = 0; i < mazeSize; i++) {
            for (int j = 0; j < mazeSize; j++) {
                MazeElement mazeElement = mazeElements.get(i).get(j);

                int x = wallThickness + j * cellSize + j * wallThickness;
                int y = wallThickness + i * cellSize + i * wallThickness;

                if (mazeElement.isLeftBorder()) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x - wallThickness, y - wallThickness, wallThickness, 2 * wallThickness + cellSize);
                }
                if (mazeElement.isRightBorder()) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x + cellSize, y - wallThickness, wallThickness, 2 * wallThickness + cellSize);
                }
                if (mazeElement.isUpBorder()) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x - wallThickness, y - wallThickness, 2 * wallThickness + cellSize, wallThickness);
                }
                if (mazeElement.isDownBorder()) {
                    gc.setFill(Color.BLACK);
                    gc.fillRect(x - wallThickness, y + cellSize, 2 * wallThickness + cellSize, wallThickness);
                }
            }
        }

        return canvas;
    }

    private Direction[] getPassageList(ArrayList<ArrayList<MazeElement>> mazeElements, int elementI, int elementJ) {
        ArrayList<Direction> passageList = new ArrayList<>();
        if(elementI > 0 && !mazeElements.get(elementI - 1).get(elementJ).isVisited()) {
            passageList.add(Direction.UP);
        }
        if(elementI < mazeElements.size() - 1 && !mazeElements.get(elementI + 1).get(elementJ).isVisited()) {
            passageList.add(Direction.DOWN);
        }
        if(elementJ > 0 && !mazeElements.get(elementI).get(elementJ - 1).isVisited()) {
            passageList.add(Direction.LEFT);
        }
        if(elementJ < mazeElements.get(0).size() - 1 && !mazeElements.get(elementI).get(elementJ + 1).isVisited()) {
            passageList.add(Direction.RIGHT);
        }
        return passageList.toArray(new Direction[0]);
    }

    private void DFS(ArrayList<ArrayList<MazeElement>> mazeElements, int elementI, int elementJ, int distance) {
        mazeElements.get(elementI).get(elementJ).setVisited(true);
        mazeElements.get(elementI).get(elementJ).setDistanceFromEnd(distance);
        maxDistanceFromEnd = Math.max(maxDistanceFromEnd, distance);

        while(true) {
            Direction[] passageList = getPassageList(mazeElements, elementI, elementJ);
            if(passageList.length == 0) return;

            int randomIndex = new Random().nextInt(passageList.length);
            Direction direction = passageList[randomIndex];
            if(direction == Direction.UP) {
                mazeElements.get(elementI).get(elementJ).setUpBorder(false);
                mazeElements.get(elementI - 1).get(elementJ).setDownBorder(false);
                DFS(mazeElements, elementI - 1, elementJ, distance + 1);
            }
            if(direction == Direction.DOWN) {
                mazeElements.get(elementI).get(elementJ).setDownBorder(false);
                mazeElements.get(elementI + 1).get(elementJ).setUpBorder(false);
                DFS(mazeElements, elementI + 1, elementJ, distance + 1);
            }
            if(direction == Direction.LEFT) {
                mazeElements.get(elementI).get(elementJ).setLeftBorder(false);
                mazeElements.get(elementI).get(elementJ - 1).setRightBorder(false);
                DFS(mazeElements, elementI, elementJ - 1, distance + 1);
            }
            if(direction == Direction.RIGHT) {
                mazeElements.get(elementI).get(elementJ).setRightBorder(false);
                mazeElements.get(elementI).get(elementJ + 1).setLeftBorder(false);
                DFS(mazeElements, elementI, elementJ + 1, distance + 1);
            }
        }
    }
}

class MazeElement {
    private int distanceFromEnd;
    private boolean visited, upBorder, downBorder, leftBorder, rightBorder;
    public MazeElement() {
        this.visited = false;
    }

    public void setBorders(boolean upBorder, boolean downBorder, boolean leftBorder, boolean rightBorder) {
        this.upBorder = upBorder;
        this.downBorder = downBorder;
        this.leftBorder = leftBorder;
        this.rightBorder = rightBorder;
    }

    public boolean isUpBorder() { return upBorder; }

    public void setUpBorder(boolean upBorder) { this.upBorder = upBorder; }

    public boolean isDownBorder() { return downBorder; }

    public void setDownBorder(boolean downBorder) {
        this.downBorder = downBorder;
    }

    public boolean isLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(boolean leftBorder) {
        this.leftBorder = leftBorder;
    }

    public boolean isRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(boolean rightBorder) {
        this.rightBorder = rightBorder;
    }

    public boolean isVisited() { return visited; }

    public void setVisited(boolean visited) { this.visited = visited; }

    public int getDistanceFromEnd() { return distanceFromEnd; }

    public void setDistanceFromEnd(int distanceFromEnd) { this.distanceFromEnd = distanceFromEnd; }

    public ArrayList<Direction> getPossibleDirections() {
        ArrayList<Direction> directionList = new ArrayList<>();
        if (!upBorder) directionList.add(Direction.UP);
        if (!downBorder) directionList.add(Direction.DOWN);
        if (!leftBorder) directionList.add(Direction.LEFT);
        if (!rightBorder) directionList.add(Direction.RIGHT);

        return directionList;
    }
}