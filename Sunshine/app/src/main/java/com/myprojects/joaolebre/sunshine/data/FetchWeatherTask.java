package com.myprojects.joaolebre.sunshine.data;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.ListView;

import com.myprojects.joaolebre.sunshine.app.HomeForecastListFragment;
import com.myprojects.joaolebre.sunshine.app.R;
import com.myprojects.joaolebre.sunshine.data.common.AsyncCaller;
import com.myprojects.joaolebre.sunshine.data.common.UrlContentGetter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;

/**
 * Created by joao.lebre on 07/02/2016.
 */
public class FetchWeatherTask implements AsyncCaller {
    private static final String CLASS_TAG = UrlContentGetter.class.getSimpleName();

    private UrlContentGetter mGetForecast;
    private String postCode;
    private HomeForecastListFragment fragment;
    private String[] weeklyForecast;

    private final String PROTOCOL_URL = "http";
    private final String BASE_URL = "api.openweathermap.org";
    private final String DIRECTORY_URL = "data/2.5";
    private final String QUERY_TYPE = "forecast";
    private final String POSTCODE_QUERY_PARAM = "q";
    private final String APIKEY_QUERY_PARAM = "APPID";
    private final String FORMAT_PARAM = "mode";
    private final String UNITS_PARAM = "units";
    private final String DAYS_PARAM = "cnt";

    //These could potentially be modified by the user:
    private final String RESPONSE_FORMAT = "json";
    private final String RESPONSE_UNITS = "metric";
    private String RESPONSE_DAYS = "7";
    private int numberOfDays;
    private final String APIKEY = "0255b417a3301f51636044ad2151b9f8"; //Stored somewhere else?


    public FetchWeatherTask(String postCode, int numberOfDays, Object fragment) {
        this.postCode = postCode;
        this.numberOfDays = numberOfDays;
        this.RESPONSE_DAYS = Integer.toString(numberOfDays);
        this.fragment = (HomeForecastListFragment)fragment;

    }

    public void fetchData() {
        fetchDataByPostcode(this.postCode);
    }

    public void fetchDataByPostcode(String postCode) {
        Uri.Builder urlBuilder = createUriPath(postCode);
        getContentFromUrl(urlBuilder);
    }

    //URL and url get methods
    private Uri.Builder createUriPath(String postCode) {
        Uri.Builder urlBuilder = new Uri.Builder();

        urlBuilder.scheme(PROTOCOL_URL)
                .authority(BASE_URL)
                .appendPath(DIRECTORY_URL)
                .appendPath(QUERY_TYPE)
                .appendQueryParameter(POSTCODE_QUERY_PARAM, postCode)
                .appendQueryParameter(FORMAT_PARAM, RESPONSE_FORMAT)
                .appendQueryParameter(UNITS_PARAM, RESPONSE_UNITS)
                .appendQueryParameter(DAYS_PARAM, RESPONSE_DAYS)
                .appendQueryParameter(APIKEY_QUERY_PARAM, APIKEY)
                .build();

        return urlBuilder;
    }

    private void getContentFromUrl(Uri.Builder url) {
        mGetForecast = new UrlContentGetter(this);

        try {
            URL encodedUrl = new URL(url.toString());
            mGetForecast.execute(encodedUrl);

        } catch (IOException ioException) {
            Log.e("FetchWeatherTask", "malformed URL");
        }
    }

    // JSON parser
    private String[] getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {
        Log.v("This is JSON: ", forecastJsonStr);

        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_MAX = "temp_max";
        final String OWM_MIN = "temp_min";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        // Set current date (Julian days)
        Time dayTime = new Time();
        dayTime.setToNow();
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // Reset timer so we can work in UTC from this point onwards
        dayTime = new Time();

        String[] resultStrs = new String[numberOfDays];
        for (int i = 0; i < weatherArray.length(); i++) {
            String day;
            String description;
            String highAndLow;

            JSONObject dayForecast = weatherArray.getJSONObject(i);

            long dateTime;
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_DESCRIPTION);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }


    // Data formatting methods:
    private String getReadableDateString(long time) {
        // API Unix timestamp -> Miliseconds
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }


    private String formatHighLows(double high, double low) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(fragment.getActivity());
        String unitPreference = sharedPreferences.getString(
                fragment.getString(R.string.preference_temperature_units_key),
                fragment.getString(R.string.preference_temperature_units_default)
        );

        if (unitPreference.equals(fragment.getString(R.string.preference_temperature_units_value_imperial))) {
            high = (high * 1.8) + 32;
            low = (low * 1.8) + 32;
        } else if (!unitPreference.equals(fragment.getString(R.string.preference_temperature_units_value_metric))) {
            Log.d(CLASS_TAG, "Unit type not found: " + unitPreference);
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    // Delegate for Async UrlContentGetter
    @Override
    public void asyncProcessFinishedWithResult(Object result) {
        try {
            weeklyForecast = getWeatherDataFromJson((String) result);
            fragment.updateWeeklyForecast(weeklyForecast);
        } catch (JSONException e) {
            Log.e("FetchWeatherTask", "Failed to parse JSON");
        }
    }
}
