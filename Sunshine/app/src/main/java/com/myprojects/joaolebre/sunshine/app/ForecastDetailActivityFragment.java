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
import android.widget.TextView;

import com.myprojects.joaolebre.sunshine.common.Utility;
import com.myprojects.joaolebre.sunshine.data.WeatherContract.WeatherEntry;


public class ForecastDetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String CLASS_TAG = ForecastDetailActivity.class.getSimpleName();
    private ShareActionProvider mShareActionProvider;
    private String mForecastDetail;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
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

        return inflater.inflate(R.layout.fragment_forecast_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER, null, mLoaderCallbacks);

        Intent intent = getActivity().getIntent();
        mForecastDetail = intent.getStringExtra(Intent.EXTRA_TEXT);
        TextView textView = (TextView) getView().findViewById(R.id.forecast_detail_text_view);
        textView.setText(mForecastDetail);
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
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(CLASS_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        String dateString = Utility.formatDate(
                data.getLong(COL_WEATHER_DATE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(
                data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(
                data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        mForecastDetail = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        TextView detailTextView = (TextView)getView().findViewById(R.id.forecast_detail_text_view);
        detailTextView.setText(mForecastDetail);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
