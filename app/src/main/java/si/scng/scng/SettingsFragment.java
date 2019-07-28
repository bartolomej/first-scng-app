package si.scng.scng;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    MainActivity mainActivity = new MainActivity();
                    mainActivity.requestUrnik();
                    mainActivity.requestJedilnik();

                    Fragment currentFragment = new UrnikFragment();
                    if (currentFragment instanceof UrnikFragment) {
                        android.support.v4.app.FragmentTransaction fragTransaction =  getActivity().getSupportFragmentManager().beginTransaction();
                        fragTransaction.detach(currentFragment);
                        fragTransaction.attach(currentFragment);
                        fragTransaction.commit();}
                }
            };
        prefs.registerOnSharedPreferenceChangeListener(listener);
        addPreferencesFromResource(R.xml.pref_screen);
        };

    }

