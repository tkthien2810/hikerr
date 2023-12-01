package com.example.hiker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ObservationAdapter extends ArrayAdapter<Observation> {

    public ObservationAdapter(Context context, int resource, List<Observation> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.observation_item, parent, false);
        }

        Observation observation = getItem(position);

        if (observation != null) {
            TextView observationText = convertView.findViewById(R.id.observationText);
            TextView observationTime = convertView.findViewById(R.id.observationTime);

            observationText.setText(observation.getObservationText());
            observationTime.setText(observation.getObservationTime());
        }

        return convertView;
    }
}


