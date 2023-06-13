package com.example.labiryntsolver;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

public class GeneratePageController {
    @FXML
    private Spinner<Integer> mazeSideSpinner;
    @FXML
    private Canvas generateCanvas;
    @FXML
    private GridPane mainGrid;
    @FXML
    private Button runBtn;
    @FXML
    private Spinner<Integer> populationSizeSpinner;
    @FXML
    private Spinner<Integer> maxAutoGenerationSpinner;
    @FXML
    private Spinner<Double> crossoverProbabilitySpinner;
    @FXML
    private Spinner<Double> mutationProbabilitySpinner;
    private Maze maze;
    private MainApplication _mainApplication;
    private WritableImage mazeImage;

    public void initialize() {
        generateCanvas.widthProperty().bind(mainGrid.widthProperty().divide(2));
        generateCanvas.heightProperty().bind(generateCanvas.widthProperty());
        mazeSideSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 50, 5));
        populationSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 50));
        maxAutoGenerationSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000000, 10000));
        crossoverProbabilitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.8));
        mutationProbabilitySpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 1, 0.2));
        maze = new Maze();

        mainGrid.widthProperty().addListener((observable, oldValue, newValue) -> redrawImage());
        mainGrid.heightProperty().addListener((observable, oldValue, newValue) -> redrawImage());
    }

    public void generateMaze() {
        int mazeSize = mazeSideSpinner.getValue();
        maze.generateMaze(mazeSize);

        Canvas mazeInCanvas = maze.getMazeInCanvas();
        mazeImage = new WritableImage((int)mazeInCanvas.getWidth(), (int)mazeInCanvas.getHeight());
        mazeInCanvas.snapshot(null, mazeImage);

        GraphicsContext gc = generateCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, generateCanvas.getWidth(), generateCanvas.getHeight());

        runBtn.setDisable(false);
    }

    public void startAlgorithm() {
        if(maze == null) return;
        int populationSize = populationSizeSpinner.getValue();
        int maxAutoGeneration = maxAutoGenerationSpinner.getValue();
        double crossoverProbability = crossoverProbabilitySpinner.getValue();
        double mutationProbability = mutationProbabilitySpinner.getValue();
        _mainApplication.goToAlgorithmPage(maze, populationSize, maxAutoGeneration, crossoverProbability, mutationProbability);
    }

    public void setMainApplicationReference(MainApplication mainApplication) {
        _mainApplication = mainApplication;
    }

    private void redrawImage() {
        if(mazeImage == null) return;
        GraphicsContext gc = generateCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, generateCanvas.getWidth(), generateCanvas.getHeight());
    }
}