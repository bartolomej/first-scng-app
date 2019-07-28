package si.scng.scng;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String URL_GET_JEDILNIK = "http://tscng-app.ddns.net/api/api.php";
    String URL_GET_NOVICE = "http://tscng-app.ddns.net/api/novice.php";
    String URL_GET_URNIK = "http://tscng-app.ddns.net/api/urnik.php";

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    String classVar;

    ViewPager mViewPager;
    TabLayout tabLayout;

    JSONHelper jsonHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        classVar = sharedPreferences.getString("default_class", "none");

        jsonHelper = new JSONHelper(this.getApplicationContext());

        setupActivity();
        apiReqests();
        // check if update needed -> then startBcgProcess()
    }

    /**
     * does all api requests
     */
    public void apiReqests() {
        if (isNetworkAvailable()) {
            // if data update needed:
            // request jedilnik, urnik, novice data
            // save new data to .json
            requestJedilnik();
            requestNews();
            requestUrnik();
        } else {
            // load data from local file
        }
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(this.getCacheDir(), 20 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void setupActivity() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mSectionsPagerAdapter.addFragment(new HomeFragment());
        mSectionsPagerAdapter.addFragment(new UrnikFragment());
        mSectionsPagerAdapter.addFragment(new JedilnikFragment());

        final TabLayout.Tab home = tabLayout.newTab();
        final TabLayout.Tab urnik = tabLayout.newTab();
        final TabLayout.Tab jedilnik = tabLayout.newTab();


        home.setIcon(R.drawable.icon_home_orange2);
        urnik.setIcon(R.drawable.icon_schedule2);
        jedilnik.setIcon(R.drawable.icon_food);

        tabLayout.addTab(home, 0);
        tabLayout.addTab(urnik, 1);
        tabLayout.addTab(jedilnik, 2);

        tabLayout.setOnTabSelectedListener(onTabSelectedListener(mViewPager));

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: {
                        home.setIcon(R.drawable.icon_home_orange2);
                        urnik.setIcon(R.drawable.icon_schedule2);
                        jedilnik.setIcon(R.drawable.icon_food);
                        break;
                    }
                    case 1: {
                        home.setIcon(R.drawable.icon_home);
                        urnik.setIcon(R.drawable.icon_schedule_orange2);
                        jedilnik.setIcon(R.drawable.icon_food);
                        break;
                    }
                    case 2: {
                        home.setIcon(R.drawable.icon_home);
                        urnik.setIcon(R.drawable.icon_schedule2);
                        jedilnik.setIcon(R.drawable.icon_food_orange2);
                        break;
                    }
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private TabLayout.OnTabSelectedListener onTabSelectedListener(final ViewPager pager) {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
            return true;
        }
        if (id == R.id.action_feedback) {
            Intent feedback = new Intent(this, FeedbackActivity.class);
            startActivity(feedback);
            return true;
        }
        if (id == R.id.action_updates) {
            Intent updates = new Intent(this, UpdatesActivity.class);
            startActivity(updates);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * use JSONHelper class method in the future
     */
    public void requestNews() {
        RequestQueue requestQueue = getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(
                URL_GET_NOVICE,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONHelper.saveJsonArray(response.getJSONArray("novice"), "Novice", getApplicationContext());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response_error: ", error.toString());
            }
        });
        requestQueue.add(request);
    }

    /**
     * use JSONHelper class method in the future
     */
    public void requestUrnik() {
        String URL = URL_GET_URNIK + "?" + "razred=" + classVar;
        RequestQueue requestQueue = getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONHelper.saveJsonArray(response.getJSONArray("data"), "Urnik", getApplicationContext());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response_error: ", error.toString());
            }
        });
        requestQueue.add(request);
    }

    /**
     * use JSONHelper class method in the future
     */
    public void requestJedilnik() {
        RequestQueue requestQueue = getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(
                URL_GET_JEDILNIK,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            saveJsonObject(response.getJSONObject("jedilnik"), "Jedilnik");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response_error: ", error.toString());
            }
        });
        requestQueue.add(request);
    }

    public void saveJsonObject(JSONObject json, String filename) {
        File file = new File(  getApplicationContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + filename + ".json");
        String jsonString =  json.toString();
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
