package it.unozerouno.givemetime.view.main.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import it.unozerouno.givemetime.R;

/**
 * Fragment handling the menu settings
 */
public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
    
}
