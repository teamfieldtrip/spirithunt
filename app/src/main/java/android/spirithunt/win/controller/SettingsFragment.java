package android.spirithunt.win.controller;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.spirithunt.win.R;

/**
 * Created by sven on 30-3-17.
 */

public class SettingsFragment extends PreferenceFragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_view);
    }
}
