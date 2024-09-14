package com.android.tv.settings.advtv_settings.user_scripts.bootanimation;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;
import android.util.Log;


public class UserScriptsBootanimation1 extends SettingsPreferenceFragment {
	private static String TAG = "UserScriptsBootanimation1";
    private Preference mRunNow;

    public static UserScriptsBootanimation1 newInstance() {
        return new UserScriptsBootanimation1();
    }

  
    public int getMetricsCategory() {
        return 1;
    }


    public void onCreatePreferences(Bundle bundle, String str) {
        Context context = getPreferenceManager().getContext();
        PreferenceScreen createPreferenceScreen = getPreferenceManager().createPreferenceScreen(context);
        createPreferenceScreen.setTitle(R.string.bootanimation_chooser);
        getString(R.string.bootanimation_chooser);
        this.mRunNow = new Preference(context);
        this.mRunNow.setKey("run_now");
        Preference preference = this.mRunNow;
        preference.setTitle(getString(R.string.user_scripts_activation));
        createPreferenceScreen.addPreference(preference);
        createPreferenceScreen.addPreference(this.mRunNow);
        setPreferenceScreen(createPreferenceScreen);
    }

	public boolean onPreferenceTreeClick(Preference preference) {
		com.android.tv.settings.advtv_settings.user_scripts.ShellUtils.CommandResult  rst; 
        rst = ShellUtils.execCommand("mount -o remount,rw /", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("mount -o remount,rw /vendor", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("unzip /system/media/tv_script/bootanimation bootanimation_1.zip -o -q -d /system/media/", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("mv -f /system/media/bootanimation_1.zip /system/media/bootanimation.zip", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("chmod -R a-wx,u+w,a+rX /system/media/bootanimation.zip", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("sed -i 's/service.bootvideo=1/service.bootvideo=0/' /vendor/build.prop", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("sed -i 's/persist.vendor.media.bootvideo=3050/persist.vendor.media.bootvideo=0050/' /vendor/build.prop", true);
		Log.e(TAG,rst.toString());
        super.onPreferenceTreeClick(preference);
        rst = ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
		Log.e(TAG,rst.toString());
        return super.onPreferenceTreeClick(preference);
    }
}
