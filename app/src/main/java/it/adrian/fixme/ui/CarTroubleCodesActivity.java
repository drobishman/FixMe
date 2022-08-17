package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.CarCustomAdapter;
import it.adrian.fixme.adapter.TroubleCodesCustomAdapter;
import it.adrian.fixme.connection.UpdateCarAsyncTask;
import it.adrian.fixme.connection.ViewCarTroubleCodesAsyncTask;
import it.adrian.fixme.connection.ViewCarTroubleCodesResponse;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;

public class CarTroubleCodesActivity extends AppCompatActivity implements ViewCarTroubleCodesResponse {

    public static TroubleCodesCustomAdapter adapter;
    public static ListView troubleCodesListView;

    private TextView registrationNumber;
    private Button checkForTroubleCodes;

    private ArrayList<TroubleCode> troubleCodesList;

    private Car car;
    private TroubleCode troubleCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_trouble_codes);

        car = (Car) getIntent().getSerializableExtra("key");

        registrationNumber = (TextView) findViewById(R.id.info_registration_number);
        checkForTroubleCodes = (Button) findViewById(R.id.btn_check_for_trouble_codes);

        registrationNumber.append(car.getRegistrationNumber());

        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        ViewCarTroubleCodesAsyncTask viewCarTroubleCodesAsyncTask = new ViewCarTroubleCodesAsyncTask(this,
                car.getId());
        viewCarTroubleCodesAsyncTask.response = CarTroubleCodesActivity.this;
        viewCarTroubleCodesAsyncTask.execute();



        checkForTroubleCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Check for Trouble Codes",Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getApplicationContext(), RequestActivity.class);
                myIntent.putExtra("key", car); //Optional parameters
                CarTroubleCodesActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    public void taskResult(ArrayList<TroubleCode> troubleCodes) {
        this.troubleCodesList = troubleCodes;

        troubleCodesListView = (ListView) findViewById(R.id.trouble_codes_list_view);
        adapter = new TroubleCodesCustomAdapter(troubleCodesList,getApplicationContext());
        troubleCodesListView.setAdapter(adapter);

    }
}