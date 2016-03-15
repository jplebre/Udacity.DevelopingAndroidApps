package com.myprojects.joaolebre.sunshine.app;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ForecastDetailViewHolder {
    public final ImageView iconView;
    public final TextView dateView;
    public final TextView dayView;
    public final TextView descriptionView;
    public final TextView highTempView;
    public final TextView lowTempView;
    public final TextView humidityView;
    public final TextView pressureView;
    public final TextView windView;

    public ForecastDetailViewHolder(View view) {
        iconView = (ImageView) view.findViewById(R.id.forecast_detail_fragment_item_icon);
        dayView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_day_textview);
        dateView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_date_textview);
        descriptionView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_forecast_textview);
        highTempView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_high_textview);
        lowTempView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_low_textview);
        humidityView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_humidity_textview);
        pressureView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_pressure_textview);
        windView = (TextView) view.findViewById(R.id.forecast_detail_fragment_item_wind_textview);
    }
}