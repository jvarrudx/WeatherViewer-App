package com.deitel.weatherviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class WeatherArrayAdapter extends ArrayAdapter<Weather> {

    private final LayoutInflater inflater;

    public WeatherArrayAdapter(Context context, List<Weather> forecast) {
        super(context, 0, forecast);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            holder = new ViewHolder();
            holder.iconTextView = convertView.findViewById(R.id.iconTextView);
            holder.dayTextView = convertView.findViewById(R.id.dayTextView);
            holder.lowTextView = convertView.findViewById(R.id.lowTextView);
            holder.hiTextView = convertView.findViewById(R.id.hiTextView);
            holder.humidityTextView = convertView.findViewById(R.id.humidityTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Weather day = getItem(position);

        if (day != null) {

            holder.iconTextView.setText(day.icon);

            holder.dayTextView.setText(
                    day.dayOfWeek + " - " + day.description
            );

            holder.lowTextView.setText(day.minTemp);
            holder.hiTextView.setText(day.maxTemp);
            holder.humidityTextView.setText(day.humidity);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView iconTextView;
        TextView dayTextView;
        TextView lowTextView;
        TextView hiTextView;
        TextView humidityTextView;
    }
}
