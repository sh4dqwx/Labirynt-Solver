package com.example.labiryntsolver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    private Stage stage;
    private Scene generatePage, algorithmPage;
    private GeneratePageController generatePageController;
    private AlgorithmPageController algorithmPageController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader generatePageLoader = new FXMLLoader(MainApplication.class.getResource("generate-page.fxml"));
        FXMLLoader algorithmPageLoader = new FXMLLoader(MainApplication.class.getResource("algorithm-page.fxml"));
        generatePage = new Scene(generatePageLoader.load(), 1080, 720);
        algorithmPage = new Scene(algorithmPageLoader.load(), 1080, 720);
        stage.setTitle("LabiryntSolver");
        stage.setScene(generatePage);
        stage.show();
        this.stage = stage;


        generatePageController = generatePageLoader.getController();
        generatePageController.setMainApplicationReference(this);

        algorithmPageController = algorithmPageLoader.getController();
        algorithmPageController.setMainApplicationReference(this);
    }

    public void goToAlgorithmPage(Maze maze, int populationSize, int maxAutoGeneration, double crossoverProbability, double mutationProbability) {
        stage.setScene(algorithmPage);
        algorithmPageController.preparePage(maze, populationSize, maxAutoGeneration, crossoverProbability, mutationProbability);
    }

    public void goToGeneratePage() {
        stage.setScene(generatePage);
    }

    public static void main(String[] args) {
        launch();
    }
}