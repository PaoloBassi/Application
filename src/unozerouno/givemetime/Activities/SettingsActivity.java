package unozerouno.givemetime.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import unozerouno.givemetime.Fragments.SettingsFragment;

/**
 * Activity showing the settings menu
 */
public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display the Settings Fragment as the main content
        getFragmentManager().beginTransaction()
                // preference fragment doesn't have it's own context
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
