package it.adrian.fixme.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.CarCustomAdapter;
import it.adrian.fixme.connection.LogoutAsyncTask;
import it.adrian.fixme.connection.LogoutResponse;
import it.adrian.fixme.connection.UserLoginAsyncTask;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;

public class UserDetailsActivity extends AppCompatActivity implements LogoutResponse {

    private User user;
    private static CarCustomAdapter adapter;

    ListView carsListView;

    private TextView welcome;
    private Button btnLogOut;
    private Button btnEditUser;

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        SharedPreferences sharedPreferences = this.getSharedPreferences("myPreferences", 0);
        if (sharedPreferences.contains("user")) {
            final Gson gson = new Gson();

            Log.d("UserDetails", sharedPreferences.getString("user", "").toString());

            user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);
        }

        //User user = (User) getIntent().getSerializableExtra("key");
        //Log.d("UserDetails", user.getSsoId());

        welcome = (TextView) findViewById(R.id.welcome);
        btnLogOut = (Button) findViewById(R.id.btn_logout);
        btnEditUser = (Button) findViewById(R.id.btn_edit_user);

        welcome.append(user.getFirstName()+ " " + user.getLastName());

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                LogoutAsyncTask logoutAsyncTask = new LogoutAsyncTask(UserDetailsActivity.this,
                        user.getSsoId());
                logoutAsyncTask.response = UserDetailsActivity.this;
                logoutAsyncTask.execute();
            }
        });

        btnEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(UserDetailsActivity.this, UpdateActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                UserDetailsActivity.this.startActivity(myIntent);
            }
        });

        carsListView = (ListView) findViewById(R.id.cars_listView);

        ArrayList<Car> carsList = new ArrayList<>(user.getUserCars());

        adapter = new CarCustomAdapter(carsList,getApplicationContext());
        carsListView.setAdapter(adapter);

        carsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Car car= carsList.get(position);

                Snackbar.make(view, car.getRegistrationNumber()+"\n"+car.getChasisNumber()+" API: "+car.getModel(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
            }
        });
    }


    public void Logout() {
        try {
            if (shp == null)
                shp = getSharedPreferences("myPreferences", MODE_PRIVATE);

            shpEditor = shp.edit();
            shpEditor.putString("user", "");
            shpEditor.commit();

            Intent myIntent = new Intent(UserDetailsActivity.this, LoginActivity.class);
            UserDetailsActivity.this.startActivity(myIntent);

        } catch (Exception ex) {
            Toast.makeText(UserDetailsActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    @Override
    public void taskResult(String result) {
        if(result.contains("SUCCESS"))
            Logout();
        else
            Toast.makeText(UserDetailsActivity.this, "Logout error! Server error "+result+"", Toast.LENGTH_LONG).show();
    }
}