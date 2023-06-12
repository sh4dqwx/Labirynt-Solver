package com.example.labiryntsolver;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

import java.util.concurrent.atomic.AtomicReference;

public class AlgorithmPageController {
    private Maze maze;
    private MainApplication _mainApplication;
    private GeneticAlgorithm geneticAlgorithm;
    private WritableImage mazeImage;
    @FXML
    private GridPane mainGrid;
    @FXML
    private Label generationNrLabel;
    @FXML
    private Canvas algorithmCanvas;
    @FXML
    private ListView<String> solutionListView;
    @FXML
    private Button backBtn;
    @FXML
    private Button autoBtn;

    public void initialize() {
        algorithmCanvas.widthProperty().bind(mainGrid.widthProperty().divide(2));
        algorithmCanvas.heightProperty().bind(algorithmCanvas.widthProperty());

        mainGrid.widthProperty().addListener((observable, oldValue, newValue) -> redrawImage());
        mainGrid.heightProperty().addListener((observable, oldValue, newValue) -> redrawImage());

        solutionListView.setOnMouseClicked(event -> {
            int selectedIndex = solutionListView.getSelectionModel().getSelectedIndex();
            drawMaze(selectedIndex);
        });
    }

    public void setMainApplicationReference(MainApplication mainApplication) {
        _mainApplication = mainApplication;
    }

    public void preparePage(Maze maze) {
        this.maze = maze;
        geneticAlgorithm = new GeneticAlgorithm(maze);
        drawMaze(0);
        autoBtn.setDisable(false);
    }

    public void nextGeneration() {
        Generation generation = geneticAlgorithm.nextGeneration();
        drawMaze(0);
        showGeneration(generation);
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void next10Generations() {
        Generation generation = null;
        for (int i = 0; i < 10; i++) {
            generation = geneticAlgorithm.nextGeneration();
        }
        drawMaze(0);
        showGeneration(generation);
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void next100Generations() {
        Generation generation = null;
        for (int i = 0; i < 100; i++) {
            generation = geneticAlgorithm.nextGeneration();
        }
        drawMaze(0);
        showGeneration(generation);
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void autoMode() {
        autoBtn.setDisable(true);
        backBtn.setDisable(true);
        final int refreshInterval = 100;
        new Thread(() -> {
            AtomicReference<Generation> generationRef = new AtomicReference<>();

            do {
                generationRef.set(geneticAlgorithm.nextGeneration());

                if (generationRef.get().getNumber() % refreshInterval == 0) {
                    Platform.runLater(() -> drawMaze(0));
                    Platform.runLater(() -> showGeneration(generationRef.get()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(generationRef.get().getNumber() + " | " + generationRef.get().getBestScore());
            } while (generationRef.get().getBestScore() != 1.0);

            Platform.runLater(() -> drawMaze(0));
            Platform.runLater(() -> showGeneration(generationRef.get()));

            System.out.println(generationRef.get().getNumber());
            System.out.println(generationRef.get() + "\n\n");
            Platform.runLater(() -> backBtn.setDisable(false));
        }).start();
    }

    private void showGeneration(Generation generation) {
        generationNrLabel.setText("Generacja " + generation.getNumber());
        ObservableList<String> content = FXCollections.observableArrayList(generation.getSolutionDisplayList());
        solutionListView.setItems(content);
    }

    public void goBack() {
        solutionListView.getItems().clear();
        generationNrLabel.setText("Generacja 0");
        _mainApplication.goToGeneratePage();
    }

    private void drawMaze(int solutionNr) {
        Canvas mazeInCanvas;
        Direction[] directionsToDraw = geneticAlgorithm.getSolutionDirectionList(solutionNr);
        if(directionsToDraw == null)
            mazeInCanvas = maze.getMazeInCanvas();
        else mazeInCanvas = maze.getMazeWithSolutionInCanvas(directionsToDraw);

        mazeImage = new WritableImage((int)mazeInCanvas.getWidth(), (int)mazeInCanvas.getHeight());
        mazeInCanvas.snapshot(null, mazeImage);

        GraphicsContext gc = algorithmCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, algorithmCanvas.getWidth(), algorithmCanvas.getHeight());
    }

    private void redrawImage() {
        GraphicsContext gc = algorithmCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, algorithmCanvas.getWidth(), algorithmCanvas.getHeight());
    }
}
