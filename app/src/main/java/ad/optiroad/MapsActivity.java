package ad.optiroad;

import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String startingPoint;
    private static final String TAG = "MapsActivity";
    Geocoder coder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        coder = new Geocoder(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Get starting point passed in PlanRouteActivity
        startingPoint = (String) getIntent().getSerializableExtra("startingPoint");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            List<Address> positionList = coder.getFromLocationName(startingPoint, 1);
            if (!positionList.isEmpty()) {
                LatLng address = addressToLatLng(positionList.get(0));
                mMap.addMarker(new MarkerOptions().position(address).title("Marker in " + startingPoint));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
            }

        }
        catch (Exception e){
            Toast toast=Toast.makeText(getApplicationContext(),"Cannot find given address",Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    private LatLng addressToLatLng(Address address){
        return new LatLng(address.getLatitude(), address.getLongitude());
    }
}
