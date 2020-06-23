package mark.dietzler.mdietzlerlab7;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public SettingsFragment() {
        // required empty public constructor
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Preference prefThatWasChanged = findPreference(key);

        switch(key) {
            case "hourformat":
                //do nothing with hour format
                prefThatWasChanged.setSummary("XXX");
                break ;
            case "clockFace":
                String testClockFace = sharedPreferences.getString(key,"");
                //sharedPreferences.edit().putString(key,"math").commit();
                prefThatWasChanged.setSummary("YYY");
                break ;
            case "partialseconds":
                //do nothing with second hand either
                prefThatWasChanged.setSummary("ZZZ");
                break ;
            case "testing_pref":
                int updateInterval = Integer.parseInt(sharedPreferences.getString(key,""));
                if( updateInterval < 100) updateInterval = 100;
                if(updateInterval > 5000) updateInterval = 5000;
                // over wright update interval already written
                sharedPreferences.edit().putString(key, Integer.toString(updateInterval)).commit();
                // set the summary
                prefThatWasChanged.setSummary("Time interval: " + updateInterval);
                // make sure the constrained value shows up in the edit box if reselected
                ((EditTextPreference)prefThatWasChanged).setText(Integer.toString(updateInterval));
                break ;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);

        SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();
        onSharedPreferenceChanged(prefs, "pref_upint");
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
