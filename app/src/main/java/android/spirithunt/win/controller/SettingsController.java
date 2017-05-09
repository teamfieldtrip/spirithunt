package android.spirithunt.win.controller;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.spirithunt.win.R;

/**
 * Created by sven on 30-3-17.
 */

public class SettingsController extends PreferenceActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
    }
}
