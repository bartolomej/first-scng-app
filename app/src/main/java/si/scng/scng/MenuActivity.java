package si.scng.scng;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MenuActivity extends AppCompatActivity {

    String URL_GET_JEDILNIK = "http://tscng-app.ddns.net/api/api.php";
    String URL_POST = "http://tscng-app.ddns.net/api/rating/rate.php";
    String URL_GET_1 = "http://tscng-app.ddns.net/api/rating/all.php?api_key=j7db4l30h7";
    String URL_GET_STATS = "http://tscng-app.ddns.net/api/rating/stats.php?api_key=j7db4l30h7";
    String API_KEY = "j7db4l30h7";
    String id;
    int rating;

    TextView text_pon;
    TextView text_tor;
    TextView text_sre;
    TextView text_cet;
    TextView text_pet;

    TextView ocena1;
    TextView ocena2;
    TextView ocena3;
    TextView ocena4;
    TextView ocena5;

    RatingBar rate1;
    RatingBar rate2;
    RatingBar rate3;
    RatingBar rate4;
    RatingBar rate5;

    String pon;
    String tor;
    String sre;
    String cet;
    String pet;

    Calendar calendar;
    SimpleDateFormat date;
    Date nowDate;
    String stringDay;
    int intDay;
    String formatedDate; //date format (dd-MM-yyy)

    JSONArray daily_stats;
    //String top_rated = null;

    public String param;
    public String meal;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu1);

        Intent myIntent = getIntent(); // gets the previously created intent
        meal = myIntent.getStringExtra("meal");
        param = myIntent.getStringExtra("activity");
        String title = myIntent.getStringExtra("title");

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        /**
         * MUST EXECUTE THIS METHODS !
         */
        startupVariableInit();
        startupProccess();
    }

    /**
     * MUST!! startup proccesing
     */
    public void startupProccess() {
        /**
         * if network not available load data from .json file
         */
        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy");
        formatedDate = date.format(calendar.getTime());
        nowDate = new Date();
        calendar.setTime(nowDate);
        intDay = calendar.get(Calendar.DAY_OF_WEEK);
        stringDay = Integer.toString(intDay);

        loadJsonFile(MenuActivity.this);
        if (isNetworkAvailable()) {
            requestJedilnikStats();
            setRatingBars();
        } else {
            hideAllRatingBars();
            Toast.makeText(this, "Omrežje ni na voljo !", Toast.LENGTH_LONG);
        }
        addListenerOnRatingBar();
    }

    /**
     * MUST!! startup variable initialization
     */
    public void startupVariableInit() {
        // Rating Bars
        rate1 = findViewById(R.id.rating_bar_1);
        rate2 = findViewById(R.id.rating_bar_2);
        rate3 = findViewById(R.id.rating_bar_3);
        rate4 = findViewById(R.id.rating_bar_4);
        rate5 = findViewById(R.id.rating_bar_5);
        // Text Views
        text_pon = findViewById(R.id.text_pon);
        text_tor = findViewById(R.id.text_tor);
        text_sre = findViewById(R.id.text_sre);
        text_cet = findViewById(R.id.text_cet);
        text_pet = findViewById(R.id.text_pet);
        // Rating Text Views
        ocena1 = findViewById(R.id.ocena1);
        ocena2 = findViewById(R.id.ocena2);
        ocena3 = findViewById(R.id.ocena3);
        ocena4 = findViewById(R.id.ocena4);
        ocena5 = findViewById(R.id.ocena5);
        // Date Variables

    }

    /**
     * @return
     * Checks for network availability
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Rating bars event listeners
     */
    public void addListenerOnRatingBar() {

        rate1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
                int stars = (int) rating;
                Toast.makeText(MenuActivity.this, "Tvoja ocena je: " + stars, Toast.LENGTH_SHORT).show();
                setRating(stars);
            }
        });

        rate2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
                int stars = (int) rating;
                Toast.makeText(MenuActivity.this, "Tvoja ocena je: " + stars, Toast.LENGTH_SHORT).show();
                setRating(stars);
            }
        });

        rate3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
                int stars = (int) rating;
                Toast.makeText(MenuActivity.this, "Tvoja ocena je: " + stars, Toast.LENGTH_SHORT).show();
                setRating(stars);
            }
        });

        rate4.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
                int stars = (int) rating;
                Toast.makeText(MenuActivity.this, "Tvoja ocena je: " + stars, Toast.LENGTH_SHORT).show();
                setRating(stars);
            }
        });

        rate5.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating,	boolean fromUser) {
                int stars = (int) rating;
                Toast.makeText(MenuActivity.this, "Tvoja ocena je: " + stars, Toast.LENGTH_SHORT).show();
                setRating(stars);
            }
        });
    }

    /**
     * GET request method for retrieving meal data
     */
    /*public void requestJedilnik() {
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest1 = new JsonArrayRequest(
                URL_GET_JEDILNIK,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("testjson:", response.toString());
                        saveToJsonFile(response);
                        new LongOperation(MenuActivity.this).execute(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR", error.toString());
                        Toast.makeText(getApplicationContext(), "Ni internetne povezave!", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue1.add(jsonArrayRequest1);
    }*/

    /**
     * GET request method for retrieving meal rates
     */
    public void requestJedilnikStats() {
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL_GET_STATS, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            daily_stats = response.getJSONArray("average_daily_grade");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //String top_rated = null;
                        /*try {
                            top_rated = response.getString("top_rated_food_grade");
                            setStats();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/
                        setStats();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MenuActivity.this, "Napaka pri povezavi s strežnikom !", Toast.LENGTH_LONG);
                        Log.d("ON ERROR: ", "ERRORED REQUEST");
                    }
                });
        requestQueue1.add(jsonObjectRequest);
    }

    /**
     * @param json
     * saves JSONArray to .json file
     */
    public void saveToJsonFile(JSONArray json) {
        File file = new File(  getApplicationContext().getFilesDir().getAbsolutePath() + "/" + "Menu1_Data" + ".json");
        String jsonString =  json.toString();
        try {
            FileWriter  fw = new FileWriter(file);
            fw.write(jsonString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * loads json from .json file
     */
    public void loadJsonFile(Context context) {
        File file = new File(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + "Jedilnik" + ".json");
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            // do exception handling
        } finally {
            try { br.close(); } catch (Exception e) { }
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(text.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Json error: ", "error converting to json array");
        }
        Log.d("Loading json: ", jsonObject.toString());
        new LongOperation(MenuActivity.this).execute(jsonObject);
    }

    /**
     * method for setting meal rates on views
     */
    public void setStats() {
        //String string_daily_stats = daily_stats.toString();
        switch (this.intDay) {
            case 2:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                break;
            case 3:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                break;
            case 4:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                try {
                    sre = daily_stats.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena3.setText("Ocena: "+sre);
                break;
            case 5:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                try {
                    sre = daily_stats.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena3.setText("Ocena: "+sre);
                try {
                    cet = daily_stats.getString(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena4.setText("Ocena: "+cet);
                break;
            case 6:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                try {
                    sre = daily_stats.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena3.setText("Ocena: "+sre);
                try {
                    cet = daily_stats.getString(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena4.setText("Ocena: "+cet);
                try {
                    pet = daily_stats.getString(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena5.setText("Ocena: "+pet);
                break;
            case 7:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: "+pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                try {
                    sre = daily_stats.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena3.setText("Ocena: "+sre);
                try {
                    cet = daily_stats.getString(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena4.setText("Ocena: "+cet);
                try {
                    pet = daily_stats.getString(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena5.setText("Ocena: "+pet);
                break;
            case 8:
                try {
                    pon = daily_stats.getString(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena1.setText("Ocena: " + pon);
                try {
                    tor = daily_stats.getString(1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena2.setText("Ocena: "+tor);
                try {
                    sre = daily_stats.getString(2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena3.setText("Ocena: "+sre);
                try {
                    cet = daily_stats.getString(3);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena4.setText("Ocena: "+cet);
                try {
                    pet = daily_stats.getString(4);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ocena5.setText("Ocena: "+pet);
                break;
        }

    }

    /**
     * @param list
     * method for setting meal data on views
     */
    public void setJedilnik(ArrayList<String> list) {
        text_pon.setText(list.get(0));
        text_tor.setText(list.get(1));
        text_sre.setText(list.get(2));
        text_cet.setText(list.get(3));
        text_pet.setText(list.get(4));
    }

    /**
     * method for hidding all rating bars except one for today
     */
    public void setRatingBars() {
        switch (this.intDay) {
            case 0:
                goneVisibility(rate1, rate2, rate3, rate4, rate5);
                break;
            case 1:
                goneVisibility(rate1, rate2, rate3, rate4, rate5);
                break;
            case 2:
                goneVisibility(rate2, rate3, rate4, rate5, null);
                break;
            case 3:
                goneVisibility(rate1, rate3, rate4, rate5, null);
                break;
            case 4:
                goneVisibility(rate1, rate2, rate4, rate5, null);
                break;
            case 5:
                goneVisibility(rate1,rate2, rate3, rate5, null);
                break;
            case 6:
                goneVisibility(rate1, rate2, rate3, rate4, null);
                break;
            case 7:
                goneVisibility(rate1, rate2, rate3, rate5, null);
                break;
            case 8:
                goneVisibility(rate1, rate2, rate3, rate4, null);
                break;
        }
    }

    /**
     * @param rate1
     * @param rate2
     * @param rate3
     * @param rate4
     * method hides rating bars
     */
    public void goneVisibility(RatingBar rate1,
                               RatingBar rate2,
                               RatingBar rate3,
                               RatingBar rate4,
                               RatingBar rate5) {
        rate1.setVisibility(View.GONE);
        rate2.setVisibility(View.GONE);
        rate3.setVisibility(View.GONE);
        rate4.setVisibility(View.GONE);
        if (rate5 != null) {
            rate5.setVisibility(View.GONE);
        }
    };

    public void hideAllRatingBars() {
        Log.d("Rating bar setup: ", "GONE");
        rate1.setVisibility(View.GONE);
        rate2.setVisibility(View.GONE);
        rate3.setVisibility(View.GONE);
        rate4.setVisibility(View.GONE);
        rate5.setVisibility(View.GONE);
    }

    /**
     * @param rate
     * sets global rating variable
     */
    public void setRating(int rate) {
        this.rating = rate;
        postData();
    }

    /**
     * POST method for posting rating data
     * --EDIT: setup for multiple menus !!
     */
    public void postData() {
        id = Settings.Secure.getString(MenuActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String s = Integer.toString(rating);
        Uri.Builder builder = Uri.parse(URL_POST).buildUpon();
            builder.appendQueryParameter("device_id", id);
            builder.appendQueryParameter("date", this.formatedDate);
            builder.appendQueryParameter("grade", s);
            builder.appendQueryParameter("api_key", API_KEY);
        String url = builder.build().toString();
        Log.d("POST URL: ", url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
           new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(MenuActivity.this, response, Toast.LENGTH_SHORT);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuActivity.this, error+"", Toast.LENGTH_SHORT);
            }
        });
        requestQueue.add(stringRequest);
        requestJedilnikStats();
    }
}

/**
 * AsyncTask for background proccessing meal data
 * --set the right params for reading json (activity.param)
 */
class LongOperation extends AsyncTask<JSONObject, String, ArrayList<String>> {

    MenuActivity activity;

    public LongOperation(MenuActivity activity) {
        this.activity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(JSONObject... response) {
        ArrayList<JsonArray> values = new ArrayList<JsonArray>();
        String concat = "";
        ArrayList<String> valuesForView = new ArrayList<String>();
        JSONObject jsonObject = null;
        try {
            jsonObject = response[0].getJSONObject(activity.meal);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JsonParser jsonParser = new JsonParser();
            values.add(jsonParser.parse(jsonObject.getJSONObject("mon").getString(activity.param)).getAsJsonArray());
            values.add(jsonParser.parse(jsonObject.getJSONObject("tue").getString(activity.param)).getAsJsonArray());
            values.add(jsonParser.parse(jsonObject.getJSONObject("wed").getString(activity.param)).getAsJsonArray());
            values.add(jsonParser.parse(jsonObject.getJSONObject("thu").getString(activity.param)).getAsJsonArray());
            values.add(jsonParser.parse(jsonObject.getJSONObject("fri").getString(activity.param)).getAsJsonArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("VALUES: ", values.toString());
        for (int i = 0; i < values.size(); i++) {
            concat = "";
            for (int j = 0; j < values.get(i).size(); j++) {
                concat = concat + values.get(i).get(j).getAsString().replace(",", "");
                if (j < values.get(i).size() - 1) {
                    concat = concat + "\n";
                }
            }
            valuesForView.add(i, concat);
        }
        Log.d("CONCAT: ", valuesForView.toString());
        return valuesForView;
    }

    @Override
    protected void onPostExecute(ArrayList<String> s) {
        activity.setJedilnik(s);
        Log.d("onPostExe:", s.toString());
    }
}


