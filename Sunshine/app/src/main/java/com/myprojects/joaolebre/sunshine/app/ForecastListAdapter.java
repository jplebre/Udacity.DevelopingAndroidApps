package com.myprojects.joaolebre.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myprojects.joaolebre.sunshine.common.Utility;

public class ForecastListAdapter extends CursorAdapter {
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    public ForecastListAdapter(Context context, Cursor c, int flags) {
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
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;

        if(viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_home_today_forecast;
        } else if (viewType == VIEW_TYPE_FUTURE_DAY) {
            layoutId = R.layout.list_item_home_forecast;
        }

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ForecastListViewHolder viewHolder = new ForecastListViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ForecastListViewHolder viewHolder = (ForecastListViewHolder)view.getTag();

        // Read Icon ID (placeholder for now)
        viewHolder.iconView.setImageResource(R.mipmap.ic_launcher);

        // Read Date
        String date = Utility.getFriendlyDayString(context, cursor.getLong(HomeForecastListFragment.COL_WEATHER_DATE));
        viewHolder.dateView.setText(date);

        // Read forecast
        String forecastDescription = cursor.getString(HomeForecastListFragment.COL_WEATHER_DESC);
        viewHolder.descriptionView.setText(forecastDescription);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature
        double high = cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MAX_TEMP);
        viewHolder.highTempView.setText(Utility.formatTemperature(context, high, isMetric));

        // Low temperature
        double low = cursor.getDouble(HomeForecastListFragment.COL_WEATHER_MIN_TEMP);
        viewHolder.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));
    }
}