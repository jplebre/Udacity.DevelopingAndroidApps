package com.myprojects.joaolebre.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.myprojects.joaolebre.sunshine.data.FetchWeatherTask;
import com.myprojects.joaolebre.sunshine.data.common.AsyncCaller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class HomeForecastListFragment extends Fragment implements AsyncCaller {

    private String[] mWeeklyforecast;
    private ArrayAdapter<String> mForecastAdapter;
    private ListView mForecastListVieworecastListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_home_forecast, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home_forecast, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupListViewAndAdapter();
        getWeatherData();
    }

    private void setupListViewAndAdapter() {
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_home_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>());

        mForecastListVieworecastListView = (ListView) getView().findViewById(R.id.list_view_forecast);

        mForecastListVieworecastListView.setAdapter(mForecastAdapter);

        mForecastListVieworecastListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doOnItemClick(parent, view, position, id);
            }
        });
    }

    private void doOnItemClick(AdapterView<?> parent, View view, int position, long id) {
        String message = parent.getItemAtPosition(position).toString();
        Intent intent = new Intent(getActivity(), ForecastDetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                getWeatherData();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Use FetchWeatherTask to populate the array
    private void getWeatherData() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String locationPreferences = sharedPreferences.getString(
                getString(R.string.preference_location_key),
                getString(R.string.preference_location_default));
        FetchWeatherTask weatherStation = new FetchWeatherTask(locationPreferences,6,this);
        weatherStation.fetchData();
    }

    @Override
    public void asyncProcessFinishedWithResult(Object result) {
        mWeeklyforecast = (String[]) result;
        if (mWeeklyforecast != null) updateArrayAdapterAndListView();
    }

    // Refresh ArrayAdapter
    private void updateArrayAdapterAndListView(){
        List<String> weekForecastList = new ArrayList<String>(Arrays.asList(mWeeklyforecast));
        mForecastAdapter.clear();
        mForecastAdapter.addAll(mWeeklyforecast);


        mForecastAdapter.notifyDataSetChanged();
    }
}
