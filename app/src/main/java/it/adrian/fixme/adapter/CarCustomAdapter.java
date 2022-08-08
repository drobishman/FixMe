package it.adrian.fixme.adapter;


import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

import it.adrian.fixme.R;
import it.adrian.fixme.connection.DeleteCarAsyncTask;
import it.adrian.fixme.connection.DeleteCarResponse;
import it.adrian.fixme.connection.UserLoginAsyncTask;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.User;
import it.adrian.fixme.ui.AddCarActivity;
import it.adrian.fixme.ui.LoginActivity;
import it.adrian.fixme.ui.UpdateCarActivity;
import it.adrian.fixme.ui.UserDetailsActivity;

public class CarCustomAdapter extends ArrayAdapter<Car> implements DeleteCarResponse {

    private ArrayList<Car> dataSet;
    Context mContext;

    private User user;
    private Car car = new Car();

    SharedPreferences shp;
    SharedPreferences.Editor shpEditor;

    // View lookup cache
    private static class ViewHolder {
        TextView registrationNumber;
        TextView chasisNumber;
        TextView brand;
        TextView model;

        Button carTroubleCodes;
        Button editCar;
        Button deleteCar;
    }

    public CarCustomAdapter(ArrayList<Car> data, Context context) {
        super(context, R.layout.car_view, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Car dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;




        SharedPreferences sharedPreferences = getContext().getSharedPreferences("myPreferences", 0);
        if (sharedPreferences.contains("user")) {
            final Gson gson = new Gson();

            Log.d("UserDetails", sharedPreferences.getString("user", "").toString());

            user = gson.fromJson(sharedPreferences.getString("user", ""), User.class);
        }

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.car_view, parent, false);
            viewHolder.registrationNumber = (TextView) convertView.findViewById(R.id.registration_number);
            viewHolder.chasisNumber = (TextView) convertView.findViewById(R.id.chasis_number);
            viewHolder.brand = (TextView) convertView.findViewById(R.id.brand);
            viewHolder.model = (TextView) convertView.findViewById(R.id.model);
            viewHolder.carTroubleCodes = (Button) convertView.findViewById(R.id.btn_car_trouble_codes);
            viewHolder.editCar = (Button) convertView.findViewById(R.id.btn_edit_car);
            viewHolder.deleteCar =(Button) convertView.findViewById(R.id.btn_delete_car);
            viewHolder.deleteCar.setTag(position);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.chasisNumber.setText("Chasis number: " + dataModel.getChasisNumber());
        viewHolder.registrationNumber.setText("Registration number: " + dataModel.getRegistrationNumber());
        viewHolder.brand.setText("Brand: " + dataModel.getBrand());
        viewHolder.model.setText("Model: " + dataModel.getModel());

        car.setRegistrationNumber(dataModel.getRegistrationNumber());
        car.setChasisNumber(dataModel.getChasisNumber());
        car.setBrand(dataModel.getBrand());
        car.setModel(dataModel.getModel());

        viewHolder.editCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dataModel.getId() == null) {
                    Toast.makeText(getContext(), "Can't delete now. Id: " + dataModel.getId(), Toast.LENGTH_LONG).show();
                } else {
                    Intent myIntent = new Intent(getContext(), UpdateCarActivity.class);
                    myIntent.putExtra("key", dataModel); //Optional parameters
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(myIntent);
                }
            }
        });

        viewHolder.carTroubleCodes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Car trouble codes button"+ dataModel.getId(), Toast.LENGTH_LONG).show();
            }
        });

        viewHolder.deleteCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(dataModel.getId() == null){
                    Toast.makeText(getContext(), "Can't delete now. Id: " + dataModel.getId(), Toast.LENGTH_LONG).show();
                }else {

                    // delete car from database
                    Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                    DeleteCarAsyncTask addCarAsyncTask = new DeleteCarAsyncTask(dataModel.getId());
                    addCarAsyncTask.response = CarCustomAdapter.this;
                    addCarAsyncTask.execute();

                    // delete car from view
                    Integer index = (Integer) view.getTag();
                    dataSet.remove(index.intValue());
                    notifyDataSetChanged();

                    // delete car from shared preferences
                    for (Iterator<Car> carIterator = user.getUserCars().iterator(); carIterator.hasNext(); ) {
                        Car car = carIterator.next();
                        if (car.equals(dataModel)) {
                            user.getUserCars().remove(car);
                        }
                    }

                    final Gson gson = new Gson();
                    String serializedObject = gson.toJson(user);
                    Log.d("CarCustomAdapter", serializedObject);


                    if (shp == null)
                        shp = getContext().getSharedPreferences("myPreferences", MODE_PRIVATE);
                    shpEditor = shp.edit();
                    shpEditor.putString("user", serializedObject);
                    shpEditor.commit();

                    Toast.makeText(getContext(), "Deleted car id: " + dataModel.getId(), Toast.LENGTH_LONG).show();
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public void taskResult(String result) {

    }

    // Method for remove an item of ListView inside adapter class
// you need to pass as an argument the tag you added to the layout of your choice
    public void removeView(View view) {
        // lv and the adapter must be public-static in their Activity Class
        UserDetailsActivity.carsListView.removeViewInLayout(view);
        UserDetailsActivity.adapter.notifyDataSetChanged();
    }
}