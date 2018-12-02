package ad.optiroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class PlanRouteActivity extends AppCompatActivity {

    private Button buttonNavigate;
    private Button buttonAddNextPoint;
    private static final String TAG = "PlanRouteActivity";
    private ViewGroup layout;
    private List<Integer> pointsList = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);
        layout = (ViewGroup) findViewById(R.id.nextPointsLayout);
        createNewInputPoint();

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
    }

    public void openNavigateActivity() {
        Intent i = new Intent(this, MapsActivity.class);
        // Pass points to MapsActivity
        System.out.println(getContent().toString());
        i.putExtra("pointsList", getContent());
        startActivity(i);
    }

    public void createNewInputPoint() {
        final EditText newPoint = new EditText(PlanRouteActivity.this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        if (pointsList.isEmpty()) {
            int id = 990;
            newPoint.setId(id);
            String text = "Type your starting point";
            newPoint.setText(text);
            newPoint.setText(text);
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
            pointsList.add(id);
            layout.addView(newPoint, params);
        } else {
            int lastPoint = pointsList.get(pointsList.size() - 1);
            params.addRule(RelativeLayout.BELOW, lastPoint);
            int id = lastPoint + 1;
            String text = "Type next point";
            newPoint.setText(text);
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
            newPoint.setId(id);
            pointsList.add(id);
            layout.addView(newPoint, params);
        }
    }

    public ArrayList<String> getContent() {
        ArrayList<String> locations = new ArrayList<>();
        for (int id : pointsList) {
            EditText point = (EditText) findViewById(id);
            locations.add(point.getText().toString());
        }
        return locations;
    }
}