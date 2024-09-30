package com.android.tv.settings.advtv_settings;

import android.app.Fragment;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;

public class AdvtvSettingsActivity extends TvSettingsActivity {

    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(AdvtvSettingsFragment.newInstance());
        }
    }

    protected Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }
}
