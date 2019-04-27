package ad.optiroad;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import com.google.maps.model.TravelMode;

import java.util.List;

public class DistanceMatrixGenerator {

    private static final GeoApiContext context = MapViewActivity.getGeoApiContext();

    public int[][] transferDistanceMatrixToLocationsMatrix(DistanceMatrix response) {
        int[][] locationsMatrix = new int[response.rows.length][response.rows.length];
        for (int i = 0; i < response.rows.length; i++) {
            DistanceMatrixElement[] rowElements = response.rows[i].elements;
            for (int j = 0; j < rowElements.length; j++) {
                DistanceMatrixElement element = rowElements[j];
                int distance = Long.valueOf(element.distance.inMeters).intValue();
                locationsMatrix[i][j] = distance;
                locationsMatrix[j][i] = distance;
            }
        }
        return locationsMatrix;
    }

    public int[][] getDistanceLocationsMatrix(List<String> locations) {
        DistanceMatrix response = getDistanceMatrix(locations);
        return transferDistanceMatrixToLocationsMatrix(response);
    }

    public DistanceMatrix getDistanceMatrix(List<String> locations) {
        try {
            DistanceMatrixApiRequest req = DistanceMatrixApi.newRequest(context);
            return req.origins(locations.toArray(new String[locations.size()]))
                    .destinations(locations.toArray(new String[locations.size()]))
                    .mode(TravelMode.DRIVING)
                    .language("pl")
                    .await();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
