package ad.optiroad;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public class ShortestPathCalculatorService extends AsyncTask<List<String>, Integer, List<String>> {
    private final MapViewActivity mapViewActivity;

    public ShortestPathCalculatorService(MapViewActivity mapViewActivity) {
        this.mapViewActivity = mapViewActivity;
    }

    public List<String> orderLocations(List<String> unorderedLocations) {
        int[][] distanceMatrix = new DistanceMatrixGenerator().getDistanceLocationsMatrix(unorderedLocations);
        List<String> orderedLocations = new ArrayList<>();
        List<Integer> sortedIndexes = new TravellingSalesmanProblemSolver().runAlgorithm(distanceMatrix);

        for (Integer index : sortedIndexes) {
            orderedLocations.add(unorderedLocations.get(index));
        }
        return orderedLocations;
    }
    @Override
    protected List<String> doInBackground(List<String>... unorderedLocations){
        return orderLocations(unorderedLocations[0]);
    }

    @Override
    protected void onProgressUpdate(Integer... progress){

    }

    @Override
    protected void onPostExecute(List<String> orderLocations){
        mapViewActivity.loadPathOnMap(orderLocations);
    }
}
