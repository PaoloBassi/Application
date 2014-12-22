package it.unozerouno.givemetime.view.main.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.DeniedByServerException;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;
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
        
        
        //Bind Debug Preferences
       Preference debugWipePrefs = (Preference) findPreference("wipeSharedPrefs");
        debugWipePrefs.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference arg0) {
				
				//TODO: This is not wiping all the settings correctly
				SharedPreferences sharedPrefs = getActivity().getSharedPreferences("my_preferences", Context.MODE_PRIVATE);
				sharedPrefs.edit().clear();
				sharedPrefs.edit().commit();
				Toast toast = Toast.makeText(getActivity(), "Settings Wiped", Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		});
        
    }
    
}
