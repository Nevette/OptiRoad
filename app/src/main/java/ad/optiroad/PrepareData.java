package ad.optiroad;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class PrepareData {

    public int[][] getDistanceMatrix(List<String> locations) {
        int matrixSize = locations.size();
        int[][] locationsMatrix = new int[matrixSize][matrixSize];
        for (int i=0; i < locations.size(); i++){
            for (int j=0; j < locations.size(); j++){
                if (i == j){
                    continue;
                }
                int distance = checkDistance(locations.get(i), locations.get(j));
                locationsMatrix[i][j] = distance;
                locationsMatrix[j][i] = distance;

            }
        }
        return locationsMatrix;
    }

    private int checkDistance(String firstPoint, String secondPoint){
        if (firstPoint.equals("Gdańsk") && secondPoint.equals("Warszawa")) return 339;
        if (firstPoint.equals("Kraków") && secondPoint.equals("Warszawa")) return 293;
        if (firstPoint.equals("Gdańsk") && secondPoint.equals("Kraków")) return 583;
        if (firstPoint.equals("Gdańsk") && secondPoint.equals("Łódź")) return 340;
        if (firstPoint.equals("Warszawa") && secondPoint.equals("Łódź")) return 136;
        if (firstPoint.equals("Kraków") && secondPoint.equals("Łódź")) return 267;
        if (firstPoint.equals("Gdańsk") && secondPoint.equals("Szczecin")) return 363;
        if (firstPoint.equals("Szczecin") && secondPoint.equals("Warszawa")) return 566;
        if (firstPoint.equals("Szczecin") && secondPoint.equals("Kraków")) return 647;
        if (firstPoint.equals("Szczecin") && secondPoint.equals("Łódź")) return 473;

        if (secondPoint.equals("Gdańsk") && firstPoint.equals("Warszawa")) return 339;
        if (secondPoint.equals("Kraków") && firstPoint.equals("Warszawa")) return 293;
        if (secondPoint.equals("Gdańsk") && firstPoint.equals("Kraków")) return 583;
        if (secondPoint.equals("Gdańsk") && firstPoint.equals("Łódź")) return 340;
        if (secondPoint.equals("Warszawa") && firstPoint.equals("Łódź")) return 136;
        if (secondPoint.equals("Kraków") && firstPoint.equals("Łódź")) return 267;
        if (secondPoint.equals("Gdańsk") && firstPoint.equals("Szczecin")) return 363;
        if (secondPoint.equals("Szczecin") && firstPoint.equals("Warszawa")) return 566;
        if (secondPoint.equals("Szczecin") && firstPoint.equals("Kraków")) return 647;
        if (secondPoint.equals("Szczecin") && firstPoint.equals("Łódź")) return 473;
        return 50;
    }

    public void test() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Create URL
                try {
                    URL mapEndpoint = new URL("https://jsonplaceholder.typicode.com/todos/1");

                    // Create connection
                    HttpsURLConnection myConnection = (HttpsURLConnection) mapEndpoint.openConnection();

                    if (myConnection.getResponseCode() == 200){
                        Log.d("REST","Success: " + myConnection.getResponseMessage());
                    }
                    else{
                        Log.d("REST", "Failure");
                    }
                    InputStream stream = new BufferedInputStream(myConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();

                    String inputString;
                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }

                    JSONObject topLevel = new JSONObject(builder.toString());
                    Log.d("REST", "output: " +  String.valueOf(topLevel));
                }
                catch (Exception e) {
                    Log.d("REST", "Exception");
                }
            }
        });
    }
}
