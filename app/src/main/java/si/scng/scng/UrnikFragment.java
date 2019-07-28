package si.scng.scng;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.Inflater;


public class UrnikFragment extends Fragment {

    LinearLayout urnikLayout;
    LayoutInflater inflater;

    Calendar calendar;
    SimpleDateFormat date;
    Date nowDate;
    String stringDay;
    int intDay;
    String formatedDate;
    int loopDay;

    SharedPreferences sharedPreferences;
    String groupVar;

    //https://stackoverflow.com/questions/37759734/dynamically-updating-a-fragment/37761276
    /*public interface DataUpdateListener {
        void onDataUpdate();
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflater = inflater;

        calendar = Calendar.getInstance();
        date = new SimpleDateFormat("dd-MM-yyyy");
        formatedDate = date.format(calendar.getTime());
        nowDate = new Date();
        calendar.setTime(nowDate);
        intDay = calendar.get(Calendar.DAY_OF_WEEK);
        Log.d("Day", Integer.toString(intDay));
        stringDay = Integer.toString(intDay);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        groupVar = sharedPreferences.getString("default_class_group", "none");
        Log.d("PREF GROUP", groupVar);

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

        try {
            new UrnikProcess(this, loopDay).execute(JSONHelper.readJsonArrayFile("Urnik", getContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        View view = inflater.inflate(R.layout.fragment_urnik, container, false);
        urnikLayout = (LinearLayout)view.findViewById(R.id.urnik);

        return view;
    }

    /**
     * sets urnik cards
     * @param arrayList
     */
    public void setUrnikCards(ArrayList<JSONArray> arrayList) {
        View urnik = inflater.inflate(R.layout.urnik_card, urnikLayout, false);

        /**
         * initialized urnik cards
         */
        TextView title1 = urnik.findViewById(R.id.name1);
        TextView class1 = urnik.findViewById(R.id.class1);
        TextView foks1 = urnik.findViewById(R.id.foks1);

        TextView title2 = urnik.findViewById(R.id.name2);
        TextView class2 = urnik.findViewById(R.id.class2);
        TextView foks2 = urnik.findViewById(R.id.foks2);

        TextView title3 = urnik.findViewById(R.id.name3);
        TextView class3 = urnik.findViewById(R.id.class3);
        TextView foks3 = urnik.findViewById(R.id.foks3);

        TextView title4 = urnik.findViewById(R.id.name4);
        TextView class4 = urnik.findViewById(R.id.class4);
        TextView foks4 = urnik.findViewById(R.id.foks4);

        TextView title5 = urnik.findViewById(R.id.name5);
        TextView class5 = urnik.findViewById(R.id.class5);
        TextView foks5 = urnik.findViewById(R.id.foks5);

        TextView title6 = urnik.findViewById(R.id.name6);
        TextView class6 = urnik.findViewById(R.id.class6);
        TextView foks6 = urnik.findViewById(R.id.foks6);

        TextView title7 = urnik.findViewById(R.id.name7);
        TextView class7 = urnik.findViewById(R.id.class7);
        TextView foks7 = urnik.findViewById(R.id.foks7);

        TextView title8 = urnik.findViewById(R.id.name8);
        TextView class8 = urnik.findViewById(R.id.class8);
        TextView foks8 = urnik.findViewById(R.id.foks8);

        switch (arrayList.size()) {
            case 8:
                try {
                    title8.setText(arrayList.get(7).getString(1));
                    foks8.setText(arrayList.get(7).getString(2));
                    /**
                     * checks if array is for groups (skupine)
                     * if length == 4 --> groups
                     */
                    if (arrayList.get(7).length() == 4) {
                        class8.setText("SKUPINE");
                    } else if (arrayList.get(6).length() < 4) {
                        class8.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case 7:
                try {
                    title7.setText(arrayList.get(6).getString(1));
                    foks7.setText(arrayList.get(6).getString(2));
                    if (arrayList.get(6).length() == 4) {
                        class7.setText("SKUPINE");
                    } else if (arrayList.get(5).length() < 4) {
                        class7.setText(""); }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case 6:
                try {
                    title6.setText(arrayList.get(5).getString(1));
                    foks6.setText(arrayList.get(5).getString(2));
                    if (arrayList.get(5).length() == 4) {
                        class6.setText("SKUPINE");
                    } else if (arrayList.get(5).length() < 4) {
                        class6.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case 5:
                try {
                    Log.d("URNIK", arrayList.toString());
                    Log.d("ODMOR", arrayList.get(4).toString());
                    if (arrayList.get(4).getString(0).equals("empty")) {
                        title5.setText("ODMOR");
                        foks5.setText("");
                        class5.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
        try {
            title4.setText(arrayList.get(3).getString(1));
            foks4.setText(arrayList.get(3).getString(2));
            if (arrayList.get(3).length() == 4) {
                class4.setText("SKUPINE");
            } else if (arrayList.get(3).length() < 4) {
                class4.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            title3.setText(arrayList.get(2).getString(1));
            foks3.setText(arrayList.get(2).getString(2));
            if (arrayList.get(2).length() == 4) {
                class3.setText("SKUPINE");
            } else if (arrayList.get(2).length() < 4) {
                class3.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            title2.setText(arrayList.get(1).getString(1));
            foks2.setText(arrayList.get(1).getString(2));
            if (arrayList.get(1).length() == 4) {
                class2.setText("SKUPINE");
            } else if (arrayList.get(1).length() < 4) {
                class2.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            title1.setText(arrayList.get(0).getString(1));
            foks1.setText(arrayList.get(0).getString(2));
            if (arrayList.get(0).length() == 4) {
                class1.setText("SKUPINE");
            } else if (arrayList.get(0).length() < 4) {
                class1.setText("");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        urnikLayout.addView(urnik);
    }


}

/**
 * background proccessing on urnik data
 */
class UrnikProcess extends AsyncTask<JSONArray, ArrayList<JSONArray>, ArrayList<JSONArray>> {

    UrnikFragment context;
    int day;

    public UrnikProcess(UrnikFragment context, int day) {
        this.context = context;
        this.day = day;
    }

    @Override
    protected ArrayList<JSONArray> doInBackground(JSONArray... jsonArrays) {
        JSONArray jsonArray = null;
        ArrayList<JSONArray> arrayList = new ArrayList<>();
        try {
             jsonArray = jsonArrays[0].getJSONArray(day);
             Log.d("URNIK ARRAY: ", jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (int i=1; i < jsonArray.length(); i++) {

            try {
                Log.d("jsonArray[0]", jsonArray.getJSONArray(5).getString(0).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {

                if (jsonArray.getJSONArray(i).getString(0).equals("empty")) {
                    arrayList.add(jsonArray.getJSONArray(i));
                } else {
                    if (jsonArray.getJSONArray(i).getString(0).equals("normal")) {
                        arrayList.add(jsonArray.getJSONArray(i).getJSONArray(1));

                    } else if (jsonArray.getJSONArray(i).getString(0).equals("group")) {

                        if (jsonArray.getJSONArray(i).getJSONArray(2).getJSONArray(0).getJSONArray(0).getString(3).equalsIgnoreCase(context.groupVar)) {
                            arrayList.add(jsonArray.getJSONArray(i).getJSONArray(2).getJSONArray(0).getJSONArray(0));
                            Log.d("8 URA - 1: ", jsonArray.getJSONArray(8).getJSONArray(2).getJSONArray(0).getJSONArray(0).toString());
                        } else if (jsonArray.getJSONArray(i).getJSONArray(2).getJSONArray(0).getJSONArray(1).getString(3).equals(this.context.groupVar)) {
                            arrayList.add(jsonArray.getJSONArray(i).getJSONArray(2).getJSONArray(0).getJSONArray(1));
                            Log.d("8 URA - 2: ", jsonArray.getJSONArray(8).getJSONArray(2).getJSONArray(0).getJSONArray(0).toString());
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("BCG JEDILNIK OBJ: ", arrayList.toString());
        return arrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<JSONArray> arrayList) {
        context.setUrnikCards(arrayList);
    }

    @Override
    protected void onProgressUpdate(ArrayList<JSONArray>... values) {
        super.onProgressUpdate(values);
    }

}
