package it.adrian.fixme.connection;

import android.app.Activity;
import android.os.AsyncTask;

public class AddCarAsyncTask extends AsyncTask<String, String, String> {

    public AddCarResponse addCarResponse;

    public Activity activity;
    public String usr;
    public String chasisNumber;
    public String registrationNumber;
    public String brand;
    public String model;

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
        return null;
    }
}
