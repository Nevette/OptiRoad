package ad.optiroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class FavouritesActivity extends AppCompatActivity {

    private Context context;
    private LinearLayout savedRoutes;
    private Database db;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favourites);

        context = this.getBaseContext();
        db = new Database(getBaseContext());

        savedRoutes = (LinearLayout) findViewById(R.id.favourites_routes_list);

        refresh();
    }

    public void refresh(){
        savedRoutes.removeAllViews();

        List<SavedRoutes> routesList = new ArrayList<>();
        routesList.addAll(db.returnAllRoutes());

        for (SavedRoutes route: routesList){
            LinearLayout row = new LinearLayout(context);
            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);

            Button label = new Button(this);
            label.setBackgroundResource(R.drawable.round_button);
            label.setTextSize(18);
            label.setClickable(true);
            label.setText(route.getTitle());
            savedRoutes.addView(row);
            row.addView(label);

            Button delete = new Button(this);
            delete.setBackgroundResource(R.drawable.round_button);
            delete.setTextSize(18);
            delete.setText("Delete");
            delete.setClickable(true);
            row.addView(delete);

            label.setOnClickListener(openNote(route));
            delete.setOnClickListener(deleteNote(route));
        }
    }

    private View.OnClickListener deleteNote(final SavedRoutes route){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                db.deleteRoute(route);
                refresh();
            }
        };
    }

    private View.OnClickListener openNote(final SavedRoutes route){
        return new View.OnClickListener(){
            @Override
            public void onClick(View view){
                newRoute(route);
            }
        };
    }

    public void newRoute(SavedRoutes route){
        Intent i = new Intent(this, PlanRouteActivity.class);
        i.putExtra("Route", route);
        startActivity(i);
    }
}
