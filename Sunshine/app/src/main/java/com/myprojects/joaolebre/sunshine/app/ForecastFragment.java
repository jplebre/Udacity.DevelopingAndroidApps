package com.myprojects.joaolebre.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.myprojects.joaolebre.sunshine.data.UrlContentGetter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    //This method is better to put things like getView()
    //onCreateView() might run before other dependencies have been created
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] weekForecastArray = new String[]{
                "Today - Sunny - 88/30",
                "Tomorrow - Foggy - 70/46",
                "Weds - Cloudy - 72/63",
                "Thurs - Rainy - 64/51",
                "Fri - Foggy - 70/46",
                "Sat - Sunny - 76/68"
        };

        List<String> weekForecastList = new ArrayList<String>(Arrays.asList(weekForecastArray));

        ArrayAdapter<String> forecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecastList);

        ListView listView = (ListView) getView().findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        getForecastContent("http://api.openweathermap.org/data/2.5/forecast?id=2643743&APPID=0255b417a3301f51636044ad2151b9f8");

    }

    public String getForecastContent(String url) {
        String response = "";
        try {
            URL encodedUrl = new URL(url);
            //&APPID=0255b417a3301f51636044ad2151b9f8
            UrlContentGetter getForecast = new UrlContentGetter();
            getForecast.execute(encodedUrl);

        } catch (IOException ioException) {
            Log.e("Placeholder Fragment", "malformed URL");
        }

        return response;
    }
}
