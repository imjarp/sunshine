package com.example.jarp.sunshine;

/**
 * Created by JARP on 01/08/2014.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jarp.sunshine.data.WeatherContract;
import com.example.jarp.sunshine.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String DATE_KEY = "forecast_date";

    public static final String LOCATION_KEY = "forecast_location";

    private static final String FORECAST_KEY = "forecast";

    private String mForecastStr;
    private String mLocation;

    private static final int DETAIL_LOADER = 0;

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment_menu, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider. You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        Bundle arguments = getArguments();
        if(arguments!=null&&arguments.containsKey(DetailActivity.DATE_KEY)){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if((arguments!=null&&arguments.containsKey(DetailActivity.DATE_KEY))&&(mLocation != null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity())))){
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null || !intent.hasExtra(DATE_KEY)) {
            return null;
        }

        String dateStr = getArguments().getString(DATE_KEY);

        String mLocation = Utility.getPreferredLocation(getActivity());

        Uri uriWeather= WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation,dateStr);

        /*String forecastDate = intent.getStringExtra(DATE_KEY);

        // Sort order: Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                Utility.getPreferredLocation(getActivity()), forecastDate);
        Log.v(LOG_TAG, weatherForLocationUri.toString());*/

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                uriWeather,
                FORECAST_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) { return; }

        // Read weather condition ID from cursor
         int weatherId = data.getInt(data.getColumnIndex(WeatherEntry.COLUMN_WEATHER_ID));
        // Use placeholder Image
        mIconView.setImageResource( Utility.getArtResourceForWeatherCondition(weatherId));

        // Read date from cursor and update views for day of week and date
        String date = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATETEXT));
        String friendlyDateText = Utility.getDayName(getActivity(), date);
        String dateText = Utility.getFormattedMonthDay(getActivity(), date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        // Read description from cursor and update view
        String description = data.getString(data.getColumnIndex(
                WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));
        mDescriptionView.setText(description);

        // Read high temperature from cursor and update view
        boolean isMetric = Utility.isMetric(getActivity());

        double high = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MAX_TEMP));
        String highString = Utility.formatTemperature(getActivity(), high, isMetric);
        mHighTempView.setText(highString);

        // Read low temperature from cursor and update view
        double low = data.getDouble(data.getColumnIndex(WeatherEntry.COLUMN_MIN_TEMP));
        String lowString = Utility.formatTemperature(getActivity(), low, isMetric);
        mLowTempView.setText(lowString);

        // Read humidity from cursor and update view
        float humidity = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_HUMIDITY));
        mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_WIND_SPEED));
        float windDirStr = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_DEGREES));
        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        float pressure = data.getFloat(data.getColumnIndex(WeatherEntry.COLUMN_PRESSURE));
        mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

        mForecastStr = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
        Log.v(LOG_TAG, "Forecast String: " + mForecastStr);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(FORECAST_KEY, mForecastStr);
        outState.putString(LOCATION_KEY, mLocation);
        super.onSaveInstanceState(outState);
    }
}