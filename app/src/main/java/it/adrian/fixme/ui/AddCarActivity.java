package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;

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

import it.adrian.fixme.R;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;

public class AddCarActivity extends AppCompatActivity {

    private Car car = new Car ();
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

                    if (chasisNumber.getText().toString().equals("")){
                        ClearAllMessages();
                        infoChasisNumber.setText(R.string.lbl_chasis_number);
                    }
                    else  if (registrationNumber.getText().toString().equals("")){
                        ClearAllMessages();
                        infoRegistrationNumber.setText(R.string.lbl_chasis_number);
                    }
                    else  if (brand.getText().toString().equals("")){
                        ClearAllMessages();
                        infoBrand.setText(R.string.lbl_chasis_number);
                    }
                    else  if (model.getText().toString().equals("")){
                        ClearAllMessages();
                        infoModel.setText(R.string.lbl_chasis_number);
                    }
                    else {

                        // set new car details to Java object
                        car.setChasisNumber(chasisNumber.getText().toString());
                        car.setRegistrationNumber(registrationNumber.getText().toString());
                        car.setBrand(brand.getText().toString());
                        car.setModel(model.getText().toString());

                        Log.d("AddNewCar", user.getSsoId()+" "+car.toString());

                        user.getUserCars().add(car);

                        // TODO ADD CALL TO SERVER TO ADD THE NEW CAR

                        try {
                            if (shp == null)
                                shp = getSharedPreferences("myPreferences", MODE_PRIVATE);

                            final Gson gson = new Gson();
                            String serializedObject = gson.toJson(user);
                            shpEditor = shp.edit();
                            shpEditor.putString("user", serializedObject);
                            shpEditor.commit();


                            Intent myIntent = new Intent(AddCarActivity.this, UserDetailsActivity.class);
                            //myIntent.putExtra("key", user); //Optional parameters
                            AddCarActivity.this.startActivity(myIntent);
                        } catch (Exception ex) {
                            Toast.makeText(AddCarActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });


        }
    }

    public void ClearAllMessages (){
        infoChasisNumber.setText("");
        infoRegistrationNumber.setText("");
        infoBrand.setText("");
        infoModel.setText("");
    }
}