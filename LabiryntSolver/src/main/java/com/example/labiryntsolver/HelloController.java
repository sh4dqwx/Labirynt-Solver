package com.example.labiryntsolver;

import javafx.fxml.FXML;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

public class HelloController {
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private GridPane mainGrid;
    private Maze maze;

    public void initialize() {
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 10));
        maze = new Maze();
    }

    public void generateMaze() {
        int spinnerValue = spinner.getValue();
        maze.generateMaze(spinnerValue);
        GridPane mazeGridPane = maze.getMazeInGrid();
        GridPane.setColumnIndex(mazeGridPane, 1);
        mazeGridPane.setMaxWidth(mainGrid.getColumnConstraints().get(1).getPrefWidth());
        mainGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null && GridPane.getColumnIndex(node) == 1);
        mainGrid.getChildren().add(mazeGridPane);
    }
}