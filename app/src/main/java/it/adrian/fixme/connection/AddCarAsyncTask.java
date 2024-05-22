package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddCarAsyncTask extends AsyncTask<String, String, String> {

    public AddCarResponse addCarResponse;

    public Activity activity;
    public String usr;
    public String chasisNumber;
    public String registrationNumber;
    public String brand;
    public String model;

    public AddCarResponse response = null;

    public AddCarAsyncTask (Activity activity, String usr, String chasisNumber, String registrationNumber, String brand, String model ) {

        this.activity = activity;
        this.usr = usr;
        this.chasisNumber = chasisNumber;
        this.registrationNumber = registrationNumber;
        this.brand = brand;
        this.model = model;

    }



    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://dchomekit.go.ro:8763/fixitweb/android/addcar?" +
                "ssoId="+usr+
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
