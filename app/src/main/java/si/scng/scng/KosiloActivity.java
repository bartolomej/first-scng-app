package si.scng.scng;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KosiloActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kosilo);

        getSupportActionBar().setTitle("KOSILO");

        String URL = "http://83.212.82.188/api.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final TextView text_pon = findViewById(R.id.text_pon2);
        final TextView text_tor = findViewById(R.id.text_tor2);
        final TextView text_sre = findViewById(R.id.text_sre2);
        final TextView text_cet = findViewById(R.id.text_cet2);
        final TextView text_pet = findViewById(R.id.text_pet2);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                URL,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++){
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String string = object.getString("KOSILO");
                                switch (i) {
                                    case 0:
                                        text_pon.setText(string);
                                    case 1:
                                        text_tor.setText(string);
                                    case 2:
                                        text_sre.setText(string);
                                    case 3:
                                        text_cet.setText(string);
                                    case 4:
                                        text_pet.setText(string);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

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


        requestQueue.add(jsonArrayRequest);
    }
}
