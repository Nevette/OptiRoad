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
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    Geocoder coder;
    private GoogleMap mMap;
    private ArrayList<String> array;
    private List<LatLng> pointsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coder = new Geocoder(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get points passed in PlanRouteActivity
        array = (ArrayList<String>) getIntent().getSerializableExtra("pointsList");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (String location : array) {
            try {
                List<Address> positionList = coder.getFromLocationName(location, 1);
                if ((positionList).size() <= 0) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Address was not given. Please try again.", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    LatLng address = addressToLatLng(positionList.get(0));
                    LatLng point = new LatLng(address.latitude, address.longitude);
                    pointsList.add(point);
                     mMap.addMarker(new MarkerOptions().position(address).title(location));
                }
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Cannot find given address", Toast.LENGTH_SHORT);
                toast.show();
            }

            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();

            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyBrPt88vvoPDDn_imh-RzCXl5Ha2F2LYig")
                    .build();

            try {
                DirectionsResult res = DirectionsApi.newRequest(context)
                        .mode(TravelMode.DRIVING)
                        .origin(getModelLatLng(pointsList.get(0)))
                        .destination(getModelLatLng(pointsList.get(1)))
                        .await();
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

            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }
            //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(6);
                mMap.addPolyline(opts);
            }
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pointsList.get(0), 6));
        }
        }

    private LatLng addressToLatLng(Address address) {
        return new LatLng(address.getLatitude(), address.getLongitude());
    }

    private String getModelLatLng(LatLng location) {
        return String.valueOf(location.latitude) + "," + location.longitude;
    }
}
