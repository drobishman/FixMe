package it.adrian.fixme.adapter;

import static it.adrian.fixme.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import it.adrian.fixme.R;
import it.adrian.fixme.model.Car;
import it.adrian.fixme.model.TroubleCode;

public class TroubleCodesCustomAdapter extends ArrayAdapter<TroubleCode> {

    private ArrayList<TroubleCode> troubleCodes;
    Context mContext;


    public TroubleCodesCustomAdapter(ArrayList<TroubleCode> troubleCodes, @NonNull Context context) {
        super(context, R.layout.activity_car_trouble_codes, troubleCodes);
        this.troubleCodes = troubleCodes;
        this.mContext = context;
    }

    // View lookup cache
    private static class ViewHolder {
        private TextView number;
        private TextView faultLocation;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TroubleCode troubleCode = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.trouble_code_view, parent, false);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        viewHolder.faultLocation.setText(troubleCode.getFaultLocation());
        viewHolder.number.setText(troubleCode.getNumber());

        // Return the completed view to render on screen
        return convertView;
    }
}