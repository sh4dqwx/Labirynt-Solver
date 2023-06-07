package com.example.labiryntsolver;

import javafx.util.Pair;

import java.util.*;

public class GeneticAlgorithm {
    private static int POPULATION_SIZE = 50;
    private Maze maze;
    //private ArrayList<Generation> generationList;
    private Generation currentGeneration;

    public GeneticAlgorithm(Maze maze) {
        //generationList = new ArrayList<>();
        this.maze = maze;
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

    public Direction[] getBestSolutionDirectionList() {
        if(currentGeneration != null)
            return currentGeneration.getSolutionList()[0].getDirectionList();
        return null;
    }

    private Generation startNewGeneration() {
        Solution[] solutionList = new Solution[POPULATION_SIZE];
        for(int i=0; i<POPULATION_SIZE; i++)
            solutionList[i] = Solution.random(maze.getDistanceFromStartToEnd(), maze);
        Generation firstGeneration = new Generation(1, solutionList, maze);
        //generationList.add(firstGeneration);
        currentGeneration = firstGeneration;
        return currentGeneration;
    }
}

class Generation {
    private static double CROSSOVER_PROBABILITY = 0.9;
    private static double MUTATION_PROBABILITY = 0.2;
    private static int HOW_MANY_MUTATIONS = 1;
    private final long nr;
    private final Solution[] solutionList;
    private final Maze maze;

    public Generation(long nr, Solution[] solutionList, Maze maze) {
        this.nr = nr;
        this.solutionList = solutionList;
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

    public Solution[] getSolutionList() {
        return solutionList;
    }

    public Generation generateNextGeneration() {
        ArrayList<Solution> nextSolutionList = new ArrayList<>();
//        double[] rouletteWheel = createRouletteWheel(getFitnessScores());
        Solution parent1 = solutionList[0];
        Solution parent2 = solutionList[1];
        int minLastGoodIndex = Math.min(parent1.getLastGoodIndex(), parent2.getLastGoodIndex());
        Direction[] directions1 = parent1.getDirectionList();
        Direction[] directions2 = parent2.getDirectionList();
        Pair<Direction[], Direction[]> directionsPair = new Pair<>(directions1, directions2);
        while(nextSolutionList.size() < solutionList.length) {
//            Solution parent1 = getRandomSolution(rouletteWheel);
//            Solution parent2 = getRandomSolution(rouletteWheel);
            if(Math.random() > CROSSOVER_PROBABILITY)
                continue;
            Pair<Direction[], Direction[]> crossedDirections = crossoverSolutions(directionsPair, minLastGoodIndex);
            Direction[] crossedDirections1 = crossedDirections.getKey();
            Direction[] crossedDirections2 = crossedDirections.getValue();
            if(Math.random() <= MUTATION_PROBABILITY) {
//                System.out.println("Mutacja");
//                for(Direction direction : crossedDirections1) System.out.print(direction + " ");
//                System.out.println();
                crossedDirections1 = mutateDirections(crossedDirections1, minLastGoodIndex, HOW_MANY_MUTATIONS);
//                for(Direction direction : crossedDirections1) System.out.print(direction + " ");
//                System.out.println();
            }

            if(Math.random() <= MUTATION_PROBABILITY) {
//                System.out.println("Mutacja");
//                for(Direction direction : crossedDirections2) System.out.print(direction + " ");
//                System.out.println();
                crossedDirections2 = mutateDirections(crossedDirections2, minLastGoodIndex, HOW_MANY_MUTATIONS);
//                for(Direction direction : crossedDirections2) System.out.print(direction + " ");
//                System.out.println();
            }
            Solution crossedSolution1 = new Solution(crossedDirections1, maze);
            Solution crossedSolution2 = new Solution(crossedDirections2, maze);

            nextSolutionList.add(crossedSolution1);
            if(nextSolutionList.size() < solutionList.length)
                nextSolutionList.add(crossedSolution2);
        }
        return new Generation(nr + 1, nextSolutionList.toArray(new Solution[0]), maze);
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

    private Direction[] mutateDirections(Direction[] directions, int minLastGoodIndex, int numberOfMutable) {
        ArrayList<Integer> arrayToRandomize = new ArrayList<>();
        for(int i=minLastGoodIndex; i< directions.length; i++) arrayToRandomize.add(i);
        Collections.shuffle(arrayToRandomize);

        for (int i = 0; i < numberOfMutable && i < directions.length; i++) {
            Direction oldDirection = directions[arrayToRandomize.get(i)];

            int randomDirectionNumber = new Random().nextInt(Direction.values().length - 1) + 1;
            if (randomDirectionNumber >= oldDirection.toInt())
                randomDirectionNumber++;
            Direction newDirection = Direction.values()[randomDirectionNumber - 1];

            directions[arrayToRandomize.get(i)] = newDirection;
        }

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