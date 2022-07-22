package it.adrian.fixme.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import it.adrian.fixme.R;

public class LoginActivity extends AppCompatActivity {

    private EditText ssoID;
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

        ssoID = (EditText) findViewById(R.id.txt_ssoID);
        password = (EditText) findViewById(R.id.txt_password);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnGetTC = (Button) findViewById(R.id.btn_gettc);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Toast.makeText(LoginActivity.this, password.getText() + "" + ssoID.getText(),
                        Toast.LENGTH_SHORT).show();

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
}