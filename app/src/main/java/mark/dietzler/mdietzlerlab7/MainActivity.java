package mark.dietzler.mdietzlerlab7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

public class MainActivity extends AppCompatActivity implements Observer{

    Clock clock;
    TextView timeView;

    //this is fine
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clock = (Clock)findViewById(R.id.clock);
        timeView = (TextView)findViewById(R.id.digitalClock);
        clock.hourMinSec.addObserver(this);
    }

    //this is fine
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //this is fine
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if(id == R.id.action_about) {
            Toast.makeText(this, "Lab 7, Winter 2019, Mark Dietzler",
                    Toast.LENGTH_SHORT)
                    .show();
            return true;
        }

        if(id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * sets the digital clock on the main clock view
     * @param observableInput
     * @param obsTimeTransfer
     */
    @Override
    public void update(Observable observableInput, Object obsTimeTransfer) {
        String digitalClockTime = new String();
        if(observableInput != null) {
            int hour,minute,second;
            hour = clock.hourMinSec.getHour();
            minute = clock.hourMinSec.getMin();
            second = clock.hourMinSec.getSec();
            digitalClockTime = String.format("%02d:%02d:%02d",hour,minute,second );
            timeView.setText(digitalClockTime);
        } else {
            timeView.setText("XX:XX:XX");
        }

    }

    /**
     * pulls preferences out of SharedPreferences when the main clock view resumes from setting screen, or from minimize
     */
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean prefsHourFormat = false;
        Boolean prefsPartialSecondsFormat = false;
        String prefsClockFace = "";

        if(prefs.contains("hourFormat")) {
            prefsHourFormat = prefs.getBoolean("hourFormat", clock.hourFormat);
        }
        if(prefs.contains("partialSeconds")) {
            prefsPartialSecondsFormat = prefs.getBoolean("partialSeconds", clock.partialSeconds);
        }
        if(prefs.contains("prefClockFace")) {
            prefsClockFace = prefs.getString("prefClockFace", clock.GetClockFace());
        }

        clock.hourFormat = prefsHourFormat;
        clock.partialSeconds = prefsPartialSecondsFormat;
        clock.SetClockFace(prefsClockFace);
        clock.invalidate();
    }
}
