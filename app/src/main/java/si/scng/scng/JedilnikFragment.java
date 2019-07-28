package si.scng.scng;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class JedilnikFragment extends Fragment {

    Calendar calendar;
    SimpleDateFormat date;
    Date nowDate;
    String stringDay;
    int intDay;
    String formatedDate;

    int loopDay = 0;

    CardView b_menu1;
    CardView b_menu2;
    CardView s_menu1;
    CardView s_menu2;

    TextView b_text1;
    TextView b_text2;
    TextView s_text1;
    TextView s_text2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy");
        formatedDate = date.format(calendar.getTime());
        nowDate = new Date();
        calendar.setTime(nowDate);
        intDay = calendar.get(Calendar.DAY_OF_WEEK);
        stringDay = Integer.toString(intDay);

        switch (intDay) {
            case 1:
                loopDay = 0;
                break;
            case 7:
                loopDay = 0;
                break;
                default:
                    loopDay = intDay - 2;
                    break;
        }

        File file = new File(  getActivity().getApplicationContext().getFilesDir().getAbsolutePath() + "/" + "Jedilnik" + ".json");
        new BcgJedilnik(this, loopDay).execute(file);

        View view = inflater.inflate(R.layout.fragment_jedilnik, container, false);

        s_menu1 = (CardView) view.findViewById(R.id.spon_menu1);
        s_menu2 = (CardView) view.findViewById(R.id.spon_menu2);
        b_menu1 = (CardView) view.findViewById(R.id.bios_menu1);
        b_menu2 = (CardView) view.findViewById(R.id.bios_menu2);

        s_text1 = s_menu1.findViewById(R.id.card_text_1);
        s_text2 = s_menu2.findViewById(R.id.card_text_2);
        b_text1 = b_menu1.findViewById(R.id.card_text_3);
        b_text2 = b_menu2.findViewById(R.id.card_text_4);

        s_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu1 = new Intent(getActivity(), MenuActivity.class);
                menu1.putExtra("meal", "spon");
                menu1.putExtra("activity", "menu1");
                menu1.putExtra("title", "ŠPON - menu 1");
                startActivity(menu1);
            }
        });

        s_menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent menu2 = new Intent(getActivity(), MenuActivity.class);
                menu2.putExtra("meal", "spon");
                menu2.putExtra("activity", "menu2");
                menu2.putExtra("title", "ŠPON - menu 2");
                startActivity(menu2);
            }
        });

        b_menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kosilo = new Intent(getActivity(), MenuActivity.class);
                kosilo.putExtra("meal", "bios");
                kosilo.putExtra("activity", "menu1");
                kosilo.putExtra("title", "BIOS");
                startActivity(kosilo);
            }
        });

        b_menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kosilo = new Intent(getActivity(), MenuActivity.class);
                kosilo.putExtra("meal", "bios");
                kosilo.putExtra("activity", "menu2");
                kosilo.putExtra("title", "BIOS");
                startActivity(kosilo);
            }
        });

        return view;
    }

    /**
     * sets jedilnik text
     * @param array
     */
    public void setJedilnik(ArrayList<String> array) {
        b_text1.setText(array.get(0));
        b_text2.setText(array.get(1));
        s_text1.setText(array.get(2));
        s_text2.setText(array.get(3));
    }

    public void incrementLoop() {
        this.loopDay++;
    }

}

/**
 * background proccessing on jedilnik data
 */
class BcgJedilnik extends AsyncTask<File, String, ArrayList<String>> {

    JedilnikFragment activity;
    int day;

    public BcgJedilnik(JedilnikFragment activity, int day) {
        this.day = day;
        this.activity = activity;
    }

    @Override
    protected ArrayList<String> doInBackground(File... file) {
        StringBuilder text = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file[0]));
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

        ArrayList<JsonArray> values = new ArrayList<JsonArray>();
        String concat = "";
        ArrayList<String> valuesForView = new ArrayList<String>();
        JSONObject sponObject = null;
        JSONObject biosObject = null;
        try {
            sponObject = jsonObject.getJSONObject("spon");
            biosObject = jsonObject.getJSONObject("bios");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonParser jsonParser = new JsonParser();
        String[] days = {"mon", "tue", "wed", "thu", "fri"};
        try {
            values.add(jsonParser.parse(sponObject.getJSONObject(days[day]).getString("menu1")).getAsJsonArray());
            values.add(jsonParser.parse(sponObject.getJSONObject(days[day]).getString("menu2")).getAsJsonArray());
            values.add(jsonParser.parse(biosObject.getJSONObject(days[day]).getString("menu1")).getAsJsonArray());
            values.add(jsonParser.parse(biosObject.getJSONObject(days[day]).getString("menu2")).getAsJsonArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < values.size(); i++) {
            concat = "";
            for (int j = 0; j < values.get(i).size(); j++) {
                concat = concat + " - " +values.get(i).get(j).getAsString().replace(",", "");
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
