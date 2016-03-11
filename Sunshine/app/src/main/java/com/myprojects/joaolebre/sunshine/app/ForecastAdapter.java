package com.myprojects.joaolebre.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myprojects.joaolebre.sunshine.common.Utility;

public class ForecastAdapter extends CursorAdapter {
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(HomeForecastListFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(HomeForecastListFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_home_forecast, parent, false);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }
}