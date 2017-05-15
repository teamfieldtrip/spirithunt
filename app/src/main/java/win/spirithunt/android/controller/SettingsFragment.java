package win.spirithunt.android.controller;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import win.spirithunt.android.R;

/**
 * @author Sven Boekelder
 * @author Roelof Roos
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private String logoutKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings_view);

        // Get the logout key, because caching everything FTW
        logoutKey = getString(R.string.setting_logout_key);

        // Get the preference and link it to this fragment
        Preference logoutPreference = findPreference(logoutKey);
        logoutPreference.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // Don't handle click on non-logout action
        if (!preference.getKey().equals(logoutKey)) return false;

        SettingsController activity = (SettingsController) this.getActivity();
        activity.logoutUser();

        return true;
    }
}
