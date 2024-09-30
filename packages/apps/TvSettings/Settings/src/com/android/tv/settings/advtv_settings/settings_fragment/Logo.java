package com.android.tv.settings.advtv_settings.settings_fragment;

import android.os.Bundle;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

public class Logo extends SettingsPreferenceFragment {
    public static Logo newInstance() {
        return new Logo();
    }


    public int getMetricsCategory() {
        return 1;
    }


    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.adv_logo, null);
    }
}

