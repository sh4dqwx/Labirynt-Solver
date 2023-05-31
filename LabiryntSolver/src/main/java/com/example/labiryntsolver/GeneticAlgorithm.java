package com.example.labiryntsolver;

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    private static int POPULATION_SIZE = 10;
    private int currentGeneration;
    private Maze maze;
    private ArrayList<Generation> generationList;

    public GeneticAlgorithm(Maze maze) {
        generationList = new ArrayList<>();
        currentGeneration = 0;
        this.maze = maze;
    }

    public Generation nextGeneration() {
        if(currentGeneration == 0) {
            return start();
        }
        //Krzyżowanie, mutacja

        Generation newGeneration = new Generation(currentGeneration + 1);

        int number = newGeneration.getNumber();

        return newGeneration;
    }

    private Generation start() {
        Generation newGeneration = new Generation(1);
        currentGeneration = 1;
        int maxDistance = maze.getMaxDistanceFromEnd();
        for(int i=0; i<POPULATION_SIZE; i++) {
            newGeneration.generateSolution(maxDistance);
        }

        //Do testów
        ArrayList<Integer> fitnessScores = newGeneration.fitnessAllSolutions(maze);
        for (int singleFitnessScore : fitnessScores) {
            System.out.print(singleFitnessScore + " ");
        }
        System.out.println();

        return newGeneration;
    }
}

class Generation {
    private int nr;
    private ArrayList<Solution> solutionList;

    public Generation(int nr) {
        this.nr = nr;
        solutionList = new ArrayList<>();
    }

    public int getNumber() {
        return nr;
    }

    public void addSolution(Solution solution) {
        solutionList.add(solution);
    }

    public void generateSolution(int maxDistance) {
        Solution solution = new Solution();

        for (int i = 0; i < maxDistance; i++) {
            int randomInt = new Random().nextInt(Direction.values().length);
            Direction direction = Direction.values()[randomInt];
            solution.addDirection(direction);
        }

        addSolution(solution);
    }

    public ArrayList<Integer> fitnessAllSolutions(Maze maze) {
        ArrayList<Integer> fitness = new ArrayList<>();
        for (Solution solution: solutionList) {
            fitness.add(solution.fitness(maze));
        }
        return fitness;
    }

    @Override
    public String toString() {
        StringBuilder toSend = new StringBuilder();
        for(int i = 0; i < solutionList.size(); i++) {
            toSend.append(String.format("Osobnik %d\n", i + 1)).append(solutionList.get(i));
            toSend.append("\n");
        }
        return toSend.toString();
    }
}

class Solution {
    private ArrayList<Direction> directionList;

    public Solution() {
        directionList = new ArrayList<>();
    }

    public ArrayList<Direction> getDirectionList() {
        return directionList;
    }

    public void addDirection(Direction direction) {
        directionList.add(direction);
    }

    public int fitness(Maze maze) {
        int i = 0, j = 0;
        for (Direction direction: directionList) {
            if(!maze.getPossibleDirections(i, j).contains(direction)) break;
            switch (direction) {
                case UP -> i--;
                case DOWN -> i++;
                case LEFT -> j--;
                case RIGHT -> j++;
            }
        }
        return maze.getDistanceToEnd(i, j);
    }

    @Override
    public String toString() {
        StringBuilder toSend = new StringBuilder();
        for(Direction direction : directionList) {
            toSend.append(direction.toString()).append(", ");
        }
        return toSend.toString();
    }
}