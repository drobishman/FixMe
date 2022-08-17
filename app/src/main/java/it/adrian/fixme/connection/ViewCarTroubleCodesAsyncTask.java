package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import it.adrian.fixme.model.TroubleCode;

public class ViewCarTroubleCodesAsyncTask extends AsyncTask<String, String, String> {

    private Activity activity;
    private int carId;
    private ArrayList<TroubleCode> troubleCodes = new ArrayList<TroubleCode>();

    public ViewCarTroubleCodesResponse response;

    public ViewCarTroubleCodesAsyncTask (Activity activity, int carId){
        this.activity = activity;
        this.carId = carId;
    }

    @Override
    protected String doInBackground(String... strings) {
        String stringUrl = "http://dcvideo.go.ro:8763/fixitweb/android/viewtccar?carId=" + carId + "";

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

        Log.d(TAG, "The response:" + result);

        try{
            JSONObject jo = new JSONObject (result);
            JSONArray ja = jo.getJSONArray("troubleCodes");
            for (int n = 0; n < ja.length(); n++) {
                JSONObject jobj = ja.getJSONObject(n);
                TroubleCode troubleCode = new TroubleCode();
                troubleCode.setId(jobj.getInt("id"));
                troubleCode.setNumber(jobj.getString("number"));
                troubleCode.setFaultLocation(jobj.getString("faultLocation"));
                troubleCode.setJob(jobj.getString("job"));
                troubleCodes.add(troubleCode);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        response.taskResult(troubleCodes);
    }
}
