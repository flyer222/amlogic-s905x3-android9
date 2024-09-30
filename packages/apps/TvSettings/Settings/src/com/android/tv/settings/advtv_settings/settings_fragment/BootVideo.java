package com.android.tv.settings.advtv_settings.settings_fragment;

import android.os.Bundle;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;


public class BootVideo extends SettingsPreferenceFragment {
    public static BootVideo newInstance() {
        return new BootVideo();
    }

    public int getMetricsCategory() {
        return 1;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.adv_bootvideo, null);
    }
}
