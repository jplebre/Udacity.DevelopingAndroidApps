package com.myprojects.joaolebre.sunshine.app;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.myprojects.joaolebre.sunshine.common.Utility;
import com.myprojects.joaolebre.sunshine.data.WeatherContract;
import com.myprojects.joaolebre.sunshine.data.WeatherContract.WeatherEntry;


public class ForecastDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CLASS_TAG = ForecastDetailActivity.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private String mForecastDetail;

    private static final int DETAIL_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // Note that we are doing a join here:
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private LoaderManager mLoaderManager;
    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallbacks;

    public ForecastDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoaderManager = getLoaderManager();
        mLoaderCallbacks = this;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_forecast_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new ShareActionProvider(getActivity());

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
            MenuItemCompat.setActionProvider(shareItem,mShareActionProvider);
        } else {
            Log.d(CLASS_TAG, "Share Action Provider is Null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_forecast_detail, container, false);

        ForecastDetailViewHolder viewHolder = new ForecastDetailViewHolder(rootView);
        rootView.setTag(viewHolder);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, mLoaderCallbacks);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_show_on_map:
                createShowOnMapIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                mForecastDetail + " " + getActivity().getString(R.string.social_share_hashtag));
        return intent;
    }

    private void createShowOnMapIntent() {
        String postCode = Utility.getPreferredLocation(getActivity());

        Uri geoLocation= Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", postCode)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(CLASS_TAG, "Couldn't call " + postCode + ", no receiving apps installed!");
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(CLASS_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || intent.getData() == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                DETAIL_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ForecastDetailViewHolder viewHolder = (ForecastDetailViewHolder)getView().getTag();

        if (data != null && data.moveToFirst()) {
            int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
            viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date
            long date = data.getLong(COL_WEATHER_DATE);
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            viewHolder.dayView.setText(friendlyDateText);
            viewHolder.dateView.setText(dateText);

            // Read description
            String description = data.getString(COL_WEATHER_DESC);
            viewHolder.descriptionView.setText(description);

            // Read unit preference
            boolean isMetric = Utility.isMetric(getActivity());

            // Read high temperature
            double high = data.getDouble(COL_WEATHER_MAX_TEMP);
            String highString = Utility.formatTemperature(getActivity(), high, isMetric);
            viewHolder.highTempView.setText(highString);

            // Read low temperature
            double low = data.getDouble(COL_WEATHER_MIN_TEMP);
            String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
            viewHolder.lowTempView.setText(lowString);

            // Read humidity
            float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
            viewHolder.humidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and direction
            float windSpeedStr = data.getFloat(COL_WEATHER_WIND_SPEED);
            float windDirStr = data.getFloat(COL_WEATHER_DEGREES);
            viewHolder.windView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            // Read pressure
            float pressure = data.getFloat(COL_WEATHER_PRESSURE);
            viewHolder.pressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // Setup info for share intent
            mForecastDetail = String.format("%s - %s - %s/%s", dateText, description, high, low);

            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
