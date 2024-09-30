package com.android.tv.settings.advtv_settings.user_scripts.logo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;


public class UserScriptsLogo8 extends SettingsPreferenceFragment {
    private Preference mRunNow;

    public static UserScriptsLogo8 newInstance() {
        return new UserScriptsLogo8();
    }

    public int getMetricsCategory() {
        return 1;
    }

    public void onCreatePreferences(Bundle bundle, String str) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        createPreferenceScreen.setTitle(R.string.logo_chooser);
        getString(R.string.logo_chooser);
        this.mRunNow = new Preference(context);
        this.mRunNow.setKey("run_now");
        Preference preference = this.mRunNow;
        preference.setTitle(getString(R.string.user_scripts_activation));
        createPreferenceScreen.addPreference(preference);
        createPreferenceScreen.addPreference(this.mRunNow);
        setPreferenceScreen(createPreferenceScreen);
    }

    public boolean onPreferenceTreeClick(Preference preference) {
        ShellUtils.execCommand("mount -o remount,rw /", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("unzip /system/media/tv_script/logo logo_8.img -o -q -d /data/media/0/", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("dd if=/data/media/0/logo_8.img of=/dev/block/logo", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("rm -rf /data/media/0/logo_8.img", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
        return super.onPreferenceTreeClick(preference);
    }

}
