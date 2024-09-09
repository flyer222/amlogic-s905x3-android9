package com.android.tv.settings.advtv_settings;

import android.os.Bundle;
import android.support.v7.preference.Preference;
//import android.ugoos.root.RootManager;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

public class AdvtvSettingsFragment extends SettingsPreferenceFragment {
    Preference root;

    private boolean isRooted() {
	return false;
        //return ((RootManager) getPreferenceManager().getContext().getSystemService("root")).getRootState() != 0;
    }

    public static AdvtvSettingsFragment newInstance() {
        return new AdvtvSettingsFragment();
    }

    public int getMetricsCategory() {
        return 1;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.adv_settings, null);
        this.root = findPreference("root");
	//this.root.setIcon(isRooted() ? R.drawable.ic_settings_root_on : R.drawable.ic_settings_root_off);
    }

    public void onResume() {
        super.onResume();
        //this.root.setIcon(isRooted() ? R.drawable.ic_settings_root_on : R.drawable.ic_settings_root_off);
    }
}
