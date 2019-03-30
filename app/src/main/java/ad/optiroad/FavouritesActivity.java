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
    private LinearLayout favouritesRoutesLayout;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
        context = this.getBaseContext();
        db = new Database(getBaseContext());
        favouritesRoutesLayout = (LinearLayout) findViewById(R.id.favourites_routes_list);
        initializeFavourites();
    }

    public void initializeFavourites() {
        favouritesRoutesLayout.removeAllViews();

        List<FavouritesRoutes> routesList = new ArrayList<>();
        routesList.addAll(db.returnAllRoutes());

        for (FavouritesRoutes route : routesList) {
            LinearLayout row = new LinearLayout(context);
            createLayoutForFavouritesRoutes(row);

            Button label = new Button(this);
            createAndAddRouteButton(label, row, route);
            favouritesRoutesLayout.addView(row);

            Button delete = new Button(this);
            createAndAddDeleteButton(delete, row);

            delete.setOnClickListener(deleteRowFromFavourites(route));
            label.setOnClickListener(openSavedRoute(route));
        }
    }

    private View.OnClickListener deleteRowFromFavourites(final FavouritesRoutes routeToDelete) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteRoute(routeToDelete);
                initializeFavourites();
            }
        };
    }

    private View.OnClickListener openSavedRoute(final FavouritesRoutes savedRouteToOpen) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSavedRouteInPlanningMode(savedRouteToOpen);
            }
        };
    }

    public void openSavedRouteInPlanningMode(FavouritesRoutes savedRouteToOpen) {
        Intent i = new Intent(this, PlanRouteActivity.class);
        i.putExtra("Route", savedRouteToOpen);
        startActivity(i);
    }

    private void createAndAddDeleteButton(Button delete, LinearLayout row) {
        delete.setBackgroundResource(R.drawable.round_button);
        delete.setTextSize(18);
        delete.setText("Delete");
        delete.setClickable(true);
        row.addView(delete);
    }

    private void createAndAddRouteButton(Button label, LinearLayout row, FavouritesRoutes route) {
        label.setBackgroundResource(R.drawable.round_button);
        label.setTextSize(18);
        label.setClickable(true);
        label.setText(route.getTitle());
        row.addView(label);
    }

    private void createLayoutForFavouritesRoutes(LinearLayout row) {
        row.setLayoutParams(new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
    }
}
