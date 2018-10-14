package ad.optiroad;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    private Button buttonPlanRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        buttonPlanRoute = (Button) findViewById(R.id.buttonPlanRoute);
        buttonPlanRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlanRouteActivity();
            }
        });
    }

    public void openPlanRouteActivity(){
        Intent i = new Intent(this, PlanRouteActivity.class);
        startActivity(i);
    }
}
