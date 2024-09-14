package com.android.tv.settings.display.outputmode;

import com.android.tv.settings.ConfigEnv;
import android.os.SystemProperties;

public class AutoFramerateManager {
    private static AutoFramerateManager manager;
    private AutoFramerateManager() {}

    public static AutoFramerateManager getManager() {
        if (manager == null)
            manager = new AutoFramerateManager();
        return manager;
    }

    public static void onReceived() {
        manager = new AutoFramerateManager();
        manager.autoApply();
    }

    private void autoApply() {
        if (canUseIt()) {
            tryOnOff();
        }
    }

    public boolean canUseIt() {
        if (ConfigEnv.getDisplayAutodetect()
                || ConfigEnv.getHdmiMode().equals("custombuilt"))
            return false;
        return true;
    }

    public boolean isActive() {
        return ConfigEnv.getAutoFramerateState();
    }

    private void tryOnOff() {
        if (isActive()) {
            SystemProperties.set("ctl.start", "afrd");
        } else {
            SystemProperties.set("ctl.stop", "afrd");
        }
    }

    public void start() {
        ConfigEnv.setAutoFramerate(true);
        SystemProperties.set("ctl.start", "afrd");
    }

    public void stop() {
        ConfigEnv.setAutoFramerate(false);
        SystemProperties.set("ctl.stop", "afrd");
    }
}
