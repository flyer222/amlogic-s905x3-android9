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
import android.text.Layout;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Keep
public class ColorAttributeFragment extends LeanbackAddBackPreferenceFragment
implements OnClickListener{
    private static final String LOG_TAG = "ColorAttributeFragment";
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
    private static final String DEFAULT_VALUE = "444";
    private static final String DEFAULT_TITLE = "YCbCr444";
    private static final String DEFAULT_COLOR_DEPTH_VALUE = "8bit";
    private ArrayList<String> colorTitleList = new ArrayList();

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            hpdFlag = intent.getBooleanExtra ("state", false);
            mHandler.sendEmptyMessageDelayed(MSG_FRESH_UI, hpdFlag ^ isHdmiMode() ? 2000 : 1000);
        }
    };
    public static ColorAttributeFragment newInstance() {
        return new ColorAttributeFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        mOutputUiManager = new OutputUiManager(getActivity());
        mIntentFilter = new IntentFilter("android.intent.action.HDMI_PLUGGED");
        mIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        updatePreferenceFragment();
    }
    private boolean needfresh() {
        ArrayList<String> list = mOutputUiManager.getColorTitleList();
        //Log.d(LOG_TAG, "colorTitleList: " + colorTitleList.toString() + "\n list: " + list.toString());
        if (colorTitleList.size() > 0 && colorTitleList.size() == list.size()) {
            for (String title : colorTitleList) {
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
        screen.setTitle(R.string.device_outputmode_color_space);
        setPreferenceScreen(screen);
        if (!isHdmiMode()) {
            colorTitleList.clear();
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
        colorTitleList.clear();
        ArrayList<String> mList = mOutputUiManager.getColorTitleList();
        for (String color:mList) {
            colorTitleList.add(color);
        }
        ArrayList<String> colorValueList = mOutputUiManager.getColorValueList();
        String value;
        String filterValue = null;
        String  curColorSpaceValue = mOutputUiManager.getCurrentColorSpaceAttr();
        Log.i(LOG_TAG,"curColorSpaceValue: "+curColorSpaceValue);
        if (curColorSpaceValue.equals("default"))
            curColorSpaceValue = DEFAULT_VALUE;
        for (int i = 0; i < colorTitleList.size(); i++) {
            value = colorValueList.get(i).trim();
            curMode = mOutputUiManager.getCurrentMode();
            if (!isModeSupportColor(curMode, value)) {
                continue;
            }
            filterValue += value;
        }
        if (filterValue != null) {
            for (int i = 0; i < OutputUiManager.COLOR_SPACE_LIST.length; i++) {
                if (filterValue.contains(OutputUiManager.COLOR_SPACE_LIST[i])) {
                    if (curColorSpaceValue.contains(OutputUiManager.COLOR_SPACE_LIST[i])) {
                        actions.add(new Action.Builder().key(OutputUiManager.COLOR_SPACE_LIST[i])
                                .title("        " + OutputUiManager.COLOR_SPACE_TITLE[i])
                                .checked(true).build());
                    } else {
                        actions.add(new Action.Builder().key(OutputUiManager.COLOR_SPACE_LIST[i])
                                .title("        " + OutputUiManager.COLOR_SPACE_TITLE[i])
                                .description("").build());
                    }
                }
            }
        }
        if (actions.size() == 0) {
            actions.add(new Action.Builder().key(DEFAULT_VALUE)
                .title("        " + DEFAULT_TITLE)
                .checked(true).build());
        }
        return actions;
    }
    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mIntentReceiver, mIntentFilter);
        mHandler.sendEmptyMessage(MSG_FRESH_UI);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mIntentReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        curValue = key;
        saveValue= mOutputUiManager.getCurrentColorSpaceAttr().trim();

        preColor = mOutputUiManager.getCurrentColorAttribute();
        if (saveValue.equals("default"))
            saveValue = DEFAULT_VALUE;
        curMode = mOutputUiManager.getCurrentMode();
        Log.i(LOG_TAG,"Set Color Space Value: "+curValue + "CurValue: "+saveValue);
        if (!curValue.equals(saveValue)) {
            curValue = curValue + "," + mOutputUiManager.getCurrentColorDepthAttr().trim();
            if (isModeSupportColor(curMode,curValue)) {
               mOutputUiManager.changeColorAttribte(curValue);
            } else {
                curValue = key + "," + DEFAULT_COLOR_DEPTH_VALUE;
                mOutputUiManager.changeColorAttribte(curValue);
            }
            curColor = curValue;
            showDialog();
            return true;
        }
        return false;
    }

    private void showDialog() {
        if (mAlertDialog == null) {
            LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            view_dialog = inflater.inflate(R.layout.dialog_outputmode, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            mAlertDialog = builder.create();
            mAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);

            tx_title = (TextView)view_dialog.findViewById(R.id.dialog_title);
            tx_content = (TextView)view_dialog.findViewById(R.id.dialog_content);

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
                        if (mAlertDialog != null) {
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
