package com.myprojects.joaolebre.sunshine.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.myprojects.joaolebre.sunshine.common.Utility;

public class HomeForecastActivity extends AppCompatActivity {

    private static final String CLASS_TAG = HomeForecastActivity.class.getSimpleName();
    private String mLocation;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation( this );

        setContentView(R.layout.activity_home_forecast);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new ForecastDetailActivityFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation( this );

        if (location != null && !location.equals(mLocation)) {
            HomeForecastListFragment ff = (HomeForecastListFragment) getFragmentManager().findFragmentById(R.id.fragment_forecast);

            if ( null != ff ) {
                ff.onLocationChanged();
            }

            mLocation = location;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_home_forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            case R.id.action_show_on_map:
                createShowOnMapIntent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createShowOnMapIntent() {
        String postCode = Utility.getPreferredLocation(this);

        Uri geoLocation= Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", postCode)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(CLASS_TAG, "Couldn't call " + postCode + ", no receiving apps installed!");
        }

    }

    private void startSettingsActivity() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
}
