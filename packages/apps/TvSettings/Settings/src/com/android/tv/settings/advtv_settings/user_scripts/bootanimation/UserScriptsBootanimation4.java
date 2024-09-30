package com.android.tv.settings.advtv_settings.user_scripts.bootanimation;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import com.android.tv.settings.R;
import com.android.tv.settings.SettingsPreferenceFragment;
import com.android.tv.settings.advtv_settings.user_scripts.ShellUtils;

/* loaded from: classes.dex */
public class UserScriptsBootanimation4 extends SettingsPreferenceFragment {
    private Preference mRunNow;

    public static UserScriptsBootanimation4 newInstance() {
        return new UserScriptsBootanimation4();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1;
    }

    @Override // com.android.tv.settings.SettingsPreferenceFragment, android.support.v14.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // android.support.v14.preference.PreferenceFragment
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

    @Override // android.support.v14.preference.PreferenceFragment, android.support.v7.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        ShellUtils.execCommand("mount -o remount,rw /", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("mount -o remount,rw /vendor", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("unzip /system/media/tv_script/bootanimation bootanimation_4.zip -o -q -d /system/media/", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("mv -f /system/media/bootanimation_4.zip /system/media/bootanimation.zip", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("chmod -R a-wx,u+w,a+rX /system/media/bootanimation.zip", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/service.bootvideo=1/service.bootvideo=0/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("sed -i 's/persist.vendor.media.bootvideo=3050/persist.vendor.media.bootvideo=0050/' /vendor/build.prop", true);
        super.onPreferenceTreeClick(preference);
        ShellUtils.execCommand("am start -n slim.box.tv.project/slim.box.tv.reboot.MainActivity", true);
        return super.onPreferenceTreeClick(preference);
    }

    @Override // com.android.tv.settings.SettingsPreferenceFragment, android.app.Fragment
    public void onResume() {
        super.onResume();
    }
}
