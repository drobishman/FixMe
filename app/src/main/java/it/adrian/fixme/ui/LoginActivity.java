package it.adrian.fixme.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.UserLoginAsyncTask;
import it.adrian.fixme.connection.UserLoginResponse;
import it.adrian.fixme.model.User;
import it.adrian.fixme.utils.PermissionUtils;

public class LoginActivity extends AppCompatActivity implements UserLoginResponse {

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;

    private EditText ssoId;
    private EditText password;
    private Button btnSubmit;
    private Button btnGetTC;
    private Button btnRegister;
    private TextView txtInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addListenerOnButton();
        PermissionUtils.askForPermissions(this);
    }

    public void addListenerOnButton() {

        ssoId = (EditText) findViewById(R.id.txt_ssoid);
        password = (EditText) findViewById(R.id.txt_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnGetTC = (Button) findViewById(R.id.btn_gettc);
        btnRegister = (Button) findViewById(R.id.btn_register);
        txtInfo = findViewById(R.id.txt_info);

        shp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        CheckLogin();

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (ssoId.getText().toString().equals("") || password.getText().toString().equals(""))
                    txtInfo.setText("Please insert userid and password");
                else{
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    UserLoginAsyncTask userLoginAsyncTask = new UserLoginAsyncTask(LoginActivity.this,
                            ssoId.getText().toString(),
                            password.getText().toString());
                    userLoginAsyncTask.response = LoginActivity.this;
                    userLoginAsyncTask.execute();
                }
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                LoginActivity.this.startActivity(myIntent);
            }
        });

        btnGetTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RequestActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                LoginActivity.this.startActivity(myIntent);
            }
        });

    }

    @Override
    public void taskResult(User user) {

        if(user == null)
            txtInfo.setText("Invalid username and password.");
        else
        if(!user.getSsoId().equals("") || user.getSsoId() != null) {
            try {
                if (shp == null)
                    shp = getSharedPreferences("myPreferences", MODE_PRIVATE);

                final Gson gson = new Gson();
                String serializedObject = gson.toJson(user);
                shpEditor = shp.edit();
                shpEditor.putString("user", serializedObject);
                shpEditor.commit();

                Intent myIntent = new Intent(LoginActivity.this, UserDetailsActivity.class);
                //myIntent.putExtra("key", user); //Optional parameters
                LoginActivity.this.startActivity(myIntent);
            } catch (Exception ex) {
                txtInfo.setText(ex.getMessage().toString());
            }
        }
        else{
            txtInfo.setText("Invalid username and password.");
        }
    }

    public void CheckLogin() {

        String userLogged = shp.getString("user", "");

        if (userLogged != null && !userLogged.equals("")) {
            Intent myIntent = new Intent(LoginActivity.this, UserDetailsActivity.class);
            LoginActivity.this.startActivity(myIntent);
        }
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}