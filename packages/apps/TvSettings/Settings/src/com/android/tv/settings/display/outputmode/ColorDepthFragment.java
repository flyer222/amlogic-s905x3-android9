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
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.text.TextUtils;
import com.android.tv.settings.dialog.old.Action;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.R;
import com.android.tv.settings.LeanbackAddBackPreferenceFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;

import android.widget.TextView;
import android.widget.Toast;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Keep
public class ColorDepthFragment extends LeanbackAddBackPreferenceFragment
implements OnClickListener {
    private static final String LOG_TAG = "ColorDepthFragment";
    private OutputUiManager mOutputUiManager;
    private static String saveValue = null;
    private static String curValue = null;
    private static String curMode = null;

    private static String curColor = null;
    private static String preColor = null;
    RadioPreference prePreference;
    RadioPreference curPreference;
    private View view_dialog;
    private TextView tx_title;
    private TextView tx_content;
    private Timer timer;
    private TimerTask task;
    private AlertDialog mAlertDialog = null;
    private int countdown = 15;
    private static final int MSG_FRESH_UI = 0;
    private static final int MSG_COUNT_DOWN = 1;
    private static final int MSG_RESET_COLOR = 2;
    private IntentFilter mIntentFilter;
    public boolean hpdFlag = false;
    private static final String DEFAULT_COLOR_DEPTH_VALUE = "8bit";
    private static final String ENABLE_COLOR_DEPTH_VALUE = "12bit";
    private static final String ACTION_ON = "on";
    private static final String ACTION_OFF = "off";

    private ArrayList<String> curValueList = new ArrayList();
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hpdFlag = intent.getBooleanExtra ("state", false);
            mHandler.sendEmptyMessageDelayed(MSG_FRESH_UI, hpdFlag ^ isHdmiMode() ? 2000 : 1000);
        }
    };
    public static ColorDepthFragment newInstance() {
        return new ColorDepthFragment();
    }
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mOutputUiManager = new OutputUiManager(getActivity());
        updatePreferenceFragment();
    }
    private boolean needfresh() {
        ArrayList<String> list = mOutputUiManager.getColorValueList();
        //Log.d(LOG_TAG, "curValueList: " + curValueList.toString() + "\n list: " + list.toString());
        if (curValueList.size() > 0 && curValueList.size() == list.size()) {
            for (String title : curValueList) {
                if (!list.contains(title))
                    return true;
            }
        }else {
            return true;
        }
        return false;
    }
    private void updatePreferenceFragment() {
        mOutputUiManager.updateUiMode();
        if (!needfresh()) return;
        final Context themedContext = getPreferenceManager().getContext();
        final PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(
                themedContext);
        screen.setTitle(R.string.device_outputmode_color_depth);
        setPreferenceScreen(screen);
        if (!isHdmiMode()) {
            curValueList.clear();
            return;
        }
        final List<Action> InfoList = getMainActions();
        for (final Action Info : InfoList) {
            final String InfoTag = Info.getKey();
            final RadioPreference radioPreference = new RadioPreference(themedContext);
            radioPreference.setKey(InfoTag);
            radioPreference.setPersistent(false);
            radioPreference.setTitle(Info.getTitle());
            radioPreference.setLayoutResource(R.layout.preference_reversed_widget);
            if (Info.isChecked()) {
                radioPreference.setChecked(true);
                prePreference = curPreference = radioPreference;
            }
            screen.addPreference(radioPreference);
        }
    }
    private boolean isModeSupportColor(final String curMode, final String curValue){
        boolean  ret = false;
        ret = mOutputUiManager.isModeSupportColor(curMode, curValue);
        return ret;
    }

    private ArrayList<Action> getMainActions() {
        ArrayList<Action> actions = new ArrayList<Action>();
        curValueList.clear();
        ArrayList<String> mList = mOutputUiManager.getColorValueList();
        for (String color:mList) {
            curValueList.add(color);
        }
        ArrayList<String> colorValueList = mOutputUiManager.getColorValueList();
        String value = null;
        String filterValue = null;
        String  curColorDepthValue = mOutputUiManager.getCurrentColorAttribute().trim();
        Log.i(LOG_TAG,"curColorDepthValue: "+curColorDepthValue);
        if (curColorDepthValue.equals("default"))
            curColorDepthValue = DEFAULT_COLOR_DEPTH_VALUE;

        for (int i = 0; i < curValueList.size(); i++) {
            value = colorValueList.get(i).trim();
            curMode = mOutputUiManager.getCurrentMode();
            if (!isModeSupportColor(curMode, value)) {
                continue;
            }
            filterValue += value;
        }

        if (filterValue != null) {
            if (filterValue.contains(mOutputUiManager.getCurrentColorSpaceAttr().trim() + "," + ENABLE_COLOR_DEPTH_VALUE)) {
                actions.add(new Action.Builder().key(ACTION_ON)
                        .title("        " + getString(R.string.on))
                        .checked(curColorDepthValue.contains(ENABLE_COLOR_DEPTH_VALUE) ? true : false)
                        .description("")
                        .build());
            }
        }

        actions.add(new Action.Builder().key(ACTION_OFF)
            .title("        " + getString(R.string.off))
            .checked(curColorDepthValue.contains(DEFAULT_COLOR_DEPTH_VALUE) ? true : false)
            .description("")
            .build());

        return actions;
    }
    @Override
    public void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter("android.intent.action.HDMI_PLUGGED");
        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mIntentReceiver);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference instanceof RadioPreference) {
            final RadioPreference radioPreference = (RadioPreference) preference;
            radioPreference.clearOtherRadioPreferences(getPreferenceScreen());
            if (radioPreference.isChecked()) {
                curPreference = radioPreference;
                if (onClickHandle(curPreference.getKey()) == true) {
                    curPreference.setChecked(true);
                }
            } else {
                radioPreference.setChecked(true);
                Log.i(LOG_TAG,"not checked");
            }
        }
      return super.onPreferenceTreeClick(preference);
    }
    public boolean onClickHandle(String key) {
        curValue = key.contains(ACTION_ON) ? ENABLE_COLOR_DEPTH_VALUE : DEFAULT_COLOR_DEPTH_VALUE;
        saveValue= mOutputUiManager.getCurrentColorDepthAttr().trim();

        preColor = mOutputUiManager.getCurrentColorAttribute();
        if (saveValue.equals("default"))
            saveValue = DEFAULT_COLOR_DEPTH_VALUE;
        curMode = mOutputUiManager.getCurrentMode();
        Log.i(LOG_TAG,"Set Color Depth Value: "+curValue + "CurValue: "+saveValue);

        if (!curValue.equals(saveValue)) {
            curValue = mOutputUiManager.getCurrentColorSpaceAttr().trim() + "," + curValue;
            if (isModeSupportColor(curMode,curValue)) {
               mOutputUiManager.changeColorAttribte(curValue);
               curColor = curValue;
               showDialog();
               return true;
           }
        }
        return false;
    }

    private void showDialog() {
        if (mAlertDialog == null) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view_dialog = inflater.inflate(R.layout.dialog_outputmode, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);

            tx_title = (TextView) view_dialog.findViewById(R.id.dialog_title);
            tx_content = (TextView) view_dialog.findViewById(R.id.dialog_content);

            TextView button_cancel = (TextView)view_dialog.findViewById(R.id.dialog_cancel);
            TextView button_ok = (TextView)view_dialog.findViewById(R.id.dialog_ok);
            button_cancel.setOnClickListener(this);
            button_ok.setOnClickListener(this);
        }
        mAlertDialog.show();
        mAlertDialog.getWindow().setContentView(view_dialog);
        mAlertDialog.setCancelable(false);

        tx_content.setText(getResources().getString(R.string.device_colorattribute_change)
        + " " + curValue);
        countdown = 10;
        if (timer == null)
            timer = new Timer();
        if (task != null)
            task.cancel();
        task = new DialogTimerTask();
        timer.schedule(task, 0, 1000);
    }

    private void recoverColorAttribute() {
        curPreference = prePreference;
        curColor = preColor;
        mHandler.sendEmptyMessage(MSG_RESET_COLOR);
        mOutputUiManager.changeColorAttribte(preColor);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_cancel:
                if (mAlertDialog != null)
                    mAlertDialog.dismiss();
                recoverColorAttribute();
                break;
            case R.id.dialog_ok:
                if (mAlertDialog != null)
                    mAlertDialog.dismiss();
                prePreference = curPreference;
                break;
        }
        task.cancel();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_FRESH_UI:
                    updatePreferenceFragment();
                    break;
                case MSG_COUNT_DOWN:
                    tx_title.setText(Integer.toString(countdown) + " " + getResources().getString(R.string.device_countdown));
                    if (countdown == 0) {
                        if(mAlertDialog != null) {
                            recoverColorAttribute();
                            mAlertDialog.dismiss();
                        }
                        task.cancel();
                    }
                    countdown--;
                    break;
                case MSG_RESET_COLOR:
                    curPreference.clearOtherRadioPreferences(getPreferenceScreen());
                    curPreference.setChecked(true);
                    break;
            }
        }
    };
    private boolean isHdmiMode() {
        return mOutputUiManager.isHdmiMode();
    }

    private class DialogTimerTask extends TimerTask {
        @Override
        public void run() {
            if (mHandler != null) {
                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
            }
        }
    }
}
