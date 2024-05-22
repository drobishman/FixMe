package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;

public class AddCarTroubleCodeAsyncTask extends AsyncTask<String, String, String> {

    public AddCarTroubleCodeResponse response;

    public int id;
    public String number;

    public AddCarTroubleCodeAsyncTask (int id, String number){

        this.id = id;
        this.number = number;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = "http://dchomekit.go.ro:8763/fixitweb/android/addtccar?" +
                "&id="+id+
                "&number="+number+"";

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
