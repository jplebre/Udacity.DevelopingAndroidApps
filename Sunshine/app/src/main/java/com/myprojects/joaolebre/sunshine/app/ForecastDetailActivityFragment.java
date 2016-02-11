package com.myprojects.joaolebre.sunshine.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
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


public class ForecastDetailActivityFragment extends Fragment {

    private static final String CLASS_TAG = ForecastDetailActivity.class.toString();
    private ShareActionProvider mShareActionProvider;
    private String mForecastDetail;

    public ForecastDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_forecast_detail, menu);

        // Locate MenuItem with ShareActionProvider
        MenuItem shareItem = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = new ShareActionProvider(getContext());

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
//            case R.id.action_share:
//                Toast.makeText(getContext(),"hey I'm sharing", Toast.LENGTH_SHORT);
//                createShareIntent();
//                return true;
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

//    private void setShareIntent(Intent shareIntent) {
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(shareIntent);
//        }
//    }

    private void createShowOnMapIntent() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String postCode = sharedPreferences.getString(
                getString(R.string.preference_location_key),
                getString(R.string.preference_location_default));

        Uri geoLocation= Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", postCode)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }

    }
}
