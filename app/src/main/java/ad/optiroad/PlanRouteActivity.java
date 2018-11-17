package ad.optiroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class PlanRouteActivity extends AppCompatActivity {

    private Button buttonNavigate;
    private ImageButton buttonAddNextPoint;
    private EditText inputStartingPoint, inputDestination;
    private static final String TAG = "PlanRouteActivity";
    private ViewGroup layout;
    private List<Integer> pointsList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);
        layout = (ViewGroup) findViewById(R.id.planRouteLayout);

        inputStartingPoint = (EditText) findViewById(R.id.inputStartingPoint);
        inputStartingPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete content of input field and set it to empty
                String startingPoint = inputStartingPoint.getText().toString();
                if (startingPoint.equals("Type your starting point")) {
                    inputStartingPoint.setText("");
                }
            }});

        buttonAddNextPoint = (ImageButton) findViewById(R.id.buttonAddNext);
        buttonAddNextPoint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createNewInputPoint();
            }
        });

        inputDestination = (EditText) findViewById(R.id.inputDestination);
        inputDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete content of input field and set it to empty
                String destination = inputDestination.getText().toString();
                if (destination.equals("Type your destination")) {
                    inputDestination.setText("");
                }
            }});

        buttonNavigate = (Button) findViewById(R.id.buttonNavigate);
        buttonNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNavigateActivity();
            }
        });
    }

    public void openNavigateActivity() {
        Intent i = new Intent(this, MapsActivity.class);
        // Pass starting point to MapsActivity
        i.putExtra("startingPoint", inputStartingPoint.getText().toString());
        i.putExtra("destination", inputDestination.getText().toString());
        startActivity(i);
    }

    public void createNewInputPoint() {
            EditText newPoint = new EditText(PlanRouteActivity.this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            if (pointsList.isEmpty()) {
                params.addRule(RelativeLayout.BELOW, R.id.inputDestination);
                // params.addRule ... (there's a bunch you can add)
                int id = 990;
                newPoint.setId(id);
                pointsList.add(id);
                layout.addView(newPoint, params);
            }
            else {
                int lastPoint = pointsList.get(pointsList.size() -1 );
                params.addRule(RelativeLayout.BELOW, lastPoint);
                int id = lastPoint + 1;
                newPoint.setId(id);
                pointsList.add(id);
                layout.addView(newPoint, params);
            }

    }
}
