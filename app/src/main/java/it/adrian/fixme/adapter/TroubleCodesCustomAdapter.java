package it.adrian.fixme.adapter;

import static it.adrian.fixme.R.*;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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


    public TroubleCodesCustomAdapter(ArrayList<TroubleCode> troubleCodes,Context context) {
        super(context, layout.trouble_code_view, troubleCodes);
        this.troubleCodes = troubleCodes;
        this.mContext = context;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView number;
        TextView faultLocation;
        TextView job;
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

            viewHolder.number = (TextView) convertView.findViewById(R.id.info_number);
            viewHolder.faultLocation = (TextView) convertView.findViewById(id.info_fault_location);
            viewHolder.job = (TextView) convertView.findViewById(id.info_job);

            result=convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.number.setText("Trouble code: " + troubleCode.getNumber());
        viewHolder.faultLocation.setText("Fault location: " + troubleCode.getFaultLocation());
        viewHolder.job.setText("Status: " + troubleCode.getJob());

        // Return the completed view to render on screen
        return convertView;
    }
}