package it.adrian.fixme.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.CarCustomAdapter;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;

public class UserDetailsActivity extends AppCompatActivity {

    private User user;
    private static CarCustomAdapter adapter;

    ListView carsListView;

    private TextView welcome;
    private Button btnLogOut;

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

        welcome.append(user.getFirstName()+ " " + user.getLastName());

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });

        carsListView = (ListView) findViewById(R.id.cars_listView);

        ArrayList<Car> carsList = new ArrayList<>(user.getUserCars());

        adapter = new CarCustomAdapter(carsList,getApplicationContext());
        carsListView.setAdapter(adapter);

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
}