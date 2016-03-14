package com.myprojects.joaolebre.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myprojects.joaolebre.sunshine.common.Utility;

public class ForecastAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_home_today_forecast;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_home_forecast;
        }
        return LayoutInflater.from(context).inflate(layoutId, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Read Icon ID (placeholder for now
        int weatherId = cursor.getInt(HomeForecastListFragment.COL_WEATHER_ID);
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.mipmap.ic_launcher);

        // Read Date from cursor
        String date = Utility.getFriendlyDayString(context, cursor.getLong(HomeForecastListFragment.COL_WEATHER_DATE));
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        dateView.setText(date);

        // Read forecast from cursor
        String forecast = cursor.getString(HomeForecastListFragment.COL_WEATHER_DESC);
        TextView forecastView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        forecastView.setText(forecast);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MAX_TEMP);

        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        highView.setText(Utility.formatTemperature(high, isMetric));

        // Low temperature from cursor
        double low = cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MIN_TEMP);

        TextView minView = (TextView) view.findViewById(R.id.list_item_low_textview);
        minView.setText(Utility.formatTemperature(low, isMetric));
    }
}