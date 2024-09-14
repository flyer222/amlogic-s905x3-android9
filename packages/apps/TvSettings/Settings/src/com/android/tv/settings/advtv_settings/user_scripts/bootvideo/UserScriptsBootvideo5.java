package com.android.tv.settings.advtv_settings.user_scripts.bootvideo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;


public class UserScriptsBootvideo5 extends SettingsPreferenceFragment {
    private Preference mRunNow;

    public static UserScriptsBootvideo5 newInstance() {
        return new UserScriptsBootvideo5();
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
        preference.setTitle(getString(R.string.user_scripts_activation));
        createPreferenceScreen.addPreference(preference);
        createPreferenceScreen.addPreference(this.mRunNow);
        setPreferenceScreen(createPreferenceScreen);
    }


    public boolean onPreferenceTreeClick(Preference preference) {
        ShellUtils.execCommand("mount -o remount,rw /", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("mount -o remount,rw /vendor", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("unzip /system/media/tv_script/bootvideo bootvideo_5 -o -q -d /vendor/etc/", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("mv -f /vendor/etc/bootvideo_5 /vendor/etc/bootvideo", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("chmod -R a-wx,u+w,a+rX /vendor/etc/bootvideo", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/service.bootvideo=0/service.bootvideo=1/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/persist.vendor.media.bootvideo=0050/persist.vendor.media.bootvideo=3050/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
        return super.onPreferenceTreeClick(preference);
    }
}
