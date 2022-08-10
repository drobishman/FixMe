package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;
import it.adrian.fixme.R;
import it.adrian.fixme.connection.AddCarAsyncTask;
import it.adrian.fixme.connection.UpdateCarAsyncTask;
import it.adrian.fixme.connection.UpdateCarResponse;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Iterator;

public class UpdateCarActivity extends AppCompatActivity implements UpdateCarResponse {

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
    private Button editCar;

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_car);

        car = (Car) getIntent().getSerializableExtra("key");

        SharedPreferences sharedPreferences = this.getSharedPreferences("myPreferences", 0);
        if (sharedPreferences.contains("user")) {
            final Gson gson = new Gson();

            Log.d("UserDetails", sharedPreferences.getString("user", "").toString());

            user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);
        }

        chasisNumber = (EditText) findViewById(R.id.txt_chasis_number);
        registrationNumber = (EditText) findViewById(R.id.txt_registration_number);
        brand = (EditText) findViewById(R.id.txt_brand);
        model = (EditText) findViewById(R.id.txt_model);

        infoChasisNumber = (TextView) findViewById(R.id.info_chasis_number);
        infoRegistrationNumber = (TextView) findViewById(R.id.info_registration_number);
        infoBrand = (TextView) findViewById(R.id.info_brand);
        infoModel = (TextView) findViewById(R.id.info_model);

        chasisNumber.setText(car.getChasisNumber());
        registrationNumber.setText(car.getRegistrationNumber());
        brand.setText(car.getBrand());
        model.setText(car.getModel());

        editCar = (Button) findViewById(R.id.btn_edit_car);

        editCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chasisNumber.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoChasisNumber.setText(R.string.lbl_chasis_number);
                } else if (registrationNumber.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoRegistrationNumber.setText(R.string.lbl_registration_number);
                } else if (brand.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoBrand.setText(R.string.lbl_brand);
                } else if (model.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoModel.setText(R.string.lbl_model);
                } else {

                    // set new car details to Java object
                    car.setChasisNumber(chasisNumber.getText().toString());
                    car.setRegistrationNumber(registrationNumber.getText().toString());
                    car.setBrand(brand.getText().toString());
                    car.setModel(model.getText().toString());

                    // update in database
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    UpdateCarAsyncTask updateCarAsyncTask = new UpdateCarAsyncTask(UpdateCarActivity.this,
                            car.getId(),
                            registrationNumber.getText().toString(),
                            chasisNumber.getText().toString(),
                            brand.getText().toString(),
                            model.getText().toString());
                    updateCarAsyncTask.response = UpdateCarActivity.this;
                    updateCarAsyncTask.execute();

                    Car tempCar = new Car();

                    // update in shared preferences
                    for (Iterator<Car> carIterator = user.getUserCars().iterator(); carIterator.hasNext(); ) {
                        Car iCar = carIterator.next();
                        if (iCar.equals(car)) {
                            tempCar=iCar;
                        }
                    }

                    user.getUserCars().remove(tempCar); // equals check only if ID are the same
                    user.getUserCars().add(car); // add the car with changed parameters

                    final Gson gson = new Gson();
                    String serializedObject = gson.toJson(user);
                    Log.d("UpdateCarActivity", serializedObject);


                    if (shp == null)
                        shp = getApplicationContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
                    shpEditor = shp.edit();
                    shpEditor.putString("user", serializedObject);
                    shpEditor.commit();

                    //update in view


                    Toast.makeText(getApplicationContext(), "Edited car id: " + car.getId() +" "+ car.getRegistrationNumber(), Toast.LENGTH_LONG).show();

                    finish();

                }
            }
        });
    }

    public void ClearAllMessages (){
        infoChasisNumber.setText("");
        infoRegistrationNumber.setText("");
        infoBrand.setText("");
        infoModel.setText("");
    }

    @Override
    public void taskResult(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    }
}