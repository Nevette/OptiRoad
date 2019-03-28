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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    Geocoder coder;
    private GoogleMap mMap;
    private List<String> sortedLocations;
    private List<LatLng> pointsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coder = new Geocoder(this);
        createMapFragment();

        List<String> unorderedLocations = getUnorderedLocationsList();
        sortedLocations = sortLocationList(unorderedLocations);

        Log.d("Order", "LOCATIONS: " + sortedLocations);
        createAndDisplayToast("Locations:" + sortedLocations);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (String location : sortedLocations) {
            try {
                List<Address> positionList = coder.getFromLocationName(location, 1);
                if ((positionList).size() <= 0) {
                    createAndDisplayToast("Address was not given. Please try again.");
                } else {
                    LatLng point = addressToLatLng(positionList.get(0));
                    pointsList.add(point);
                    mMap.addMarker(new MarkerOptions().position(point).title(location));
                }
            } catch (Exception e) {
                createAndDisplayToast("Cannot find given address");
            }

            List<LatLng> pathToDraw = new ArrayList();

            GeoApiContext geoApiContext = getGeoApiContext();

            for (int locationPoint=1; locationPoint<= pointsList.size(); locationPoint++) {
                try {
                    DirectionsResult directionRequestResult = requestDirection(geoApiContext, locationPoint);
                    getEncodedPolylines(directionRequestResult, pathToDraw);
                }
                catch (Exception ex) {
                    Log.e(TAG, ex.getLocalizedMessage());
                }
                drawPolyline(pathToDraw);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsList.get(0), 6));
            }
        }
    }

    private GeoApiContext getGeoApiContext (){
        return new GeoApiContext.Builder().apiKey("api_key").build();
    }

    public DirectionsResult requestDirection (GeoApiContext geoApiContext, int locationPoint)
            throws ApiException, InterruptedException, IOException {
        return DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.DRIVING)
                .origin(getModelLatLng(pointsList.get(locationPoint-1)))
                .destination(getModelLatLng(pointsList.get(locationPoint)))
                .await();
    }

    public void drawPolyline(List<LatLng> path){
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(6);
            mMap.addPolyline(opts);
        }
    }

    public void createAndDisplayToast(String toastText){
        Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void createMapFragment(){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public List<String> getUnorderedLocationsList() {
        // Get points passed in PlanRouteActivity
        return (ArrayList<String>) getIntent().getSerializableExtra("pointsList");
    }

    public List<String> sortLocationList(List<String> unorderedLocations){
        return new SolveProblem().orderLocations(unorderedLocations);
    }


    private LatLng addressToLatLng(Address address) {
        return new LatLng(address.getLatitude(), address.getLongitude());
    }

    private String getModelLatLng(LatLng location) {
        return String.valueOf(location.latitude) + "," + location.longitude;
    }

    private void getEncodedPolylines(DirectionsResult res, List<LatLng> path){
        //Loop through legs and steps to get encoded polylines of each step
        if (res.routes != null && res.routes.length > 0) {
            DirectionsRoute route = res.routes[0];

            if (route.legs != null) {
                for (int i = 0; i < route.legs.length; i++) {
                    DirectionsLeg leg = route.legs[i];
                    if (leg.steps != null) {
                        for (int j = 0; j < leg.steps.length; j++) {
                            DirectionsStep step = leg.steps[j];
                            if (step.steps != null && step.steps.length > 0) {
                                for (int k = 0; k < step.steps.length; k++) {
                                    DirectionsStep step1 = step.steps[k];
                                    EncodedPolyline points1 = step1.polyline;
                                    if (points1 != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                        for (com.google.maps.model.LatLng coord1 : coords1) {
                                            path.add(new LatLng(coord1.lat, coord1.lng));
                                        }
                                    }
                                }
                            } else {
                                EncodedPolyline points = step.polyline;
                                if (points != null) {
                                    //Decode polyline and add points to list of route coordinates
                                    List<com.google.maps.model.LatLng> coords = points.decodePath();
                                    for (com.google.maps.model.LatLng coord : coords) {
                                        path.add(new LatLng(coord.lat, coord.lng));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
