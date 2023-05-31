package com.example.labiryntsolver;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    private static int POPULATION_SIZE = 10;
    private Maze maze;
    private ArrayList<Generation> generationList;
    private Generation currentGeneration;

    public GeneticAlgorithm(Maze maze) {
        generationList = new ArrayList<>();
        this.maze = maze;
    }

    public Generation nextGeneration() {
        if(currentGeneration == null)
            return startNewGeneration();
        Generation nextGeneration = currentGeneration.generateNextGeneration();
        generationList.add(nextGeneration);
        currentGeneration = nextGeneration;
        return currentGeneration;
    }

    private Generation startNewGeneration() {
        Solution[] solutionList = new Solution[POPULATION_SIZE];
        for(int i=0; i<POPULATION_SIZE; i++)
            solutionList[i] = Solution.random(maze.getMaxDistanceFromEnd(), maze);
        Generation firstGeneration = new Generation(1, solutionList, maze);
        generationList.add(firstGeneration);
        currentGeneration = firstGeneration;
        return currentGeneration;
    }
}

class Generation {
    private static double CROSSOVER_PROBABILITY = 0.8;
    private int nr;
    private Solution[] solutionList;
    private Maze maze;

    public Generation(int nr, Solution[] solutionList, Maze maze) {
        this.nr = nr;
        this.solutionList = solutionList;
        this.maze = maze;
    }

    public int getNumber() {
        return nr;
    }

    public int[] getFitnessScores() {
        int[] fitnessScores = new int[solutionList.length];
        for(int i=0; i< solutionList.length; i++)
            fitnessScores[i] = solutionList[i].getFitness();
        return fitnessScores;
    }

    public Generation generateNextGeneration() {
        ArrayList<Solution> nextSolutionList = new ArrayList<>();
        double[] rouletteWheel = createRouletteWheel(getFitnessScores());
        while(nextSolutionList.size() < solutionList.length) {
            Solution parent1 = getRandomSolution(rouletteWheel);
            Solution parent2 = getRandomSolution(rouletteWheel);
            if(Math.random() > CROSSOVER_PROBABILITY)
                continue;
            Direction[] parent1Directions = mutateDirections(parent1.getDirectionList());
            Direction[] parent2Directions = mutateDirections(parent2.getDirectionList());
            Pair<Direction[], Direction[]> crossedDirections = crossoverSolutions(new Pair<>(parent1Directions, parent2Directions));

            Solution crossedSolution1 = new Solution(crossedDirections.getKey(), maze);
            Solution crossedSolution2 = new Solution(crossedDirections.getValue(), maze);

            nextSolutionList.add(crossedSolution1);
            if(nextSolutionList.size() < solutionList.length)
                nextSolutionList.add(crossedSolution2);
        }

        return new Generation(nr + 1, nextSolutionList.toArray(new Solution[0]), maze);
    }

    private double[] createRouletteWheel(int[] fitnessScores) {
        int fitnessSum = 0;
        for(int fitness : fitnessScores)
            fitnessSum += fitness;
        double[] rouletteWheel = new double[fitnessScores.length];
        for(int i=0; i<fitnessScores.length; i++)
            rouletteWheel[i] = 1.0 * fitnessScores[i] / fitnessSum;
        return rouletteWheel;
    }

    private Solution getRandomSolution(double[] rouletteWheel) {
        double randomValue = Math.random();
        double baseValue = 0.0;
        for(int i=0; i<rouletteWheel.length; i++) {
            if(baseValue <= randomValue && randomValue <= rouletteWheel[i])
                return solutionList[i];
            baseValue += rouletteWheel[i];
        }
        return solutionList[rouletteWheel.length - 1];
    }

    private Pair<Direction[], Direction[]> crossoverSolutions(Pair<Direction[], Direction[]> directionsPair) {
        int minDirectionsLength = Math.min(directionsPair.getKey().length, directionsPair.getValue().length);
        int cutPoint = new Random().nextInt(minDirectionsLength - 1);

        Direction[] first = new Direction[minDirectionsLength];
        Direction[] second = new Direction[minDirectionsLength];
        for(int i=0; i<minDirectionsLength; i++) {
            if(i <= cutPoint) {
                first[i] = directionsPair.getKey()[i];
                second[i] = directionsPair.getValue()[i];
            } else {
                first[i] = directionsPair.getValue()[i];
                second[i] = directionsPair.getKey()[i];
            }
        }
        return new Pair<>(first, second);
    }

    private Direction[] mutateDirections(Direction[] directions) {
        int randomPoint = new Random().nextInt(directions.length);
        Direction oldDirection = directions[randomPoint];

        int randomDirectionNumber = new Random().nextInt(Direction.values().length - 1) + 1;
        if (randomDirectionNumber >= oldDirection.toInt())
            randomDirectionNumber++;
        Direction newDirection = Direction.values()[randomDirectionNumber - 1];

        directions[randomPoint] = newDirection;
        return directions;
    }

    @Override
    public String toString() {
        StringBuilder toSend = new StringBuilder();
        for (int i = 0; i < solutionList.length; i++) {
            toSend.append(String.format("Osobnik %d\n", i + 1)).append(solutionList[i]);
            toSend.append("\n");
        }
        return toSend.toString();
    }
}

class Solution {
    private Direction[] directionList;
    private int fitness;

    public static Solution random(int maxDistance, Maze maze) {
        Direction[] randomDirections = new Direction[maxDistance];
        for (int i = 0; i < maxDistance; i++) {
            int randomInt = new Random().nextInt(Direction.values().length);
            Direction direction = Direction.values()[randomInt];
            randomDirections[i] = direction;
        }

        return new Solution(randomDirections, maze);
    }

    public Solution(Direction[] directionList, Maze maze) {
        this.directionList = directionList;
        calulateFitness(maze);
    }

    public int getFitness() {
        return fitness;
    }

    public Direction[] getDirectionList() {
        return directionList;
    }

    private void calulateFitness(Maze maze) {
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
        fitness = maze.getDistanceToEnd(i, j);
    }

    @Override
    public String toString() {
        StringBuilder toSend = new StringBuilder();
        for(Direction direction : directionList) {
            toSend.append(direction.toString()).append(", ");
        }
        String newLine = "\n" + fitness;
        toSend.append(newLine);
        return toSend.toString();
    }
}