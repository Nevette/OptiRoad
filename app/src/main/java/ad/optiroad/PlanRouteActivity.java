package ad.optiroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlanRouteActivity extends AppCompatActivity {

    private ImageView buttonNavigate, buttonAddNextPoint, buttonSave, buttonFavourites, buttonClear;
    private Database db;
    private Context context;
    private static final String TAG = "PlanRouteActivity";
    private ViewGroup layout;
    private List<Integer> pointsList = new ArrayList();
    private String savedRoutePoints = null;
    private static final String NEW_POINT_TEXT = "Type next point";
    private static final String INITIAL_POINT_TEXT = "Type your starting point";
    private List<AddressLabel> addressLabelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);
        layout = (ViewGroup) findViewById(R.id.nextPointsLayout);
        context = getBaseContext();
        db = new Database(this.getBaseContext());

        savedRoutePoints = openSavedRoute();
        if (savedRoutePoints == null || savedRoutePoints.isEmpty()) {
            createInitialAddressLabel();
        } else {
            loadSavedRoad(savedRoutePoints);
        }

        initNavigationButtons();
    }

    private void loadSavedRoad(String savedRoutePoints) {
        AddressLabel currentField = null;
        String[] splittedSavedRoutePoints = savedRoutePoints.split(",");

        for (String city : splittedSavedRoutePoints) {
            currentField = createAddressLabel(currentField, city);
            addAddressLabel(currentField);
        }
    }

    private void createInitialAddressLabel() {
        AddressLabel label = createAddressLabel(null, "");
        addAddressLabel(label);
    }

    private void createNextAddressLabel() {
        AddressLabel label = createAddressLabel(addressLabelList.get(addressLabelList.size() - 1), "");
        addAddressLabel(label);
    }

    private AddressLabel createAddressLabel(AddressLabel parent, String content) {
        final EditText field = new EditText(PlanRouteActivity.this);
        RelativeLayout.LayoutParams params = createLayoutForUserInput();

        int id = 1;
        if (parent != null) {
            params.addRule(RelativeLayout.BELOW, parent.getInputFieldId());
            id = parent.getInputFieldId() + 1;
            field.setHint(NEW_POINT_TEXT);
        } else {
            field.setHint(INITIAL_POINT_TEXT);
        }
        field.setId(id);
        field.setText(content);
        return new AddressLabel(field, params);
    }

    private void addAddressLabel(AddressLabel addressLabel) {
        layout.addView(addressLabel.inputField, addressLabel.layoutParams);
        pointsList.add(addressLabel.getInputFieldId());
        addressLabelList.add(addressLabel);
    }

    private RelativeLayout.LayoutParams createLayoutForUserInput() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        return params;
    }

    public ArrayList<String> getInputPoints() {
        ArrayList<String> locations = new ArrayList<>();
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            if (point.equals(", ")) {
                continue;
            }
            locations.add(point.getText().toString());
        }
        Log.d("LOCATIONS", "LOCATIONS" + locations);
        return locations;
    }

    private String getTitleForRoute() {
        String title = "";
        int lastPoint = pointsList.size() - 1;
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            if (pointsList.indexOf(id) == 0) {
                title += (point.getText().toString()) + ", ";
            }
            if (pointsList.indexOf(id) == lastPoint) {
                title += (point.getText().toString());
            }
        }
        return title;
    }

    private String getInputPointsAsString() {
        String locations = "";
        int lastPoint = pointsList.size() - 1;
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            if (pointsList.indexOf(id) == lastPoint) {
                locations += (point.getText().toString());
            } else {
                locations += (point.getText().toString()) + ",";
            }
        }
        return locations;
    }

    private void initNavigationButtons() {
        buttonAddNextPoint = (ImageView) findViewById(R.id.buttonAddNext);
        buttonAddNextPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNextAddressLabel();
            }
        });

        buttonNavigate = (ImageView) findViewById(R.id.buttonNavigate);
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigateActivity();
            }
        });

        buttonSave = (ImageView) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRouteToFavourites();
            }
        });

        buttonFavourites = (ImageView) findViewById(R.id.buttonFavourites);
        buttonFavourites.setImageResource(android.R.color.transparent);
        buttonFavourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFavouritesActivity();
            }
        });

        buttonClear = (ImageView) findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInputPoints();
            }
        });
    }

    public void openFavouritesActivity() {
        Intent i = new Intent(this, FavouritesActivity.class);
        startActivity(i);
    }

    public void addRouteToFavourites() {
        FavouritesRoutes routeToSave = new FavouritesRoutes();
        String cities = getInputPointsAsString();
        routeToSave.setContent(cities);
        String routeTitle = getTitleForRoute();
        routeToSave.setTitle(routeTitle);
        db.addRoute(routeToSave);
        createAndShowSuccessToast();
    }

    private void clearInputPoints() {
        layout.removeAllViewsInLayout();
        createInitialAddressLabel();

    }

    public void createAndShowSuccessToast() {
        Toast toast = Toast.makeText(context, "Route successfully saved", Toast.LENGTH_LONG);
        toast.show();
    }

    public String openSavedRoute() {
        String savedPoints = (String) getIntent().getSerializableExtra("Route");
        if (savedPoints != null) {
            return savedPoints;
        }
        return null;
    }

    public void openNavigateActivity() {
        Intent i = new Intent(this, MapsActivity.class);
        // Pass points to MapsActivity
        System.out.println(getInputPoints().toString());
        i.putExtra("pointsList", getInputPoints());
        startActivity(i);
    }

    private class AddressLabel {
        EditText inputField;
        RelativeLayout.LayoutParams layoutParams;

        public AddressLabel(EditText field, RelativeLayout.LayoutParams params) {
            this.inputField = field;
            this.layoutParams = params;
        }

        public Integer getInputFieldId() {
            return inputField.getId();
        }
    }
}