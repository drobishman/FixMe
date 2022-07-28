package it.adrian.fixme.connection;

import android.app.Activity;
import android.os.AsyncTask;

public class RegisterAsyncTask extends AsyncTask<String, String, String> {

    public RegisterAsyncTask (Activity activity, String usr, String psw, String )

    public RegisterResponse response = null;

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        response.taskResult(null);
    }
}
