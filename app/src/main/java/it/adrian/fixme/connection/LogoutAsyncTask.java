package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LogoutAsyncTask extends AsyncTask<String, String, String> {

    public Activity activity;
    public String usr;

    public LogoutResponse response;

    public LogoutAsyncTask (Activity activity, String usr){

        this.activity=activity;
        this.usr=usr;
    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://dcvideo.go.ro:8763/fixitweb/android/logoff?ssoId="+usr+"";

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

            Log.d("RegisterAsyncTask", response.toString());

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

        Log.d("Register status", result.toString());

        if(result==null || result.contains("FAIL")){
            response.taskResult(null);
        }else
            response.taskResult(result);
    }
}
