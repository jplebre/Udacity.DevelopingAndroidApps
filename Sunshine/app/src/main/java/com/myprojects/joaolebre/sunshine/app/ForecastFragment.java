package com.myprojects.joaolebre.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.myprojects.joaolebre.sunshine.data.FetchWeatherTask;
import com.myprojects.joaolebre.sunshine.data.common.AsyncCaller;
import com.myprojects.joaolebre.sunshine.data.common.UrlContentGetter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements AsyncCaller {

    private String[] weeklyForecast;
    private ArrayAdapter<String> forecastAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getWeatherData();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_refresh)
        {
            getWeatherData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Use FetchWeatherTask to populate the array
    private void getWeatherData() {
        FetchWeatherTask weatherStation = new FetchWeatherTask("N113FQ",6,this);
        weatherStation.fetchData();
    }

    @Override
    public void asyncProcessFinishedWithResult(Object result) {
        weeklyForecast = (String[]) result;
        if (weeklyForecast != null) updateArrayAdapterAndListView();
    }

    // Refresh ArrayAdapter
    private void updateArrayAdapterAndListView(){
        List<String> weekForecastList = new ArrayList<String>(Arrays.asList(weeklyForecast));

        forecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_textview, weekForecastList);
        ListView listView = (ListView) getView().findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        forecastAdapter.notifyDataSetChanged();
    }
}
