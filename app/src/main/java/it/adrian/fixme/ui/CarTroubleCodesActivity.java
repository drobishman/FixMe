package it.adrian.fixme.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.CarCustomAdapter;
import it.adrian.fixme.adapter.TroubleCodesCustomAdapter;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;

public class CarTroubleCodesActivity extends AppCompatActivity {

    public static TroubleCodesCustomAdapter adapter;
    public static ListView troubleCodesListView;

    private TextView registrationNumber;
    private Button checkForTroubleCodes;

    private ArrayList<TroubleCode> troubleCodesList;

    private Car car;
    private TroubleCode troubleCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_trouble_codes);

        registrationNumber = (TextView) findViewById(R.id.info_registration_number);
        checkForTroubleCodes = (Button) findViewById(R.id.btn_check_for_trouble_codes);

        troubleCodesListView = (ListView) findViewById(R.id.trouble_codes_list_view);
        troubleCodesList = new ArrayList<TroubleCode>();
        adapter = new TroubleCodesCustomAdapter(troubleCodesList,getApplicationContext());
        troubleCodesListView.setAdapter(adapter);
    }
}