package com.android.tv.settings.system;

import android.app.Fragment;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;

public class HomeSettingsActivity extends TvSettingsActivity {

    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        @Override // android.support.v17.preference.LeanbackSettingsFragment
        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(HomeSettingsFragment.newInstance());
        }
    }

    @Override // com.android.tv.settings.TvSettingsActivity
    protected Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }
}
