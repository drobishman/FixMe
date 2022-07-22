package it.adrian.fixme.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.UserLoginAsyncTask;
import it.adrian.fixme.connection.UserLoginResponse;
import it.adrian.fixme.model.User;

public class LoginActivity extends AppCompatActivity implements UserLoginResponse {

    private EditText ssoId;
    private EditText password;
    private Button btnSubmit;
    private Button btnGetTC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        ssoId = (EditText) findViewById(R.id.txt_ssoid);
        password = (EditText) findViewById(R.id.txt_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnGetTC = (Button) findViewById(R.id.btn_gettc);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, password.getText() + " " + ssoId.getText(),
                        Toast.LENGTH_SHORT).show();
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                UserLoginAsyncTask userLoginAsyncTask = new UserLoginAsyncTask(LoginActivity.this, ssoId.getText().toString(), password.getText().toString());
                userLoginAsyncTask.response=LoginActivity.this;
                userLoginAsyncTask.execute();


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

        Intent myIntent = new Intent(LoginActivity.this, UserDetailsActivity.class);
        myIntent.putExtra("key", user); //Optional parameters
        LoginActivity.this.startActivity(myIntent);
    }
}