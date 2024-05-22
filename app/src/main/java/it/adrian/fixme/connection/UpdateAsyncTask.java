package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.adrian.fixme.model.User;

public class UpdateAsyncTask extends AsyncTask<String, String, String> {
    private Activity activity;
    private String usr;
    private String psw;
    private String fName;
    private String lName;
    private String email;
    public UpdateResponse response = null;

    public UpdateAsyncTask (Activity activity, String usr, String psw, String fName, String lName, String email){

        this.activity=activity;
        this.usr=usr;
        this.psw=psw;
        this.fName=fName;
        this.lName=lName;
        this.email=email;
    }


    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://dchomekit.go.ro:8763/fixitweb/android/update?ssoId="+usr+"&password="+psw+"&firstName="+fName+"&lastName="+lName+"&email="+email+"";

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

        User user = new User();

        Log.d("Register status", result.toString());

        if(result==null || result.contains("FAIL")){
            response.taskResult(null);
        }else {
            try {
                JSONObject response = new JSONObject(result);
                //user.setId(response.getInt("id"));
                user.setSsoId(response.getString("ssoId"));
                user.setFirstName(response.getString("firstName"));
                user.setLastName(response.getString("lastName"));
                user.setEmail(response.getString("email"));
                user.setLoggedIn(response.getBoolean("loggedIn"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("User: ", user.getSsoId());
        response.taskResult(user);
    }
}
