package com.example.labiryntsolver;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicBoolean;

public class AlgorithmPageController {
    private Maze maze;
    @FXML
    private GridPane mainGrid;
    private MainApplication _mainApplication;
    private GeneticAlgorithm geneticAlgorithm;
    @FXML
    private Canvas algorithmCanvas;

    public void initialize() {
        algorithmCanvas.widthProperty().bind(mainGrid.widthProperty().divide(2));
        algorithmCanvas.heightProperty().bind(algorithmCanvas.widthProperty());
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
        Generation generation = null;
        do {
            generation = geneticAlgorithm.nextGeneration();
            System.out.println(generation.getNumber() + " | " + generation.getBestScore());
        } while (generation.getBestScore() != 1.0);
        drawMaze();
        System.out.println(generation.getNumber());
        System.out.println(generation + "\n\n");
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

        WritableImage mazeImage = new WritableImage((int)mazeInCanvas.getWidth(), (int)mazeInCanvas.getHeight());
        mazeInCanvas.snapshot(null, mazeImage);

        GraphicsContext gc = algorithmCanvas.getGraphicsContext2D();
        gc.drawImage(mazeImage, 0, 0, algorithmCanvas.getWidth(), algorithmCanvas.getHeight());
    }
}
