package com.example.jarp.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends ActionBarActivity implements ForecastFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if(findViewById(R.id.weather_detail_container)!=null)
        {

            mTwoPane=true;

            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.weather_detail_container,new DetailFragment())
                                       .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings:
                Intent settings = new Intent(this, SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.action_map:
                OpenPreferredLocationInMap();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }

    public void OpenPreferredLocationInMap()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String location = pref.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));

        //Uri uri = Uri.Builder().

        Intent intentViewMap = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0:0?").buildUpon().appendQueryParameter("q",location).build();
        intentViewMap.setData(uri);
        startActivity(intentViewMap);


    }


    @Override
    public void onItemSelected(String date) {
        if(mTwoPane)
        {

            Bundle args = new Bundle();
            args.putString(DetailActivity.DATE_KEY, date);
            DetailFragment detailFragment = new DetailFragment();
            detailFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.weather_detail_container,detailFragment)
                                       .commit();


        }
        else
        {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.DATE_KEY, date);
            startActivity(intent);
        }
    }
}
