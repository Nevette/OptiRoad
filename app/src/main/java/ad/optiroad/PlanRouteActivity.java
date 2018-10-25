package ad.optiroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PlanRouteActivity extends AppCompatActivity {

    private Button buttonNavigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_route);

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
        startActivity(i);
    }
}
