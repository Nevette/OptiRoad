package ad.optiroad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolveProblem {

    public List<String> orderLocations(List<String> unorderedLocations){
        unorderedLocations = Arrays.asList("Gdańsk", "Kraków", "Warszawa", "Łódź", "Szczecin");

        int[][] distanceMatrix = new PrepareData().getDistanceMatrix(unorderedLocations);
        List<String> orderedLocations = new ArrayList<>();

        List<Integer> sortedIndexes = new TSProblem().runAlgorithm(distanceMatrix);

        for (Integer index : sortedIndexes){
            orderedLocations.add(unorderedLocations.get(index));
        }

        return orderedLocations;
    }
}
