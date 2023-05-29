package com.example.labiryntsolver;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;

public class GeneratePageController {
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private GridPane mainGrid;
    private Maze maze;
    private MainApplication _mainApplication;

    public void initialize() {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 10));
        maze = new Maze();
    }

    //Zarezerowwane
    public void generateMaze() {
        int spinnerValue = spinner.getValue();
        maze.generateMaze(spinnerValue);
        GridPane mazeGridPane = maze.getMazeInGrid();

        mazeGridPane.setMaxWidth(600);
        mazeGridPane.setMaxHeight(600);

        mainGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 1);
        mainGrid.add(mazeGridPane, 1, 0);
    }

    public void startAlgorithm() {
        _mainApplication.goToAlgorithmPage(maze);
    }

    public void setMainApplicationReference(MainApplication mainApplication) {
        _mainApplication = mainApplication;
    }
}