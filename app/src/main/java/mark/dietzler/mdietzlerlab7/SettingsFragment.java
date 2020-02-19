package mark.dietzler.mdietzlerlab7;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
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
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
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
