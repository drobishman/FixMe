package it.adrian.fixme.connection;

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
import java.util.List;

import it.adrian.fixme.model.TroubleCode;
import it.adrian.fixme.ui.ResponseActivity;


public class TroubleCodesByNumberAsyncTask extends AsyncTask<String, String, String> {

    private ResponseActivity activity;
    private String troubleCodeNumber;
    public TroubleCodesByCategoryResponse response = null;
    private List<TroubleCode> troubleCodeList = new ArrayList<>();

    /**
     * default constructor method
     * @param activity caller activity
     * @param troubleCodeNumber the id of the category
     */
    public TroubleCodesByNumberAsyncTask(ResponseActivity activity, String troubleCodeNumber){

        this.activity = activity;
        this.troubleCodeNumber = troubleCodeNumber;
    }


    /**
     * Http request
     *
     * @param strings default parameter
     * @return null
     */
    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "dcvideo.go.ro:8763/fixitweb/android/trouble_code-"+ troubleCodeNumber +"";

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

    /**
     * all pois will be created and added to a List of Pois and send as response
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        TroubleCode troubleCode = new TroubleCode();

        if(result==null){
            response.taskResult(null);
        }else
            try {
                JSONObject response = new JSONObject(result);
                troubleCode.setId(response.getInt("id"));
                troubleCode.setNumber(response.getString("number"));
                troubleCode.setFaultLocation(response.getString("faultLocation"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        Log.d("TroubleCode: ", result);
        response.taskResult(troubleCode);
    }
}