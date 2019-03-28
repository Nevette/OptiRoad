package ad.optiroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlanRouteActivity extends AppCompatActivity {

    private Button buttonNavigate;
    private Button buttonAddNextPoint;
    private Button buttonSave;
    private Database db;
    private Context context;
    private static final String TAG = "PlanRouteActivity";
    private ViewGroup layout;
    private List<Integer> pointsList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);
        layout = (ViewGroup) findViewById(R.id.nextPointsLayout);
        createNewInputPoint();

        context = getBaseContext();
        db = new Database(this.getBaseContext());

        buttonAddNextPoint = (Button) findViewById(R.id.buttonAddNext);
        buttonAddNextPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewInputPoint();
            }
        });

        buttonNavigate = (Button) findViewById(R.id.buttonNavigate);
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigateActivity();
            }
        });

        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRouteToFavourites();
            }
        });
    }

    public void addRouteToFavourites() {
        FavouritesRoutes routeToSave = new FavouritesRoutes();
        String cities = getInputPointsAsString();
        routeToSave.setContent(cities);
        routeToSave.setTitle(cities);
        db.addRoute(routeToSave);
        createAndShowSuccessToast();
    }

    public void createAndShowSuccessToast(){
        Toast toast = Toast.makeText(context, "Route successfully saved", Toast.LENGTH_LONG);
        toast.show();
    }

    public void openNavigateActivity() {
        Intent i = new Intent(this, MapsActivity.class);
        // Pass points to MapsActivity
        System.out.println(getInputPoints().toString());
        i.putExtra("pointsList", getInputPoints());
        startActivity(i);
    }

    public void createNewInputPoint() {
        final EditText newPoint = new EditText(PlanRouteActivity.this);
        RelativeLayout.LayoutParams params = createLayoutForUserInput();

        if (pointsList.isEmpty()) {
            int id = createInputForStartingPoint(newPoint);
            setIdForNewInput(newPoint, id);
            clearStartingPointInput(newPoint);
            addIdToPointsList(id);
            addInputViewToLayout(newPoint, params);
        } else {
            int lastPoint = pointsList.get(pointsList.size() - 1);
            int id = createNextPointInput(params, newPoint, lastPoint);
            clearNextPointInput(newPoint);
            setIdForNewInput(newPoint, id);
            addIdToPointsList(id);
            addInputViewToLayout(newPoint, params);
        }
    }

    private void addInputViewToLayout(final EditText newPoint, RelativeLayout.LayoutParams params){
        layout.addView(newPoint, params);
    }

    private void setIdForNewInput(final EditText newPoint, int id){
        newPoint.setId(id);
    }

    private RelativeLayout.LayoutParams createLayoutForUserInput(){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        return params;
    }

    private int createInputForStartingPoint(final EditText newPoint){
        int id = 1;
        newPoint.setId(id);
        String text = "Type your starting point";
        newPoint.setText(text);
        return id;
    }

    private void clearStartingPointInput(final EditText newPoint){
        newPoint.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete content of input field and set it to empty
                String startingPointText = newPoint.getText().toString();
                if (startingPointText.equals("Type your starting point")) {
                    newPoint.setText("");
                }
            }
        });
    }

    private int createNextPointInput(RelativeLayout.LayoutParams params, final EditText newPoint,
                                     int lastPoint){
        params.addRule(RelativeLayout.BELOW, lastPoint);
        int id = lastPoint + 1;
        String text = "Type next point";
        newPoint.setText(text);
        return id;
    }

    private void clearNextPointInput(final EditText newPoint){
        newPoint.setOnClickListener(new EditText.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete content of input field and set it to empty
                String startingPointText = newPoint.getText().toString();
                if (startingPointText.equals("Type next point")) {
                    newPoint.setText("");
                }
            }
        });
    }

    private void addIdToPointsList(int id){
        pointsList.add(id);
    }

    public ArrayList<String> getInputPoints() {
        ArrayList<String> locations = new ArrayList<>();
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            locations.add(point.getText().toString());
        }
        return locations;
    }

    private String getInputPointsAsString(){
        String locations = "";
        int lastPoint = pointsList.size() -1;
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            if (pointsList.indexOf(id) == lastPoint) {
                locations += (point.getText().toString());
            }
            else {
                locations += (point.getText().toString()) + ",";
            }
        }
        return locations;
    }
}