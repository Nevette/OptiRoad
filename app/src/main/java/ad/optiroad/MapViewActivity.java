package ad.optiroad;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapViewActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    Geocoder coder;
    private GoogleMap mMap;
    private List<LatLng> pointsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coder = new Geocoder(this);
        createMapFragment();

        List<String> unorderedLocations = getUnorderedLocationsList();
        sortLocationList(unorderedLocations);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    public static GeoApiContext getGeoApiContext() {
        return new GeoApiContext.Builder().apiKey("API_KEY").build();
    }

    public DirectionsResult requestDirection(GeoApiContext geoApiContext, int locationPoint)
            throws ApiException, InterruptedException, IOException {
        return DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(getModelLatLng(pointsList.get(locationPoint - 1)))
                .destination(getModelLatLng(pointsList.get(locationPoint)))
                .await();
    }

    public void drawPolyline(List<LatLng> path) {
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(6);
            mMap.addPolyline(opts);
        }
    }

    public void createAndDisplayToast(String toastText) {
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void createMapFragment() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public List<String> getUnorderedLocationsList() {
        // Get points passed in PlanRouteActivity
        return (ArrayList<String>) getIntent().getSerializableExtra("pointsList");
    }

    public void sortLocationList(List<String> unorderedLocations) {
        new ShortestPathCalculatorService(this).execute(unorderedLocations);
    }

    public void loadPathOnMap(List<String> sortedLocations) {
        for (String location : sortedLocations) {
            try {
                List<Address> positionList = coder.getFromLocationName(location, 1);
                if ((positionList).size() <= 0) {
                    createAndDisplayToast("Address was not given. Please try again.");
                } else {
                    LatLng point = addressToLatLng(positionList.get(0));
                    pointsList.add(point);
                    mMap.addMarker(new MarkerOptions().position(point).title(String.valueOf(sortedLocations.indexOf(location) + 1)).draggable(false));
                }
            } catch (Exception e) {
                createAndDisplayToast("Cannot find given address");
            }

            List<LatLng> pathToDraw = new ArrayList();

            GeoApiContext geoApiContext = getGeoApiContext();

            for (int locationPoint = 1; locationPoint <= pointsList.size(); locationPoint++) {
                try {
                    DirectionsResult directionRequestResult = requestDirection(geoApiContext, locationPoint);
                    getEncodedPolylines(directionRequestResult, pathToDraw);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getLocalizedMessage());
                }
                drawPolyline(pathToDraw);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsList.get(0), 6));
            }
        }
    }

    private LatLng addressToLatLng(Address address) {
        return new LatLng(address.getLatitude(), address.getLongitude());
    }

    private String getModelLatLng(LatLng location) {
        return String.valueOf(location.latitude) + "," + location.longitude;
    }

    private void getEncodedPolylines(DirectionsResult directionRequestResult, List<LatLng> path) {
        //Loop through legs and steps to get encoded polylines of each step
        DirectionsRoute route = processDirectionsApiResponse(directionRequestResult);

        if (route != null && route.legs != null) {
            for (int i = 0; i < route.legs.length; i++) {
                DirectionsLeg leg = route.legs[i];
                if (leg.steps != null) {
                    for (int j = 0; j < leg.steps.length; j++) {
                        DirectionsStep step = leg.steps[j];
                        List<com.google.maps.model.LatLng> coords;
                        if (step.steps != null && step.steps.length > 0) {
                            coords = getPolylineForSubsteps(step);
                        } else {
                            coords = getPolylineForStep(step);
                        }
                        addPointsToRouteCoordinatns(coords, path);
                    }
                }
            }
        }
    }

    private DirectionsRoute processDirectionsApiResponse(DirectionsResult directionRequestResult) {
        if (directionRequestResult.routes != null && directionRequestResult.routes.length > 0) {
            return directionRequestResult.routes[0];
        }
        return null;
    }

    private List<com.google.maps.model.LatLng> decodePolyline(EncodedPolyline points) {
        return points.decodePath();
    }

    private void addPointsToRouteCoordinatns(List<com.google.maps.model.LatLng> coords, List<LatLng> path) {
        for (com.google.maps.model.LatLng coord : coords) {
            path.add(new LatLng(coord.lat, coord.lng));
        }
    }

    private List<com.google.maps.model.LatLng> getPolylineForSubsteps(DirectionsStep step) {
        for (int k = 0; k < step.steps.length; k++) {
            DirectionsStep substep = step.steps[k];
            EncodedPolyline pointsWithSubsteps = substep.polyline;
            if (pointsWithSubsteps != null) {
                //Decode polyline and add points to list of route coordinates
                return decodePolyline(pointsWithSubsteps);
            }
        }
        return null;
    }

    private List<com.google.maps.model.LatLng> getPolylineForStep(DirectionsStep step) {
        EncodedPolyline points = step.polyline;
        if (points != null) {
            //Decode polyline and add points to list of route coordinates
            return decodePolyline(points);
        }
        return null;
    }
}

