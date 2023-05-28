package com.example.labiryntsolver;

import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class Maze {
    private ArrayList<ArrayList<MazeElement>> mazeElements;
    public Maze() {
        mazeElements = new ArrayList<>();
    }

    public void generateMaze(int size) {
        mazeElements.clear();

        for (int i = 0; i < size; i++) {
            ArrayList<MazeElement> mazeRow = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                MazeElement element = new MazeElement();
                element.setBorders(true, true, true, true);
                mazeRow.add(element);
            }
            mazeElements.add(mazeRow);
        }

        //DFS(mazeElements, 0, 0);
    }

    public GridPane getMazeInGrid() {
        GridPane grid = new GridPane();

        for (int i = 0; i < mazeElements.size(); i++) {
            for (int j = 0; j < mazeElements.get(i).size(); j++) {
                MazeElement mazeElement = mazeElements.get(i).get(j);
                StackPane stackPane = new StackPane();
                stackPane.setStyle("-fx-background-color: white");
                if (mazeElement.isLeftBorder()) {
                    Rectangle leftBorder = new Rectangle(5, 100, Color.BLACK);
                    stackPane.getChildren().add(leftBorder);
                }
                if (mazeElement.isRightBorder()) {
                    Rectangle rightBorder = new Rectangle(5, 100, Color.BLACK);
                    rightBorder.setTranslateX(95);
                    stackPane.getChildren().add(rightBorder);
                }
                if (mazeElement.isUpBorder()) {
                    Rectangle upBorder = new Rectangle(100, 5, Color.BLACK);
                    stackPane.getChildren().add(upBorder);
                }
                if (mazeElement.isDownBorder()) {
                    Rectangle downBorder = new Rectangle(100, 5, Color.BLACK);
                    downBorder.setTranslateY(95);
                    stackPane.getChildren().add(downBorder);
                }
                grid.add(stackPane, j, i);
            }
        }

        return grid;
    }

    private PassageEnum[] getPassageList(ArrayList<ArrayList<MazeElement>> mazeElements, int elementI, int elementJ) {
        ArrayList<PassageEnum> passageList = new ArrayList<>();
        if(elementI > 0 && !mazeElements.get(elementI - 1).get(elementJ).isVisited()) {
            passageList.add(PassageEnum.UP);
        }
        if(elementI < mazeElements.size() - 1 && !mazeElements.get(elementI + 1).get(elementJ).isVisited()) {
            passageList.add(PassageEnum.DOWN);
        }
        if(elementJ > 0 && !mazeElements.get(elementI).get(elementJ - 1).isVisited()) {
            passageList.add(PassageEnum.LEFT);
        }
        if(elementJ < mazeElements.get(0).size() - 1 && !mazeElements.get(elementI).get(elementJ + 1).isVisited()) {
            passageList.add(PassageEnum.RIGHT);
        }
        return passageList.toArray(new PassageEnum[0]);
    }

    private void DFS(ArrayList<ArrayList<MazeElement>> mazeElements, int elementI, int elementJ) {
        mazeElements.get(elementI).get(elementJ).setVisited(true);
        while(true) {
            PassageEnum[] passageList = getPassageList(mazeElements, elementI, elementJ);
            if(passageList.length == 0) return;

            int randomIndex = new Random().nextInt(passageList.length);
            PassageEnum direction = passageList[randomIndex];
            if(direction == PassageEnum.UP) {
                mazeElements.get(elementI).get(elementJ).setUpBorder(false);
                mazeElements.get(elementI - 1).get(elementJ).setDownBorder(false);
                DFS(mazeElements, elementI - 1, elementJ);
            }
            if(direction == PassageEnum.DOWN) {
                mazeElements.get(elementI).get(elementJ).setDownBorder(false);
                mazeElements.get(elementI + 1).get(elementJ).setUpBorder(false);
                DFS(mazeElements, elementI + 1, elementJ);
            }
            if(direction == PassageEnum.LEFT) {
                mazeElements.get(elementI).get(elementJ).setLeftBorder(false);
                mazeElements.get(elementI).get(elementJ - 1).setRightBorder(false);
                DFS(mazeElements, elementI, elementJ - 1);
            }
            if(direction == PassageEnum.RIGHT) {
                mazeElements.get(elementI).get(elementJ).setRightBorder(false);
                mazeElements.get(elementI).get(elementJ + 1).setLeftBorder(false);
                DFS(mazeElements, elementI, elementJ + 1);
            }
        }
    }
}

class MazeElement {
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

    public void setUpBorder(boolean upBorder) {
        this.upBorder = upBorder;
    }

    public void setDownBorder(boolean downBorder) {
        this.downBorder = downBorder;
    }

    public void setLeftBorder(boolean leftBorder) {
        this.leftBorder = leftBorder;
    }

    public void setRightBorder(boolean rightBorder) {
        this.rightBorder = rightBorder;
    }

    public boolean isUpBorder() {
        return upBorder;
    }

    public boolean isDownBorder() {
        return downBorder;
    }

    public boolean isLeftBorder() {
        return leftBorder;
    }

    public boolean isRightBorder() {
        return rightBorder;
    }

    public void setVisited(boolean visited) { this.visited = visited; }

    public boolean isVisited() { return visited; }
}

enum PassageEnum {
    UP,
    DOWN,
    LEFT,
    RIGHT
}
