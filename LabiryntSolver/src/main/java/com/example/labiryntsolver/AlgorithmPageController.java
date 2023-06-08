package com.example.labiryntsolver;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;

public class AlgorithmPageController {
    private Maze maze;
    @FXML
    private GridPane mainGrid;
    private MainApplication _mainApplication;
    private GeneticAlgorithm geneticAlgorithm;
    @FXML
    private Canvas algorithmCanvas;
    private WritableImage mazeImage;

    public void initialize() {
        algorithmCanvas.widthProperty().bind(mainGrid.widthProperty().divide(2));
        algorithmCanvas.heightProperty().bind(algorithmCanvas.widthProperty());

        mainGrid.widthProperty().addListener((observable, oldValue, newValue) -> redrawImage());
        mainGrid.heightProperty().addListener((observable, oldValue, newValue) -> redrawImage());
    }

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
        drawMaze();
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void next10Generations() {
        Generation generation = null;
        for (int i = 0; i < 10; i++) {
            generation = geneticAlgorithm.nextGeneration();
        }
        drawMaze();
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void next100Generations() {
        Generation generation = null;
        for (int i = 0; i < 100; i++) {
            generation = geneticAlgorithm.nextGeneration();
        }
        drawMaze();
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
    }

    public void autoMode() {
        final int refreshInterval = 100;

        new Thread(() -> {
            Generation generation = null;

            do {
                generation = geneticAlgorithm.nextGeneration();

                if (generation.getNumber() % refreshInterval == 0) {
                    Platform.runLater(this::drawMaze);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println(generation.getNumber() + " | " + generation.getBestScore());
            } while (generation.getBestScore() != 1.0);

            Platform.runLater(this::drawMaze);

            System.out.println(generation.getNumber());
            System.out.println(generation + "\n\n");
        }).start();
    }

    public void goBack() {
        _mainApplication.goToGeneratePage();
    }

    private void drawMaze() {
        Canvas mazeInCanvas;
        Direction[] directionsToDraw = geneticAlgorithm.getBestSolutionDirectionList();
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
