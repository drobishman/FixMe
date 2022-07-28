package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.RegisterAsyncTask;
import it.adrian.fixme.connection.RegisterResponse;
import it.adrian.fixme.connection.UserLoginAsyncTask;
import it.adrian.fixme.model.User;

public class RegisterActivity extends AppCompatActivity implements RegisterResponse {

    private EditText usr;
    private EditText psw;
    private EditText fName;
    private EditText lName;
    private EditText email;
    private TextView infoUsr;
    private TextView infoPsw;
    private TextView infoFName;
    private TextView infoLName;
    private TextView infoEmail;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usr = (EditText) findViewById(R.id.txt_usr);
        psw = (EditText) findViewById(R.id.txt_psw);
        fName = (EditText) findViewById(R.id.txt_fname);
        lName = (EditText) findViewById(R.id.txt_lname);
        email = (EditText) findViewById(R.id.txt_mail);
        infoUsr = (TextView) findViewById(R.id.info_usr);
        infoPsw = (TextView) findViewById(R.id.info_psw);
        infoFName = (TextView) findViewById(R.id.info_fname);
        infoLName = (TextView) findViewById(R.id.info_lname);
        infoEmail = (TextView) findViewById(R.id.info_mail);
        btnRegister = (Button) findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usr.getText().toString().equals(""))
                    infoUsr.setText(R.string.lbl_ssoid);
                else if (psw.getText().toString().equals(""))
                    infoPsw.setText(R.string.lbl_password);
                else if (fName.getText().toString().equals(""))
                    infoFName.setText(R.string.lbl_first_name);
                else if (lName.getText().toString().equals(""))
                    infoLName.setText(R.string.lbl_last_name);
                else if (email.getText().toString().equals(""))
                    infoEmail.setText(R.string.lbl_email);
                else {

                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(RegisterActivity.this, usr.getText().toString(), psw.getText().toString());
                    registerAsyncTask.response = RegisterActivity.this;
                    registerAsyncTask.execute();
                }


            }
        });

    }

    @Override
    public void taskResult(User user){
        // TODO here some thing with new registered user
    }
}