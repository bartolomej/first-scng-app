package si.scng.scng;

import android.content.Context;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONHelper {

    private static RequestQueue requestQueue;
    Context context;

    public JSONHelper(Context context) {
        this.context = context;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            Cache cache = new DiskBasedCache(context.getCacheDir(), 20 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            requestQueue = new RequestQueue(cache, network);
            requestQueue.start();
        }
        return requestQueue;
    }

    public void getObjectApi(String url, String object) {
        RequestQueue requestQueue = getRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Response: ", response.getJSONObject("jedilnik").toString());
                            // ? JSONHelper.saveJsonObject(response.getJSONObject("jedilnik"), "Jedilnik");
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

    public static void saveJsonObject(JSONObject json, String filename, Context context) {
        File file = new File(  context.getFilesDir().getAbsolutePath() + "/" + filename + ".json");
        String jsonString =  json.toString();
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveJsonObject(JSONObject json, String filename) {
        File file = new File(  context.getApplicationContext().getFilesDir().getAbsolutePath() + "/" + filename + ".json");
        String jsonString =  json.toString();
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveJsonArray(JSONArray json, String filename, Context context) {
        File file = new File(  context.getFilesDir().getAbsolutePath() + "/" + filename + ".json");
        String jsonString =  json.toString();
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(jsonString);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject readJsonObjectFile(String filename) throws JSONException {
        File file = new File(this.context.getFilesDir().getAbsolutePath() + "/" + filename + ".json");
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
        return new JSONObject(text.toString());
    }

    public static JSONArray readJsonArrayFile(String filename, Context context) throws JSONException {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename + ".json");
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
        Log.d("READ URNIK: ", text.toString());
        return new JSONArray(text.toString());
    }
}
