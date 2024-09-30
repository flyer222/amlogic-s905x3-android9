package com.android.tv.settings;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import com.droidlogic.app.SdrManager;

public class ConfigEnv {
    private final static String TAG = "ConfigEnv";
    private final static String path = "/odm/env.ini";

    public static String getBigCoreClock() {
        return getValue("max_freq_big") + "000";
    }

    public static String getLittleCoreClock() {
        return getValue("max_freq_little") + "000";
    }

    public static String getBigCoreGovernor() {
        return getValue("governor_big");
    }

    public static String getLittleCoreGovernor() {
        return getValue("governor_little");
    }

    /** Not used,
     *  It works on systemcontrol. read from /proc/cmdline
     */
    public static String getHdmiMode() {
        return getValue("hdmimode");
    }

    public static String getVoutMode() {
        return getValue("voutmode");
    }

    public static boolean getDisplayAutodetect() {
        return getValue("display_autodetect").equals("true");
    }

    public static int getDisplayZoomrate() {
        return Integer.parseInt(getValue("zoom_rate"));
    }

    public static int getWakeOnLan() {
        return Integer.parseInt(getValue("enable_wol"));
    }

    public static String getColorAttribute() {
        return getValue("colorattribute");
    }

    public static String getHeartBeat() {
        return getValue("heartbeat");
    }

    public static String getAdjustScreenWay() {

        String value = getValue("adjustScreenWay");
        if (value == null) {
            setAdjustScreenWay("zoom");
            return "zoom";
        }
        return value;
    }

    public static int[] getScreenAlignment() {
        String align[];
        String alignment;

        try {
            alignment = getValue("screenAlignment");
            align = alignment.split(" ");
            int[] result = {
                    Integer.valueOf(align[0]), // left
                    Integer.valueOf(align[1]), // top
                    Integer.valueOf(align[2]), // right
                    Integer.valueOf(align[3]) // bottom
            };
            return result;
        } catch (Exception e) {
            Log.e(TAG, "env.ini doesn't have screenAlignment option");
            setScreenAlignment(0, 0, 0, 0);
        }
        return new int[]{0, 0, 0, 0};
    }

    public static boolean getAutoFramerateState() {
        try {
            if (getValue("autoFramerate").equals("true"))
                return true;
            else
                return false;
        } catch (Exception e) {
            Log.e(TAG, "env.ini doesn't have autoFramerate option");
            setAutoFramerate(false);
        }
        return false;
    }

    public static String getGpuScaleMode() {
        String mode = getValue("gpuScaleMode");

        if (mode == null) {
            mode = "2";
            setGpuScaleMode(mode);
        }
        return mode;
    }

    public static String getOverlay() {
        String overlays = getValue("overlays");

        if (overlays == null) {
            overlays = "";
            setOverlay(overlays);
        }

        return overlays;
    }

    public static String getOverlaySize() {
        String size = getValue("overlays_resize");

        if (size == null) {
            size = "16384";
            setOverlaySize(size);
        }

        return size;
    }

    public static int getSdr2Hdr() {
        String value = getValue("hdr_policy");
        if (value == null) {
            value = "1";
            setSdr2Hdr(SdrManager.HDR_POLICY_HDR_OFF);
        }
        return Integer.parseInt(value);
    }

    private static String getValue(String keyWord) {
        return _getValue(keyWord + "=");
    }

    private static String _getValue(String startTerm) {
        File boot_ini = new File(path);
        if (boot_ini.exists()) {
            try {
                String line;
                FileReader fileReader = new FileReader(boot_ini);
                BufferedReader reader = new BufferedReader(fileReader);
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(startTerm)) {
                        String value = line.split("=")[1];

                        if (value.startsWith("\"")) {
                            return value.split("\"")[1];
                        } else
                            return value;
                    }
                }
                reader.close();
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static void setBigCoreFreq(String freq) {
        setValue("max_freq_big", freq.substring(0, freq.length()-3));
    }

    public static void setLittleCoreFreq(String freq) {
        setValue("max_freq_little", freq.substring(0, freq.length()-3));
    }

    public static void setBigCoreGovernor(String governor) {
        setValue("governor_big", governor);
    }

    public static void setLittleCoreGovernor(String governor) {
        setValue("governor_little", governor);
    }

    public static void setHdmiMode(String mode) {
        if (mode.equals("autodetect"))
            setDisplayAutodetect("true");
        else
            setDisplayAutodetect("false");
        setDisableHPD(mode.startsWith("ODROID-VU"));
        mode = convertVUResolution(mode);
        setValue("hdmimode", mode);
    }

    private static void setDisplayAutodetect(String mode) {
        setValue("display_autodetect", mode);
    }

    private static String convertVUResolution(String mode) {
        if (mode.startsWith("ODROID-VU5")) //VU5, VU5A, VU7
            return "800x480p60hz";
        else if (mode.startsWith("ODROID-VU7")) // VU7+, VU7A+
            return  "1024x600p60hz";
        else if (mode.startsWith("ODROID-VU8"))
            return "1024x768p60hz";
        return mode;
    }

    public static void setVoutMode(String mode) {
        setValue("voutmode", mode);
    }

    public static void setDisplayZoom(int rate) {
        setValue("zoom_rate", String.valueOf(rate));
    }

    public static void setWakeOnLan(int on) {
        setValue("enable_wol", String.valueOf(on));
    }

    public static void setColorAttribute(String color) {
        setValue("colorattribute", color);
    }

    public static void setHeartBeat(String mode) {
        setValue("heartbeat", mode);
    }

    public static void setAdjustScreenWay(String way) {
        setValue("adjustScreenWay", way);
    }

    public static void setScreenAlignment(int left, int top, int right, int bottom) {
        String alignment = "" + left + " " + top + " " + right + " " + bottom;
        setValue("screenAlignment", alignment);
    }

    public static void setAutoFramerate(boolean state) {
        setValue("autoFramerate", state?"true":"false");
    }

    public static void setGpuScaleMode(String mode) {
        setValue("gpuScaleMode", mode);
    }

    public static void setOverlay(String overlay) {
        setValue("overlays", overlay);
    }

    public static void setOverlaySize(String size) {
        setValue("overlays_resize", Integer.valueOf(size));
    }

    public static void setDisableHPD(boolean state) {
        setValue("disablehpd", state?"true":"false");
    }

    public static void setSdr2Hdr(int mode) {
        setValue("hdr_policy", mode);
    }

    private static void setValue (String keyWord, String val) {
        _setValue(keyWord + "=", val);
    }

    private static void _setValue (String startTerm, String val) {
        boolean isSet = false;
        try {
            File boot_ini = new File(path);
            FileReader fileReader = new FileReader(boot_ini);
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(startTerm)) {
                    line = startTerm + "\"" + val + "\"";
                    isSet = true;
                }
                lines.add(line + "\n");
            }

            if (isSet == false) {
                line = startTerm + "\"" + val + "\"";
                lines.add(line + "\n");
            }

            fileReader.close();
            reader.close();

            FileWriter fileWriter = new FileWriter(boot_ini);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (String newline : lines)
                writer.write(newline);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setValue (String keyWord, int val) {
        _setValue(keyWord + "=", val);
    }

    private static void _setValue (String startTerm, int val) {
        boolean isSet = false;
        try {
            File boot_ini = new File(path);
            FileReader fileReader = new FileReader(boot_ini);
            BufferedReader reader = new BufferedReader(fileReader);

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(startTerm)) {
                    line = startTerm + val;
                    isSet = true;
                }
                lines.add(line + "\n");
            }

            if (isSet == false) {
                line = startTerm + val;
                lines.add(line + "\n");
            }

            fileReader.close();
            reader.close();

            FileWriter fileWriter = new FileWriter(boot_ini);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (String newline : lines)
                writer.write(newline);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
