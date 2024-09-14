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

package com.android.tv.settings.display.outputmode;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v14.preference.SwitchPreference;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.TwoStatePreference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.android.tv.settings.EnvProperty;
import com.android.tv.settings.SettingsConstant;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;
import com.android.tv.settings.R;
import com.android.tv.settings.LeanbackAddBackPreferenceFragment;

import com.droidlogic.app.DolbyVisionSettingManager;


public class ScreenResolutionFragment extends LeanbackAddBackPreferenceFragment implements
        OnClickListener {

    private static final String KEY_COLORSPACE = "colorspace_setting";
    private static final String KEY_COLORDEPTH = "colordepth_setting";
    private static final String KEY_DISPLAYMODE = "displaymode_setting";
    private static final String KEY_DOLBYVISION = "dolby_vision";
    private static final String KEY_DOLBYVISION_PRIORITY = "dolby_vision_graphics_priority";
    private static final String KEY_AUTOFRAMERATE = "auto_framerate";
    private static final String DEFAULT_VALUE = "444,8bit";

    private String preMode;
    private String preDeepColor;
    private View view_dialog;
    private TextView tx_title;
    private TextView tx_content;
    private Timer timer;
    private TimerTask task;
    private AlertDialog mAlertDialog = null;
    private int countdown = 15;
    private static String mode = null;
    private static final int MSG_FRESH_UI = 0;
    private static final int MSG_COUNT_DOWN = 1;
    private static final int MSG_PLUG_FRESH_UI = 2;

    private DolbyVisionSettingManager mDolbyVisionSettingManager;
    private Preference mDisplayModePref;
    private Preference mDeepColorPref;
    private Preference mColorDepthPref;
    private Preference mDolbyVisionPref;
    private Preference mGraphicsPriorityPref;
    private OutputUiManager mOutputUiManager;
    private IntentFilter mIntentFilter;
    public boolean hpdFlag = false;

    private AutoFramerateManager autoFramerateManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FRESH_UI:
                    updateScreenResolutionDisplay();
                    break;
                case MSG_COUNT_DOWN:
                    tx_title.setText(Integer.toString(countdown) + " " +
                        getResources().getString(R.string.device_countdown));
                    if (countdown == 0) {
                        if (mAlertDialog != null) {
                            mAlertDialog.dismiss();
                        }
                        task.cancel();
                    }
                    countdown--;
                    break;
            }
        }
    };
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hpdFlag = intent.getBooleanExtra ("state", false);
            mHandler.sendEmptyMessageDelayed(MSG_FRESH_UI, hpdFlag ^ isHdmiMode() ? 2000 : 1000);
        }
    };

    public static ScreenResolutionFragment newInstance() {
        return new ScreenResolutionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOutputUiManager = new OutputUiManager(getActivity());
        autoFramerateManager = AutoFramerateManager.getManager();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.screen_resolution, null);

        mDolbyVisionSettingManager = new DolbyVisionSettingManager((Context) getActivity());
        mDisplayModePref = findPreference(KEY_DISPLAYMODE);
        mDeepColorPref = findPreference(KEY_COLORSPACE);
        mColorDepthPref = findPreference(KEY_COLORDEPTH);
        mDolbyVisionPref = findPreference(KEY_DOLBYVISION);
        mIntentFilter = new IntentFilter("android.intent.action.HDMI_PLUGGED");
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        mGraphicsPriorityPref = findPreference(KEY_DOLBYVISION_PRIORITY);

        TwoStatePreference autoFramePref = (TwoStatePreference)findPreference(KEY_AUTOFRAMERATE);

        if (autoFramerateManager == null)
            autoFramerateManager = AutoFramerateManager.getManager();

        autoFramePref.setEnabled(autoFramerateManager.canUseIt());
        autoFramePref.setChecked(autoFramerateManager.isActive());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof SwitchPreference) {
            TwoStatePreference autoFrame = (TwoStatePreference) preference;
            if (autoFrame.isChecked())
                autoFramerateManager.start();
            else
                autoFramerateManager.stop();
        }
        return super.onPreferenceTreeClick(preference);
    }

        @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);
        mHandler.sendEmptyMessage(MSG_FRESH_UI);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mIntentReceiver);
    }

    private void updateScreenResolutionDisplay() {
        mOutputUiManager.updateUiMode();

        // set dolby vision summary.
        if (true == mDolbyVisionSettingManager.isDolbyVisionEnable()) {
            if (mDolbyVisionSettingManager.getDolbyVisionType() == 2) {
                mDolbyVisionPref.setSummary(R.string.dolby_vision_low_latency_yuv);
            } else if (mDolbyVisionSettingManager.getDolbyVisionType() == 3) {
                mDolbyVisionPref.setSummary(R.string.dolby_vision_low_latency_rgb);
            } else {
                mDolbyVisionPref.setSummary(R.string.dolby_vision_default_enable);
            }
        } else {
            mDolbyVisionPref.setSummary(R.string.dolby_vision_off);
        }
        if (mDolbyVisionSettingManager.getGraphicsPriority().equals("1")) {
            mGraphicsPriorityPref.setSummary(R.string.graphics_priority);
        } else if (mDolbyVisionSettingManager.getGraphicsPriority().equals("0")) {
            mGraphicsPriorityPref.setSummary(R.string.video_priority);
        }

        /* Convert VU's name from resolution */
        String curDisplayMode = getCurrentDisplayMode();
        {
            if (curDisplayMode.equals("800x480p60hz")) {
                if (mOutputUiManager.isVoutmodeHdmi())
                    curDisplayMode = "ODROID-VU5A";
                else
                    curDisplayMode = "ODROID-VU5/7";
            }
            else if (curDisplayMode.equals("1024x600p60hz")) {
                if (mOutputUiManager.isVoutmodeHdmi())
                    curDisplayMode = "ODROID-VU7A Plus";
                else
                    curDisplayMode = "ODROID-VU7 Plus";
            }
            else if (curDisplayMode.equals("1024x768p60hz"))
                curDisplayMode = "ODROID-VU8";
            else
            {}
        }

        mDisplayModePref.setSummary(curDisplayMode);
        if (isHdmiMode()) {
            mDeepColorPref.setVisible(true);
            mDeepColorPref.setSummary(mOutputUiManager.getCurrentColorSpaceTitle());
            mColorDepthPref.setVisible(true);
            mColorDepthPref.setSummary(
                mOutputUiManager.getCurrentColorDepthAttr().contains("8bit") ? "off":"on");
        } else {
            mDeepColorPref.setVisible(false);
            mColorDepthPref.setVisible(false);
        }
        boolean dvFlag = mOutputUiManager.isDolbyVisionEnable()
            && mOutputUiManager.isTvSupportDolbyVision();
        if (dvFlag) {
            mDeepColorPref.setEnabled(false);
            mColorDepthPref.setEnabled(false);
        } else {
            mDeepColorPref.setEnabled(true);
            mColorDepthPref.setEnabled(true);
        }
        //only S912 as Mbox, T962E as Mbox, can display this options
        //T962E as TV and T962X, display in Settings-->Display list.
        if (EnvProperty.getBoolean("ro.vendor.platform.support.dolbyvision", false) == true) {
            if (isHdmiMode()) {
                mDolbyVisionPref.setVisible(true);
                mGraphicsPriorityPref.setVisible(
                    mDolbyVisionSettingManager.isDolbyVisionEnable() ? true:false);
            } else {
                mDolbyVisionPref.setVisible(false);
                mGraphicsPriorityPref.setVisible(false);
            }
        } else {
            mDolbyVisionPref.setVisible(false);
            mGraphicsPriorityPref.setVisible(false);
        }
    }

    /**
     * Taggle best resolution state.
     * if current best resolution state is enable, it will disable best resolution after method.
     * if current best resolution state is disable, it will enable best resolution after method.
     */
    private String getCurrentDisplayMode() {
        return mOutputUiManager.getCurrentMode();
    }
    private boolean isHdmiMode() {
        return mOutputUiManager.isHdmiMode();
    }

    /**
     * show Alert Dialog to Users.
     * Tips: Users can confirm current state, or cancel to recover previous state.
     */
    private void showDialog () {
        if (mAlertDialog == null) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view_dialog = inflater.inflate(R.layout.dialog_outputmode, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);

            tx_title = (TextView)view_dialog.findViewById(R.id.dialog_title);
            tx_content = (TextView)view_dialog.findViewById(R.id.dialog_content);

            TextView button_cancel = (TextView)view_dialog.findViewById(R.id.dialog_cancel);
            button_cancel.setOnClickListener(this);

            TextView button_ok = (TextView)view_dialog.findViewById(R.id.dialog_ok);
            button_ok.setOnClickListener(this);
        }
        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view_dialog);
        mAlertDialog.setCancelable(false);

        tx_content.setText(getResources().getString(R.string.device_outputmode_change)
            + " " +getCurrentDisplayMode());

        countdown = 15;
        if (timer == null)
            timer = new Timer();
        if (task != null)
            task.cancel();
        task = new DialogTimerTask();
        timer.schedule(task, 0, 1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                break;
            case R.id.dialog_ok:
                if (mAlertDialog != null) {
                    mAlertDialog.dismiss();
                }
                break;
        }
        task.cancel();
    }
    private class DialogTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
            }
        }
    };
}
