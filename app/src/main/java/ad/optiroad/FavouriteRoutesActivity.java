package ad.optiroad;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;


public class FavouriteRoutesActivity extends AppCompatActivity {

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

        List<Route> routesList = new ArrayList<>();
        routesList.addAll(db.getAllRoutes());

        for (Route route : routesList) {
            LinearLayout row = new LinearLayout(context);
            createLayoutForFavouritesRoutes(row);

            Button label = new Button(this);
            createAndAddRouteButton(label, row, route);
            favouritesRoutesLayout.addView(row);
            label.setOnClickListener(loadSavedRoute(route));

            ImageView delete = new ImageView(this);
            createAndAddDeleteButton(delete, row);

            delete.setOnClickListener(deleteRowFromFavourites(route));
        }
    }

    private View.OnClickListener deleteRowFromFavourites(final Route routeToDelete) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deleteRoute(routeToDelete);
                initializeFavourites();
            }
        };
    }

    private View.OnClickListener loadSavedRoute(final Route savedRouteToOpen) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSavedRouteInPlanningMode(savedRouteToOpen.getSavedRouteContent());
            }
        };
    }

    public void openSavedRouteInPlanningMode(String savedRouteToOpen) {
        Intent i = new Intent(this, RoutePlanningActivity.class);
        i.putExtra("Route", savedRouteToOpen);
        startActivity(i);
    }

    private void createAndAddDeleteButton(ImageView delete, LinearLayout row) {
        delete.setImageResource(R.drawable.ic_menu_delete);
        delete.setScaleType(ImageView.ScaleType.CENTER);
        delete.setMinimumHeight(50);
        delete.setMinimumWidth(50);
        delete.setClickable(true);
        row.addView(delete);
    }

    private void createAndAddRouteButton(Button label, LinearLayout row, Route route) {
        label.setBackgroundResource(R.drawable.round_button_transparent);
        label.setAllCaps(false);
        label.setTextSize(16);
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
