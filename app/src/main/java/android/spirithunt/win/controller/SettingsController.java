package android.spirithunt.win.controller;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by sven on 30-3-17.
 */

public class SettingsController extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
            .replace(android.R.id.content, new SettingsFragment())
            .commit();
    }
}
