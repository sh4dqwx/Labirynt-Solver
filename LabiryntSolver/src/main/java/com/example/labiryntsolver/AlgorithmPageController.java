package com.example.labiryntsolver;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

public class AlgorithmPageController {
    private Maze maze;
    @FXML
    private GridPane mainGrid;
    private MainApplication _mainApplication;
    private GeneticAlgorithm geneticAlgorithm;

    public void setMainApplicationReference(MainApplication mainApplication) {
        _mainApplication = mainApplication;
    }

    public void preparePage(Maze maze) {
        this.maze = maze;
        geneticAlgorithm = new GeneticAlgorithm(maze);
        drawMaze();
    }

    public void nextGeneration() {
        Generation generation = geneticAlgorithm.nextGeneration();
        System.out.println(generation);
    }

    public void autoMode() {

    }

    public void goBack() {
        _mainApplication.goToGeneratePage();
    }

    //Zarezerwowane
    private void drawMaze() {
        GridPane mazeGridPane = maze.getMazeInGrid();

        mazeGridPane.setMaxWidth(600);
        mazeGridPane.setMaxHeight(600);

        mainGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 1);
        mainGrid.add(mazeGridPane, 1, 0);
    }
}
