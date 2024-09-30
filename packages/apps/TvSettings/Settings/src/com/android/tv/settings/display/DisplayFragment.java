/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package com.android.tv.settings.display;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;

import com.droidlogic.app.SdrManager;

import com.android.tv.settings.EnvProperty;
import com.android.tv.settings.util.DroidUtils;
import com.android.tv.settings.SettingsConstant;
import com.android.tv.settings.R;
import com.android.tv.settings.LeanbackAddBackPreferenceFragment;

public class DisplayFragment extends LeanbackAddBackPreferenceFragment {

    private static final String TAG = "DisplayFragment";

    private static final String KEY_POSITION = "position";
    private static final String KEY_OUTPUTMODE = "outputmode";
    private static final String KEY_HDR = "hdr";
    private static final String KEY_SDR = "sdr";
    private static final String KEY_DOLBY_VISION    = "dolby_vision";
    private static final String KEY_PQ_SETTINGS = "pq_custom";

    private static final String PERSIST_PQ_SETTINGS = "persist.pq.settings";

    private SdrManager mSdrManager;

    public static DisplayFragment newInstance() {
        return new DisplayFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.display, null);
        mSdrManager = new SdrManager((Context) getActivity());

        boolean isHdrSupport = mSdrManager.isHdrSupport();

        final Preference outputmodePref = findPreference(KEY_OUTPUTMODE);
        outputmodePref.setVisible(SettingsConstant.needScreenResolutionFeture(getContext()));

        final Preference screenPositionPref = findPreference(KEY_POSITION);
        screenPositionPref.setVisible(true);

        final Preference sdrPref = findPreference(KEY_SDR);
        boolean sdrFeature = SettingsConstant.needDroidlogicSdrFeature(getContext());
        sdrPref.setVisible(isHdrSupport&&sdrFeature);

        final Preference hdrPref = findPreference(KEY_HDR);
        boolean hdrFeature = SettingsConstant.needDroidlogicHdrFeature(getContext());
        hdrPref.setVisible(!isHdrSupport||hdrFeature);

        final Preference dvPref =(Preference) findPreference(KEY_DOLBY_VISION);
        dvPref.setVisible(false);

        final Preference pqCustomPref = findPreference(KEY_PQ_SETTINGS);
        pqCustomPref.setVisible(EnvProperty.getBoolean(PERSIST_PQ_SETTINGS, true));
    }
}
