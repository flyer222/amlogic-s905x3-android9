package com.android.tv.settings.inputmethod;

import android.app.Fragment;
import com.android.tv.settings.BaseSettingsFragment;
import com.android.tv.settings.TvSettingsActivity;

public class MouseActivity extends TvSettingsActivity {

    public static class SettingsFragment extends BaseSettingsFragment {
        public static SettingsFragment newInstance() {
            return new SettingsFragment();
        }

        public void onPreferenceStartInitialScreen() {
            startPreferenceFragment(MouseFragment.newInstance());
        }
    }

    protected Fragment createSettingsFragment() {
        return SettingsFragment.newInstance();
    }
}
