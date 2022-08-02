package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import it.adrian.fixme.R;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;

public class AddCarActivity extends AppCompatActivity {

    private Car car;
    private User user;

    private EditText chasisNumber;
    private EditText registrationNumber;
    private EditText brand;
    private EditText model;
    private TextView infoChasisNumber;
    private TextView infoRegistrationNumber;
    private TextView infoBrand;
    private TextView infoModel;
    private Button addCar;

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        SharedPreferences sharedPreferences = this.getSharedPreferences("myPreferences", 0);
        if (sharedPreferences.contains("user")) {
            final Gson gson = new Gson();

            Log.d("UserDetails", sharedPreferences.getString("user", "").toString());

            user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);

            chasisNumber = (EditText) findViewById(R.id.txt_chasis_number);
            registrationNumber = (EditText) findViewById(R.id.txt_registration_number);
            brand = (EditText) findViewById(R.id.txt_brand);
            model = (EditText) findViewById(R.id.txt_model);

            infoChasisNumber = (TextView) findViewById(R.id.info_chasis_number);
            infoRegistrationNumber = (TextView) findViewById(R.id.info_registration_number);
            infoBrand = (TextView) findViewById(R.id.info_brand);
            infoModel = (TextView) findViewById(R.id.info_model);

            addCar = (Button) findViewById(R.id.btn_add_car);

            addCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO implement class and methods to add a new car to logged user and update server also.
                }
            });


        }
    }
}