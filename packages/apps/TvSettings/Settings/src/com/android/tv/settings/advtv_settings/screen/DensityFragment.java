package com.android.tv.settings.advtv_settings.screen;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.support.v17.preference.LeanbackPreferenceFragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.view.IWindowManager;
import com.android.tv.settings.R;
import com.android.tv.settings.RadioPreference;
import com.android.tv.settings.dialog.old.Action;
import java.util.ArrayList;


public class DensityFragment extends LeanbackPreferenceFragment {
    private static final String ACTION_DENSITY_120 = "120";
    private static final String ACTION_DENSITY_160 = "160";
    private static final String ACTION_DENSITY_200 = "200";
    private static final String ACTION_DENSITY_240 = "240";
    private static final String ACTION_DENSITY_280 = "280";
    private static final String ACTION_DENSITY_300 = "300";
    private static final String ACTION_DENSITY_320 = "320";
    private static final int DEFAULT_DISPLAY = 0;
    private static final String DENSITY_RADIO_GROUP = "density";
    private static final String TAG = "DensityFragment";
    private static int density;
    private Context mContext;
    protected IWindowManager mWm;

    private ArrayList<Action> getActions() {
        ArrayList<Action> arrayList = new ArrayList<>();
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_120).title(getString(R.string.screen_density_120)).checked(density == 120).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_160).title(getString(R.string.screen_density_160)).checked(density == 160).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_200).title(getString(R.string.screen_density_200)).checked(density == 200).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_240).title(getString(R.string.screen_density_240)).checked(density == 240).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_280).title(getString(R.string.screen_density_280)).checked(density == 280).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_300).title(getString(R.string.screen_density_300)).checked(density == 300).build());
        arrayList.add(new Action.Builder().key(ACTION_DENSITY_320).title(getString(R.string.screen_density_320)).checked(density == 320).build());
        return arrayList;
    }

    private int getDensity() {
        try {
            int initialDisplayDensity = this.mWm.getInitialDisplayDensity(0);
            int baseDisplayDensity = this.mWm.getBaseDisplayDensity(0);
            return initialDisplayDensity != baseDisplayDensity ? baseDisplayDensity : initialDisplayDensity;
        } catch (RemoteException e) {
            return 0;
        }
    }

    public static DensityFragment newInstance() {
        return new DensityFragment();
    }

    private void setDensity(int i) {
        try {
            this.mWm.setForcedDisplayDensityForUser(0, i, -2);
        } catch (RemoteException e) {
        }
    }

    @Override  
    public void onCreatePreferences(Bundle bundle, String str) {
        this.mWm = IWindowManager.Stub.asInterface(ServiceManager.checkService("window"));
        density = getDensity();
        this.mContext = getPreferenceManager().getContext();
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(this.mContext);
        createPreferenceScreen.setTitle(R.string.screen_density);
        Preference preference = null;
        for (Action action : getActions()) {
            String key = action.getKey();
            RadioPreference radioPreference = new RadioPreference(this.mContext);
            radioPreference.setKey(key);
            radioPreference.setPersistent(false);
            radioPreference.setTitle(action.getTitle());
            radioPreference.setRadioGroup(DENSITY_RADIO_GROUP);
            radioPreference.setLayoutResource(R.layout.preference_reversed_widget);
            if (action.isChecked()) {
                radioPreference.setChecked(true);
                preference = radioPreference;
            }
            createPreferenceScreen.addPreference(radioPreference);
        }
        if (preference != null && bundle == null) {
            scrollToPreference(preference);
        }
        setPreferenceScreen(createPreferenceScreen);
    }
   
    @Override 
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!(preference instanceof RadioPreference)) {
            return super.onPreferenceTreeClick(preference);
        }
        RadioPreference radioPreference = (RadioPreference) preference;
        radioPreference.clearOtherRadioPreferences(getPreferenceScreen());
        if (radioPreference.isChecked()) {
            String str = radioPreference.getKey().toString();
            if (str.equals(ACTION_DENSITY_120)) {
                setDensity(android.support.v7.appcompat.R.styleable.AppCompatTheme_windowNoTitle);
            }
            if (str.equals(ACTION_DENSITY_160)) {
                setDensity(160);
            }
            if (str.equals(ACTION_DENSITY_200)) {
                setDensity(200);
            }
            if (str.equals(ACTION_DENSITY_240)) {
                setDensity(240);
            }
            if (str.equals(ACTION_DENSITY_280)) {
                setDensity(280);
            }
            if (str.equals(ACTION_DENSITY_300)) {
                setDensity(300);
            }
            if (str.equals(ACTION_DENSITY_320)) {
                setDensity(320);
            }
        }
        return super.onPreferenceTreeClick(preference);
    }

}
