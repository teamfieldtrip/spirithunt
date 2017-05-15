package win.spirithunt.android.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import io.socket.client.Ack;
import io.socket.client.Socket;
import win.spirithunt.android.R;
import win.spirithunt.android.protocol.AuthLogin;
import win.spirithunt.android.provider.SocketProvider;

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

        logoutUser();
        return true;
    }

    /**
     * Logs a user out, ends the activity and returns to the login screen.
     */
    protected void logoutUser() {
        // Get SharedPreferences
        SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Emit to the server
        Socket socket = SocketProvider.getInstance().getConnection();

        socket.emit("auth:logout", sharedPref.getString(getString(R.string.saved_jwt), ""), new Ack() {
            @Override
            public void call(final Object... args) {

            }
        });

        // Remove JSON Web Token from SharedPreferences
        editor.remove(getString(R.string.saved_jwt));

        // Safe to do async, as the in-memory entry is instantly updated.
        editor.apply();

        // Get the LoginIntent
        Intent intent = new Intent(this.getContext(), LoginController.class);

        // Don't save history
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Don't animate after logout
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // Start activity and end this one.
        startActivity(intent);
        getActivity().finish();
    }
}
