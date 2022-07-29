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

import com.google.gson.Gson;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.RegisterAsyncTask;
import it.adrian.fixme.connection.RegisterResponse;
import it.adrian.fixme.model.User;

public class RegisterActivity extends AppCompatActivity implements RegisterResponse {

    private User user;

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

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences sharedPreferences = this.getSharedPreferences("myPreferences", 0);
        if (sharedPreferences.contains("user")) {
            final Gson gson = new Gson();

            Log.d("UserDetails", sharedPreferences.getString("user", "").toString());

            user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);
        }

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

        shp = getSharedPreferences("myPreferences", MODE_PRIVATE);
        CheckLogin();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (usr.getText().toString().equals("")){
                    ClearAllMessages();
                    infoUsr.setText(R.string.lbl_ssoid);
                }
                else if (psw.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoPsw.setText(R.string.lbl_password);
                }
                else if (fName.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoFName.setText(R.string.lbl_first_name);
                }
                else if (lName.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoLName.setText(R.string.lbl_last_name);
                }
                else if (email.getText().toString().equals("")) {
                    ClearAllMessages();
                    infoEmail.setText(R.string.lbl_email);
                }
                else {
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    RegisterAsyncTask registerAsyncTask = new RegisterAsyncTask(RegisterActivity.this,
                            usr.getText().toString(),
                            psw.getText().toString(),
                            fName.getText().toString(),
                            lName.getText().toString(),
                            email.getText().toString()
                    );
                    registerAsyncTask.response = RegisterActivity.this;
                    registerAsyncTask.execute();
                }


            }
        });

    }

    @Override
    public void taskResult(User user){

        if(!user.getSsoId().equals("") || user.getSsoId() != null) {
            try {
                if (shp == null)
                    shp = getSharedPreferences("myPreferences", MODE_PRIVATE);

                final Gson gson = new Gson();
                String serializedObject = gson.toJson(user);
                shpEditor = shp.edit();
                shpEditor.putString("user", serializedObject);
                shpEditor.commit();

                Intent myIntent = new Intent(RegisterActivity.this, UserDetailsActivity.class);
                //myIntent.putExtra("key", user); //Optional parameters
                RegisterActivity.this.startActivity(myIntent);
            } catch (Exception ex) {
                infoUsr.setText(ex.getMessage().toString());
            }
        }
        else{
            infoUsr.setText("Invalid data");
        }
    }

    public void CheckLogin() {

        String userLogged = shp.getString("user", "");

        if (userLogged != null && !userLogged.equals("")) {
            usr.setText(user.getSsoId());
            usr.setEnabled(false);
        }
    }

    public void ClearAllMessages (){
        infoUsr.setText("");
        infoFName.setText("");
        infoLName.setText("");
        infoPsw.setText("");
        infoEmail.setText("");
    }
}