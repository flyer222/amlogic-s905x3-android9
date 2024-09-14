package com.android.tv.settings.display.outputmode;

import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;

import android.app.Fragment;

/**
 * Activity to control Output mode settings.
 */
public class OutputmodeActivity extends TvSettingsActivity {

    @Override
    protected Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }

    public static class SettingsFragment extends BaseSettingsFragment {

        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        @Override
        public void onPreferenceStartInitialScreen() {
            final OutputmodeFragment fragment = OutputmodeFragment.newInstance();
            startPreferenceFragment(fragment);
        }
    }
}


