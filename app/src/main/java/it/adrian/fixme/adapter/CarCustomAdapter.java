package it.adrian.fixme.adapter;


import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.model.Car;

public class CarCustomAdapter extends ArrayAdapter<Car> implements View.OnClickListener{

    private ArrayList<Car> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView registrationNumber;
        TextView chasisNumber;
        TextView brand;
        TextView model;
    }

    public CarCustomAdapter(ArrayList<Car> data, Context context) {
        super(context, R.layout.car_view, data);
        this.dataSet = data;
        this.mContext=context;

    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Car dataModel=(Car)object;

        switch (v.getId())
        {
            case R.id.registrationNumber:
                Snackbar.make(v, "Car plate number " +dataModel.getRegistrationNumber(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Car dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.car_view, parent, false);
            viewHolder.registrationNumber = (TextView) convertView.findViewById(R.id.registrationNumber);
            viewHolder.chasisNumber = (TextView) convertView.findViewById(R.id.chasisNumber);
            viewHolder.brand = (TextView) convertView.findViewById(R.id.brand);
            viewHolder.model = (TextView) convertView.findViewById(R.id.model);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.chasisNumber.setText(dataModel.getChasisNumber());
        viewHolder.registrationNumber.setText(dataModel.getRegistrationNumber());
        viewHolder.brand.setText(dataModel.getBrand());
        viewHolder.model.setText(dataModel.getModel());
        // Return the completed view to render on screen
        return convertView;
    }
}