package it.adrian.fixme.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Objects;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.*;
import it.adrian.fixme.connection.TroubleCodesByCategoryResponse;
import it.adrian.fixme.connection.TroubleCodesByNumberAsyncTask;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;
import it.adrian.fixme.utils.PermissionUtils;

public class ResponseActivity extends AppCompatActivity implements TroubleCodesByCategoryResponse {

    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> itemList = new ArrayList<>();
    private Car car;
    private FusedLocationProviderClient fusedLocationClient;
    private Context c;
    private Location location;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_response);

        c = this.getApplicationContext();

        car = (Car) getIntent().getSerializableExtra("key");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViewElements();

        PermissionUtils.askForPermissions(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Toast.makeText(ResponseActivity.this, "Location" + location.getLongitude() + " ; " + location.getLatitude(), Toast.LENGTH_LONG).show();
                            ResponseActivity.this.location = location;
                        }
                    }
                });

        String dtcs = Objects.requireNonNull(getIntent().getExtras()).getString("EXTRA_DTCS");

        if(dtcs==null)
            dtcs = "";
        if(!dtcs.isEmpty()) {
            String[] dtcArray = dtcs.split("\\n");
            String language = getIntent().getExtras().getString("EXTRA_LANGUAGE");
            String vin = getIntent().getExtras().getString("EXTRA_VIN");

            Log.d("DTCS", dtcs);

            for (String dtc: dtcArray) {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                TroubleCodesByNumberAsyncTask troubleCodesByNumberAsyncTask = new TroubleCodesByNumberAsyncTask(this, dtc);
                troubleCodesByNumberAsyncTask.response=this;
                troubleCodesByNumberAsyncTask.execute();
            }

        }else{
            addListItem("No error codes received.");
        }
    }

    @Override
    public void onBackPressed(){

        if(car != null){
        Intent myIntent = new Intent(this, UserDetailsActivity.class);
        this.startActivity(myIntent);
        } else finish();


    }

    private void initViewElements() {

        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(R.string.translation_result);
        ListView listView = (ListView) findViewById(R.id.output);

        Log.d("ItemList",itemList.toString());

        adapter = new ResultAdapter(this, itemList, car, location);
        listView.setAdapter(adapter);
        clearListView();
    }

    public static void addListItem(String value){
        itemList.add(value);
        adapter.notifyDataSetChanged();
    }

    private static void clearListView(){
        itemList.clear();
        adapter.notifyDataSetChanged();
    }

    public Context getC() {
        return c;
    }

    @Override
    public void taskResult(TroubleCode troubleCode) {
        addListItem(troubleCode.getNumber() + " \n " + troubleCode.getFaultLocation());
    }
}
