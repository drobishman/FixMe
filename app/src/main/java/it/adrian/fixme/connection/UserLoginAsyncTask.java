package it.adrian.fixme.connection;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;
import it.adrian.fixme.model.User;
import it.adrian.fixme.ui.LoginActivity;

public class UserLoginAsyncTask extends AsyncTask<String, String, String> {

    private LoginActivity loginActivity;
    private String ssoId;
    private String password;
    public UserLoginResponse response = null;

    public UserLoginAsyncTask(LoginActivity loginActivity, String ssoId, String password) {
        this.loginActivity = loginActivity;
        this.ssoId = ssoId;
        this.password = password;

    }

    @Override
    protected String doInBackground(String... strings) {

        String stringUrl = "http://dcvideo.go.ro:8763/fixitweb/android//login?ssoId="+ssoId+"&password="+password+"";

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

        User user = new User();
        Set<Car> carSet = new HashSet<Car>();
        Set<TroubleCode> troubleCodesSet = new HashSet<TroubleCode>();

        //Log.d("UserLoginAsyncTasK", result);

        if(result==null || result.contains("FAIL")){
            response.taskResult(null);
        }else {
            try {
                JSONObject response = new JSONObject(result);
                user.setId(response.getInt("id"));
                user.setSsoId(response.getString("ssoId"));
                user.setFirstName(response.getString("firstName"));
                user.setLastName(response.getString("lastName"));
                user.setEmail(response.getString("email"));
                user.setLoggedIn(response.getBoolean("loggedIn"));
                JSONArray userCars = response.getJSONArray("userCars");
                for (int n = 0; n < userCars.length(); n++) {
                    JSONObject jCar = userCars.getJSONObject(n);
                    Car car = new Car();
                    car.setId(jCar.getInt("id"));
                    car.setRegistrationNumber(jCar.getString("registrationNumber"));
                    car.setChasisNumber(jCar.getString("chasisNumber"));
                    car.setBrand(jCar.getString("brand"));
                    car.setModel(jCar.getString("model"));
                    JSONArray jTroubleCodes = jCar.getJSONArray("troubleCodes");
                    for (int i = 0; i < jTroubleCodes.length(); i++) {
                        JSONObject jTroubleCode = jTroubleCodes.getJSONObject(i);
                        TroubleCode troubleCode = new TroubleCode();
                        troubleCode.setFaultLocation(jTroubleCode.getString("faultLocation"));
                        troubleCode.setNumber(jTroubleCode.getString("number"));
                        troubleCode.setId(jTroubleCode.getInt("id"));
                        troubleCodesSet.add(troubleCode);
                    }
                    car.setCarTroubleCodes(troubleCodesSet);
                    carSet.add(car);
                }
                user.setUserCars(carSet);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("User: ", user.getSsoId());
            response.taskResult(user);
        }
    }
}
