package it.adrian.fixme.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.AddCarTroubleCodeAsyncTask;
import it.adrian.fixme.connection.AddCarTroubleCodeResponse;
import it.adrian.fixme.connection.DeleteCarAsyncTask;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.ui.LoginActivity;
import it.adrian.fixme.ui.UserDetailsActivity;

/**
 * Created by adrian on 19/07/2017.
 */

public class ResultAdapter extends ArrayAdapter<String> implements AddCarTroubleCodeResponse {

    private Context context;
    private ArrayList<String> values;
    private Car car;
    private Location location;

    public ResultAdapter(Context context, ArrayList<String> values, Car car, Location location) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.car = car;
        this.location = location;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String[] vals = values.get(position).split("\\n");

        final MainListHolder mainListHolder;
        View rowView = convertView;
        if (convertView == null) {
            mainListHolder = new MainListHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.result_item, parent, false);
            mainListHolder.text1 = (TextView) rowView.findViewById(R.id.text1);
            mainListHolder.text2 = (TextView) rowView.findViewById(R.id.text2);
            mainListHolder.addTCTocar = (Button) rowView.findViewById(R.id.btn_add_tccar);
            if(car == null)
                mainListHolder.addTCTocar.setEnabled(false);
            rowView.setTag(mainListHolder);
        } else {
            mainListHolder = (MainListHolder) rowView.getTag();
        }
        mainListHolder.text1.setText(vals[0]);

        for (int i = 1; i < vals.length; i++) {
            if (i != 1)
                mainListHolder.text2.setText(mainListHolder.text2.getText() + "\n" + vals[i]);
            else
                mainListHolder.text2.setText(vals[i]);

            mainListHolder.addTCTocar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"Content:" + vals[0],Toast.LENGTH_LONG).show();

                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    AddCarTroubleCodeAsyncTask addCarTroubleCodeAsyncTask = new AddCarTroubleCodeAsyncTask(car.getId(), vals[0], location);
                    addCarTroubleCodeAsyncTask.response = ResultAdapter.this;
                    addCarTroubleCodeAsyncTask.execute();

                }
            });
        }
        return rowView;
    }

    @Override
    public void taskResult(String result) {

    }

    private class MainListHolder {
        private TextView text1;
        private TextView text2;
        private Button addTCTocar;
    }
}
