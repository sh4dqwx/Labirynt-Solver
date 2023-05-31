package com.example.labiryntsolver;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

public class GeneratePageController {
    @FXML
    private Spinner<Integer> spinner;
    @FXML
    private Canvas generateCanvas;
    @FXML
    private GridPane mainGrid;
    private Maze maze;
    private MainApplication _mainApplication;

    public void initialize() {
        generateCanvas.widthProperty().bind(mainGrid.widthProperty().divide(2));
        generateCanvas.heightProperty().bind(generateCanvas.widthProperty());
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 30, 10));
        maze = new Maze();
    }

    public void generateMaze() {
        int spinnerValue = spinner.getValue();
        maze.generateMaze(spinnerValue);

        Canvas mazeInCanvas = maze.getMazeInCanvas();
        WritableImage mazeImage = new WritableImage((int)mazeInCanvas.getWidth(), (int)mazeInCanvas.getHeight());
        mazeInCanvas.snapshot(null, mazeImage);

        GraphicsContext gc = generateCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, generateCanvas.getWidth(), generateCanvas.getHeight());
    }

    public void startAlgorithm() {
        _mainApplication.goToAlgorithmPage(maze);
    }

    public void setMainApplicationReference(MainApplication mainApplication) {
        _mainApplication = mainApplication;
    }
}