package com.myprojects.joaolebre.sunshine.data;

import android.net.Uri;
import android.util.Log;

import com.myprojects.joaolebre.sunshine.data.common.UrlContentGetter;

import java.io.IOException;
import java.net.URL;

/**
 * Created by joao.lebre on 07/02/2016.
 */
public class FetchWeatherTask {
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
    private final String RESPONSE_DAYS = "7";
    private final String APIKEY = "0255b417a3301f51636044ad2151b9f8"; //Stored somewhere else?


    public FetchWeatherTask(){
    }

    public String fetchDataByPostcode(String postCode, String country){
        Uri.Builder urlBuilder = new Uri.Builder();
        UrlContentGetter getForecast = new UrlContentGetter();

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

        try {
            URL encodedUrl = new URL(urlBuilder.toString());
            getForecast.execute(encodedUrl);

        } catch (IOException ioException) {
            Log.e("FetchWeatherTask", "malformed URL");
        }

        return "";
    }
}
