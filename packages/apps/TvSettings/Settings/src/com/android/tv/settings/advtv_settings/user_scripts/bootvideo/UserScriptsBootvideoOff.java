package com.android.tv.settings.advtv_settings.user_scripts.bootvideo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;


public class UserScriptsBootvideoOff extends SettingsPreferenceFragment {
    private Preference mRunNow;

    public static UserScriptsBootvideoOff newInstance() {
        return new UserScriptsBootvideoOff();
    }

    public int getMetricsCategory() {
        return 1;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        createPreferenceScreen.setTitle(R.string.bootvideo_chooser);
        getString(R.string.bootvideo_chooser);
        this.mRunNow = new Preference(context);
        this.mRunNow.setKey("run_now");
        Preference preference = this.mRunNow;
        preference.setTitle(getString(R.string.user_scripts_disable_all));
        createPreferenceScreen.addPreference(preference);
        createPreferenceScreen.addPreference(this.mRunNow);
        setPreferenceScreen(createPreferenceScreen);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ShellUtils.execCommand("mount -o remount,rw /vendor", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/service.bootvideo=1/service.bootvideo=0/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/persist.vendor.media.bootvideo=3050/persist.vendor.media.bootvideo=0050/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
        return super.onPreferenceTreeClick(preference);
    }

}
