package it.adrian.fixme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import it.adrian.fixme.R;
import it.adrian.fixme.model.User;

public class UserDetailsActivity extends AppCompatActivity {

    private TextView id;
    private TextView ssoId;
    private TextView password;
    private TextView firstName;
    private TextView lastName;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        //TODO view user details

        User user = (User) getIntent().getSerializableExtra("key");
        Log.d("UserDetails", user.getSsoId());

        id = (TextView) findViewById(R.id.id);
        ssoId = (TextView) findViewById(R.id.ssoid);
        password = (TextView) findViewById(R.id.password);
        firstName = (TextView) findViewById(R.id.first_name);
        lastName = (TextView) findViewById(R.id.last_name);
        email = (TextView) findViewById(R.id.email);

        id.append(user.getId().toString());
        ssoId.append(user.getSsoId());
        password.append("NULL");
        firstName.append(user.getFirstName());
        lastName.append(user.getLastName());
        email.append(user.getEmail());
    }
}