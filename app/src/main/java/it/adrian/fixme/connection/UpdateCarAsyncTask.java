package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.adrian.fixme.ui.UserDetailsActivity;

public class UpdateCarAsyncTask extends AsyncTask<String, String, String> {

    private Activity activity;

    public int id;
    public String chasisNumber;
    public String registrationNumber;
    public String brand;
    public String model;

    public UpdateCarResponse response = null;


    public UpdateCarAsyncTask (Activity activity, int id, String chasisNumber, String registrationNumber, String brand, String model ){

        this.activity = activity;
        this.id = id;
        this.chasisNumber = chasisNumber;
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = "http://dchomekit.go.ro:8763/fixitweb/android/updatecar?" +
                "id="+id+
                "&registrationNumber="+registrationNumber+
                "&chasisNumber="+chasisNumber+
                "&brand="+brand+
                "&model="+model+"";

        Log.d(TAG, "making get request:" + stringUrl);

        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(stringUrl);
            HttpURLConnection httpconn = (HttpURLConnection) url
                    .openConnection();
            if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(httpconn.getInputStream()),
                        8192);
                String strLine;

                while ((strLine = input.readLine()) != null) {
                    response.append(strLine);
                }
                input.close();
            }

            Log.d("AddCarAsyncTask", response.toString());

            String jsonOutput = response.toString();

            return jsonOutput;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        response.taskResult(result);

    }
}
