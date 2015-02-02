package it.unozerouno.givemetime.view.main;

import it.unozerouno.givemetime.R;
import it.unozerouno.givemetime.view.main.fragments.SettingsFragment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

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
