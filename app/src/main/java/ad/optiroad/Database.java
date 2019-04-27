package ad.optiroad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class Database extends SQLiteOpenHelper {

    private static final String DATABASE = "Database";
    private static final Integer DATABASE_VERSION = 1;

    private static final String ROUTES_TABLE = "routesTable";
    private static final String ROUTE_ID = "id";
    private static final String ROUTE_TITLE = "title";
    private static final String ROUTE_POINTS = "content";

    private static final String[] ROUTES_COLUMN = {ROUTE_ID, ROUTE_TITLE, ROUTE_POINTS};

    public Database(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    public void addRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ROUTE_TITLE, route.getTitle());
        values.put(ROUTE_POINTS, route.getSavedRouteContent());
        db.insert(ROUTES_TABLE, null, values);
        db.close();
    }

    public void saveRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ROUTE_TITLE, route.getTitle());
        values.put(ROUTE_POINTS, route.getSavedRouteContent());

        db.update(ROUTES_TABLE, values, "id = ?", new String[]{route.getId().toString()});
        db.close();
    }

    public void deleteRoute(Route route) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "delete from " + ROUTES_TABLE + " where id = " + route.getId().toString();
        System.out.println(sql);
        db.execSQL(sql);
        db.close();
    }

    public List<Route> getAllRoutes() {
        List<Route> routeList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "select * from " + ROUTES_TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Route route = new Route();
                route.setId(Long.parseLong(cursor.getString(0)));
                route.setTitle(cursor.getString(1));
                route.setContent(cursor.getString(2));
                routeList.add(route);
            }
            while (cursor.moveToNext());
        }
        return routeList;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table " + ROUTES_TABLE + " " +
                "( id integer primary key autoincrement, " +
                "title text," +
                "content text )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int j) {
        String sql = "drop table if exists " + ROUTES_TABLE;
        db.execSQL(sql);
        this.onCreate(db);
    }
}
