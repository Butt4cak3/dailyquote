package io.github.dasspike.dailyquote;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * This fragment is used for the 'Settings' page in the navigation drawer.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
        // TODO: Populate settings and hook them up
    }

}
