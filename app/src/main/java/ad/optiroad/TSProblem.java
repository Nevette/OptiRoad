package ad.optiroad;

import java.util.ArrayList;
import java.util.List;

public class TSProblem {
    int bestDistance = Integer.MAX_VALUE;
    int[][] neighborhoodMatrix = {
            {0, 20, 1, 30},
            {20, 0, 3, 2},
            {1, 3, 0, 3},
            {30, 2, 3, 0}
    };
    int numberOfVertices = 4;
    int initialVertex = 0;
    List<Integer> bestPath;

    public static void main(String[] args) {
        new TSProblem().run();
    }

    public List<Integer> runAlgorithm(int[][] distanceMatrix) {
        neighborhoodMatrix = distanceMatrix;
        numberOfVertices = distanceMatrix.length;
        findShortestPath();
        bestPath.add(0);
        return bestPath;
    }

    public void run() {
        findShortestPath();
        System.out.println("best distance2: " + bestDistance);
        for (Integer i : bestPath) {
            System.out.print(i + " ");
        }
    }

    public void findShortestPath() {
        int currentDistance = 0;
        boolean[] visitedVertices = new boolean[numberOfVertices];
        int currentVertex = initialVertex;
        List<Integer> verticesStack = new ArrayList<>();
        findShortestPath(currentDistance, visitedVertices, currentVertex, verticesStack);
    }

    public void findShortestPath(int currentDistance, boolean[] visitedVertices,
                                 int currentVertex, List<Integer> verticesStack) {
        verticesStack.add(currentVertex);
        System.out.println("current vertex: " + currentVertex);
        if (verticesStack.size() < numberOfVertices) {
            visitedVertices[currentVertex] = true;

            for (int neighbor = 0; neighbor < numberOfVertices; neighbor++) {
                if (neighbor != currentVertex && !visitedVertices[neighbor]) {
                    currentDistance += neighborhoodMatrix[currentVertex][neighbor];
                    findShortestPath(currentDistance, visitedVertices, neighbor,
                            verticesStack);
                    currentDistance -= neighborhoodMatrix[currentVertex][neighbor];
                }
            }
            visitedVertices[currentVertex] = false;
        } else {
            currentDistance += neighborhoodMatrix[currentVertex][initialVertex];
            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestPath = new ArrayList<>(verticesStack);
            }
            currentDistance -= neighborhoodMatrix[currentVertex][initialVertex];
        }
        verticesStack.remove(verticesStack.size() - 1);
    }
}