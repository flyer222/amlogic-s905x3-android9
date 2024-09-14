package com.android.tv.settings.advtv_settings.user_scripts.bootvideo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;
import android.util.Log;


public class UserScriptsBootvideo8 extends SettingsPreferenceFragment {
	private String TAG = "UserScriptsBootvideo";
    private Preference mRunNow;

    public static UserScriptsBootvideo8 newInstance() {
        return new UserScriptsBootvideo8();
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
		com.android.tv.settings.advtv_settings.user_scripts.ShellUtils.CommandResult rst;
        rst = ShellUtils.execCommand("mount -o remount,rw /", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("mount -o remount,rw /vendor", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("unzip /system/media/tv_script/bootvideo bootvideo_8 -o -q -d /vendor/etc/", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("mv -f /vendor/etc/bootvideo_8 /vendor/etc/bootvideo", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("chmod -R a-wx,u+w,a+rX /vendor/etc/bootvideo", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("sed -i 's/service.bootvideo=0/service.bootvideo=1/' /vendor/build.prop", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("sed -i 's/persist.vendor.media.bootvideo=0050/persist.vendor.media.bootvideo=3050/' /vendor/build.prop", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
		Log.e(TAG,rst.toString());
        return super.onPreferenceTreeClick(preference);
    }

}
