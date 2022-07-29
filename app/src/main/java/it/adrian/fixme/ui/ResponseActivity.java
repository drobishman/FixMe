package it.adrian.fixme.ui;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.adapter.*;
import it.adrian.fixme.connection.TroubleCodesByCategoryResponse;
import it.adrian.fixme.connection.TroubleCodesByNumberAsyncTask;
import it.adrian.fixme.model.TroubleCode;

public class ResponseActivity extends AppCompatActivity implements TroubleCodesByCategoryResponse {

    private static ArrayAdapter<String> adapter;
    private static ArrayList<String> itemList = new ArrayList<>();

    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_response);
        c = this.getApplicationContext();
        initViewElements();

        String dtcs = getIntent().getExtras().getString("EXTRA_DTCS");
        if(dtcs==null)
            dtcs = "";
        if(!dtcs.equals("")) {
            String[] dtcArray = dtcs.split("\\n");
            String language = getIntent().getExtras().getString("EXTRA_LANGUAGE");
            String vin = getIntent().getExtras().getString("EXTRA_VIN");

            Log.d("DTCS", dtcs);

            for (String dtc: dtcArray) {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                TroubleCodesByNumberAsyncTask troubleCodesByNumberAsyncTask = new TroubleCodesByNumberAsyncTask(this, dtc);
                troubleCodesByNumberAsyncTask.response=this;
                troubleCodesByNumberAsyncTask.execute();
            }

        }else{
            addListItem("No error codes received.");
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    private void initViewElements() {
        if(getSupportActionBar()!=null)
            getSupportActionBar().setTitle(R.string.translation_result);
        ListView listView = (ListView) findViewById(R.id.output);

        Log.d("ItemList",itemList.toString());

        adapter = new ResultAdapter(this, itemList);
        listView.setAdapter(adapter);
        clearListView();
    }

    public static void addListItem(String value){
        itemList.add(value);
        adapter.notifyDataSetChanged();
    }

    private static void clearListView(){
        itemList.clear();
        adapter.notifyDataSetChanged();
    }

    public Context getC() {
        return c;
    }

    @Override
    public void taskResult(TroubleCode troubleCode) {
        Log.d("TroubleCode = ",troubleCode.getNumber() + " \n " + troubleCode.getFaultLocation());
        addListItem(troubleCode.getNumber() + " \n " + troubleCode.getFaultLocation());
    }
}
