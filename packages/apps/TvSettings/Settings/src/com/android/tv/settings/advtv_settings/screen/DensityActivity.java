package com.android.tv.settings.advtv_settings.screen;

import android.app.Fragment;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;


public class DensityActivity extends TvSettingsActivity {

    
    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        @Override 
        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(DensityFragment.newInstance());
        }
    }

    @Override 
    protected Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }
}
