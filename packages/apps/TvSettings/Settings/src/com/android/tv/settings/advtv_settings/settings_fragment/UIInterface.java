package com.android.tv.settings.advtv_settings.settings_fragment;

import android.os.Bundle;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;

public class UIInterface extends SettingsPreferenceFragment {
    public static UIInterface newInstance() {
        return new UIInterface();
    }
    public int getMetricsCategory() {
        return 1;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        setPreferencesFromResource(R.xml.advtv_interface, null);
    }
}
