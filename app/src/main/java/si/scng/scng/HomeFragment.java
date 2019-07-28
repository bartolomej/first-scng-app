package si.scng.scng;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class HomeFragment extends Fragment {

    String URL_GET_STATS = "http://83.212.82.188/rating/stats.php?api_key=j7db4l30h7";
    String daily_stats = null;
    String top_rated = null;

    LinearLayout linearLayout;
    LayoutInflater inflater;

    ArrayList<View> cards = new ArrayList<View>();
    ArrayList<TextView> texts = new ArrayList<TextView>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        new BcgProcess(HomeFragment.this).execute("Novice");

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        linearLayout = (LinearLayout) view.findViewById(R.id.cards);
        return view;

    }

    public void setCard(JSONObject jsonObject) throws JSONException {
        final String url = jsonObject.getString("link");;
        Log.d("NEWS OBJECT: ", jsonObject.toString());
        View card =  inflater.inflate(R.layout.home_card, linearLayout, false);
        TextView date = card.findViewById(R.id.news_date);
        TextView title = card.findViewById(R.id.news_title);
        TextView text = card.findViewById(R.id.news_text);
        try {
            date.setText(jsonObject.getString("date"));
            title.setText(jsonObject.getString("title").replaceFirst(" ", ""));
            text.setText(jsonObject.getString("desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });
        linearLayout.addView(card);
    }

}

class BcgProcess extends AsyncTask<String, JSONObject, ArrayList<String>> {

    HomeFragment fragment;

    public BcgProcess(HomeFragment fragment) {
        this.fragment = fragment;
    }

    @Override
    protected ArrayList<String> doInBackground(String... filename) {
        JSONArray jsonArray = null;
        try {
            jsonArray = JSONHelper.readJsonArrayFile(filename[0], fragment.getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                publishProgress((JSONObject) jsonArray.get(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(JSONObject... values) {
        super.onProgressUpdate(values);
        try {
            fragment.setCard(values[0]);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        super.onPostExecute(strings);
    }
}
