package com.example.jarp.sunshine;

/**
 * Created by JARP on 17/07/2014.
 */

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jarp.sunshine.data.WeatherContract;
import com.example.jarp.sunshine.service.SunshineService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public  class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private String mLocation;
    private final String SELECTED_KEY ="LIST_POSITION";
    private int mPosition = ListView.INVALID_POSITION;
    ListView mListForeCast;
    private boolean mUseTodayLayout;

    public interface Callback{
        public void onItemSelected(String date);
    }


    private final static int FORECAST_LOADER=0;
    // For the forecast view we're showing only a small subset of the stored data.
// Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
// In this case the id needs to be fully qualified with a table name, since
// the content provider joins the location & weather tables in the background
// (both have an _id column)
// On the one hand, that's annoying. On the other, you can search the weather table
// using the location set by the user, which is only in the Location table.
// So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS. If FORECAST_COLUMNS changes, these
// must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;

    private String name;
    //public ArrayAdapter<String> mForecastAdapter;

    public ForecastAdapter mForecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        mForecastAdapter  = new ForecastAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);

        String [] forecastDay = new String[]{"Today-Sunny-88/63","Tomorrow-Foggy-88/63","Today-Sunny-88/63","Tomorrow-Foggy-88/63","Today-Sunny-88/63","Tomorrow-Foggy-88/63","Today-Sunny-88/63","Tomorrow-Foggy-88/63"};

        List<String> weekForeCast = new ArrayList<String>(Arrays.asList(forecastDay));

        //new ArrayAdapter<String>(getActivity(),R.layout.list_item_forecast,R.id.list_item_forecast_textview,weekForeCast);
        // The SimpleCursorAdapter will take data from the database through the
        // Loader and use it to populate the ListView it's attached to.



        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);

        mListForeCast = (ListView) rootView.findViewById(R.id.listview_forecast);

        mListForeCast.setAdapter(mForecastAdapter);

        mListForeCast.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = mForecastAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getString(COL_WEATHER_DATE));

                    /*Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.DATE_KEY, cursor.getString(COL_WEATHER_DATE));
                    startActivity(intent);*/
                }
                mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things. It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet. Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
    }

    @Override
    public void onStart() {
        super.onStart();
        //updateWeather();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.forecastfragment, menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_refresh:
                Toast.makeText(getActivity(),"RefreshAction",Toast.LENGTH_LONG).show();


                updateWeather();
            default:
        }


        return super.onOptionsItemSelected(item);
    }

    private void updateWeather() {

        //String location = Utility.getPreferredLocation(getActivity());
        //new FetchWeatherTask(getActivity()).execute(location);



        Intent alarmIntent = new Intent(getActivity(),SunshineService.AlarmReceiver.class);
        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA,mLocation);

        PendingIntent pi = PendingIntent.getBroadcast(getActivity(),0,alarmIntent,
                                                        PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5000,pi);

        /*Intent sunshineService = new Intent(getActivity(),SunshineService.class);
        sunshineService.putExtra(SunshineService.LOCATION_QUERY_EXTRA,Utility.getPreferredLocation(getActivity()));
        getActivity().startService(sunshineService);*/
    }



    private ArrayList<String> tags;

    /* The date/time conversion code is going to be moved outside the asynctask later,
* so for convenience we're breaking it out into its own method now.
*/
    private String getReadableDateString(long time){
// Because the API returns a unix timestamp (measured in seconds),
// it must be converted to milliseconds in order to be converted to valid date.
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("E, MMM d");
        return format.format(date).toString();
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String unitType = pref.getString(getString(R.string.pref_temperature_key), getString(R.string.pref_temperature_default));
// For presentation, assume the user doesn't care about tenths of a degree.

        String imperial = getString(R.string.Imperial);
        if (unitType.equals(imperial))
        {
            high= ( high * 1.8 ) + 32;
            low = ( low  * 1.8 ) + 32;
        }

        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);



        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     *
     * Fortunately parsing is easy: constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
            throws JSONException {

// These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DATETIME = "dt";
        final String OWM_DESCRIPTION = "main";

        JSONObject forecastJson = new JSONObject(forecastJsonStr);
        JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

        String[] resultStrs = new String[numDays];
        for(int i = 0; i < weatherArray.length(); i++) {
// For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

// Get the JSON object representing the day
            JSONObject dayForecast = weatherArray.getJSONObject(i);

// The date/time is returned as a long. We need to convert that
// into something human-readable, since most people won't read "1400356800" as
// "this saturday".
            long dateTime = dayForecast.getLong(OWM_DATETIME);
            day = getReadableDateString(dateTime);

// description is in a child array called "weather", which is 1 element long.
            JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
            description = weatherObject.getString(OWM_DESCRIPTION);

// Temperatures are in a child object called "temp". Try not to name variables
// "temp" when working with temperature. It confuses everybody.
            JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
            double high = temperatureObject.getDouble(OWM_MAX);
            double low = temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        return resultStrs;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

// Sort order: Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
                return new CursorLoader(
                        getActivity(),
                        weatherForLocationUri,
                        FORECAST_COLUMNS,
                        null,
                        null,
                        sortOrder
                );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListForeCast.smoothScrollToPosition(mPosition);
        }
        mForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
        if (mForecastAdapter != null) {
            mForecastAdapter.setUseTodayLayout(mUseTodayLayout);
        }
    }

}