package com.example.labiryntsolver;

import javafx.util.Pair;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class GeneticAlgorithm {
    private int populationSize;
    private double crossoverProbability, mutationProbability;
    private Maze maze;
    //private ArrayList<Generation> generationList;
    private Generation currentGeneration;

    public GeneticAlgorithm(Maze maze, int populationSize, double crossoverProbability, double mutationProbability) {
        //generationList = new ArrayList<>();
        this.maze = maze;
        this.populationSize = populationSize;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
    }

    public Generation nextGeneration() {
        if(currentGeneration == null)
            return startNewGeneration();
        //if(currentGeneration.getNumber() == 100000) return currentGeneration;
        Generation nextGeneration = currentGeneration.generateNextGeneration();
        //generationList.add(nextGeneration);
        currentGeneration = nextGeneration;
        return currentGeneration;
    }

    public Direction[] getSolutionDirectionList(int nr) {
        if(currentGeneration != null)
            return currentGeneration.getSolutionList()[nr].getDirectionList();
        return null;
    }

    private Generation startNewGeneration() {
        Solution[] solutionList = new Solution[populationSize];
        for(int i = 0; i< populationSize; i++)
            solutionList[i] = Solution.random(maze.getDistanceFromStartToEnd(), maze);
        Generation firstGeneration = new Generation(1, solutionList, maze, crossoverProbability, mutationProbability);
        //generationList.add(firstGeneration);
        currentGeneration = firstGeneration;
        return currentGeneration;
    }
}

class Generation {
    private double crossoverProbability;
    private double mutationProbability;
    private final long nr;
    private final Solution[] solutionList;
    private final Maze maze;

    public Generation(long nr, Solution[] solutionList, Maze maze, double crossoverProbability, double mutationProbability) {
        this.nr = nr;
        this.solutionList = solutionList;
        this.crossoverProbability = crossoverProbability;
        this.mutationProbability = mutationProbability;
        Arrays.sort(this.solutionList, new Comparator<Solution>() {
            @Override
            public int compare(Solution o1, Solution o2) {
                int compareValue = o1.compareTo(o2);
                return Integer.compare(0, compareValue);
            }
        });
        this.maze = maze;
    }

    public long getNumber() {
        return nr;
    }

    public double[] getFitnessScores() {
        double[] fitnessScores = new double[solutionList.length];
        for(int i=0; i< solutionList.length; i++)
            fitnessScores[i] = solutionList[i].getFitness();
        return fitnessScores;
    }

    public double getBestScore() {
        OptionalDouble max = Arrays.stream(getFitnessScores()).max();
        if(max.isPresent())
            return max.getAsDouble();
        return 0.0;
    }

    public Solution[] getSolutionList() { return solutionList; }

    public String[] getSolutionDisplayList() {
        String[] solutionDisplayList = new String[solutionList.length];
        for(int i=0; i<solutionList.length; i++) {
            int solutionNr = i+1;
            DecimalFormat df = new DecimalFormat("#.######");
            df.setRoundingMode(RoundingMode.CEILING);
            solutionDisplayList[i] = "Rozwiązanie: " + solutionNr + ", Fitness: " + df.format(solutionList[i].getFitness());
        }
        return solutionDisplayList;
    }

    public Generation generateNextGeneration() {
        ArrayList<Solution> nextSolutionList = new ArrayList<>();
        Solution parent1 = solutionList[0];
        Solution parent2 = solutionList[1];
        int minLastGoodIndex = Math.min(parent1.getLastGoodIndex(), parent2.getLastGoodIndex());
        Direction[] directions1 = parent1.getDirectionList();
        Direction[] directions2 = parent2.getDirectionList();
        Pair<Direction[], Direction[]> directionsPair = new Pair<>(directions1, directions2);
        while(nextSolutionList.size() < solutionList.length) {
            if(Math.random() > crossoverProbability)
                continue;
            Pair<Direction[], Direction[]> crossedDirections = crossoverSolutions(directionsPair, minLastGoodIndex);
            Direction[] crossedDirections1 = crossedDirections.getKey();
            Direction[] crossedDirections2 = crossedDirections.getValue();
            if(Math.random() <= mutationProbability) {
//                System.out.println("Mutacja");
//                for(Direction direction : crossedDirections1) System.out.print(direction + " ");
//                System.out.println();
                crossedDirections1 = mutateDirections(crossedDirections1, minLastGoodIndex);
//                for(Direction direction : crossedDirections1) System.out.print(direction + " ");
//                System.out.println();
            }

            if(Math.random() <= mutationProbability) {
//                System.out.println("Mutacja");
//                for(Direction direction : crossedDirections2) System.out.print(direction + " ");
//                System.out.println();
                crossedDirections2 = mutateDirections(crossedDirections2, minLastGoodIndex);
//                for(Direction direction : crossedDirections2) System.out.print(direction + " ");
//                System.out.println();
            }
            Solution crossedSolution1 = new Solution(crossedDirections1, maze);
            Solution crossedSolution2 = new Solution(crossedDirections2, maze);

            nextSolutionList.add(crossedSolution1);
            if(nextSolutionList.size() < solutionList.length)
                nextSolutionList.add(crossedSolution2);
        }
        return new Generation(nr + 1, nextSolutionList.toArray(new Solution[0]), maze, crossoverProbability, mutationProbability);
    }

    private double[] createRouletteWheel(double[] fitnessScores) {
        double fitnessSum = 0.0;
        for(double fitness : fitnessScores)
            fitnessSum += fitness;
        double[] rouletteWheel = new double[fitnessScores.length];
        for(int i=0; i<fitnessScores.length; i++)
            rouletteWheel[i] = fitnessScores[i] / fitnessSum;
        return rouletteWheel;
    }

    private Solution getRandomSolution(double[] rouletteWheel) {
        double randomValue = Math.random();
        double baseValue = 0.0;
        for(int i=0; i<rouletteWheel.length; i++) {
            if(baseValue <= randomValue && randomValue <= baseValue + rouletteWheel[i])
                return solutionList[i];
            baseValue += rouletteWheel[i];
        }
        return solutionList[rouletteWheel.length - 1];
    }

    private Pair<Direction[], Direction[]> crossoverSolutions(Pair<Direction[], Direction[]> directionsPair, int minLastGoodIndex) {
        Direction[] directions1 = directionsPair.getKey();
        Direction[] directions2 = directionsPair.getValue();
        int solutionLength = directions1.length;
        int cutPoint = new Random().nextInt(minLastGoodIndex, solutionLength);

        Direction[] first = new Direction[solutionLength];
        Direction[] second = new Direction[solutionLength];
        for(int i = 0; i < solutionLength; i++) {
            if(i <= cutPoint) {
                first[i] = directions1[i];
                second[i] = directions2[i];
            } else {
                first[i] = directions2[i];
                second[i] = directions1[i];
            }
        }
        return new Pair<>(first, second);
    }

    private Direction[] mutateDirections(Direction[] directions, int minLastGoodIndex) {
        int mutationIndex = new Random().nextInt(minLastGoodIndex, directions.length);
        Direction oldDirection = directions[mutationIndex];

        int randomDirectionNumber = new Random().nextInt(Direction.values().length - 1) + 1;
        if (randomDirectionNumber >= oldDirection.toInt())
            randomDirectionNumber++;
        Direction newDirection = Direction.values()[randomDirectionNumber - 1];

        directions[mutationIndex] = newDirection;

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

class Solution implements Comparable<Solution> {
    private final Direction[] directionList;
    private int lastGoodIndex;
    private double fitness;

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

    public double getFitness() {
        return fitness;
    }

    public int getLastGoodIndex() { return lastGoodIndex; }

    public Direction[] getDirectionList() {
        return directionList;
    }

    private void calulateFitness(Maze maze) {
        int i = 0, j = 0;
        boolean breakFlag = false;
        for (int k=0; k<directionList.length; k++) {
            if(!maze.getPossibleDirections(i, j).contains(directionList[k])) {
                lastGoodIndex = k;
                break;
            }
            switch (directionList[k]) {
                case UP -> {
                    if(k > 0 && directionList[k-1] == Direction.DOWN) {
                        breakFlag = true;
                        break;
                    }
                    i--;
                }
                case DOWN -> {
                    if(k > 0 && directionList[k-1] == Direction.UP) {
                        breakFlag = true;
                        break;
                    }
                    i++;
                }
                case LEFT -> {
                    if(k > 0 && directionList[k-1] == Direction.RIGHT) {
                        breakFlag = true;
                        break;
                    }
                    j--;
                }
                case RIGHT -> {
                    if(k > 0 && directionList[k-1] == Direction.LEFT) {
                        breakFlag = true;
                        break;
                    }
                    j++;
                }
            }

            if(breakFlag) {
                lastGoodIndex = k-1;
                break;
            }
        }
        fitness = 1.0 / (maze.getDistanceToEnd(i, j) + 1);
    }

    @Override
    public int compareTo(Solution s) {
        return Double.compare(fitness, s.getFitness());
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