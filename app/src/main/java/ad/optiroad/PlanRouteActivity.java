package ad.optiroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PlanRouteActivity extends AppCompatActivity {

    private Button buttonNavigate;
    private EditText inputStartingPoint;
    private static final String TAG = "PlanRouteActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

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
        startActivity(i);
    }
}
