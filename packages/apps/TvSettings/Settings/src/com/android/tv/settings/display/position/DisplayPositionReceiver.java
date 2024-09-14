package com.android.tv.settings.display.position;

import android.content.Context;

import com.droidlogic.app.DisplayPositionManager;
import com.android.tv.settings.ConfigEnv;

public class DisplayPositionReceiver {
    private final String TAG = "DisplayPositionReceiver";

    public static void onReceive(Context context) {
        DisplayPositionManager mDisplayPositionManager;
        mDisplayPositionManager = new DisplayPositionManager (context);

        if (ConfigEnv.getAdjustScreenWay().equals("zoom"))
            mDisplayPositionManager.zoomByPercent(ConfigEnv.getDisplayZoomrate());
        else {
            int[] alignment = ConfigEnv.getScreenAlignment();
            mDisplayPositionManager.alignBy(alignment[0], alignment[1],
                    alignment[2], alignment[3]);
        }
    }
}
