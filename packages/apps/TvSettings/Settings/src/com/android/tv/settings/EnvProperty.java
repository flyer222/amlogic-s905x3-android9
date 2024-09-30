package com.android.tv.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import android.os.SystemProperties;

public class EnvProperty {
    private static final String PROP_FILE = "/odm/default.prop";

    public static int getInt(String key, int defaultValue) {
        return SystemProperties.getInt(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return SystemProperties.getBoolean(key, defaultValue);
    }

    public static String get (String key) {
        return SystemProperties.get(key);
    }

    public static String get (String key, String defaultValue) {
        return SystemProperties.get(key, defaultValue);
    }

    public static String getFromFile(String key) {
        try {
            InputStream inputStream = new FileInputStream(PROP_FILE);
            Properties properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty(key);
        } catch (Exception e) {}
        return "";
    }

    public static boolean set (String key, String value) {
        SystemProperties.set(key, value);

        return true;
    }

    public static boolean save (String key, String value, String comment) {
        Properties properties = new Properties();
        File propertyFile = new File(PROP_FILE);
        try {
            FileInputStream inStream = new FileInputStream(propertyFile);
            properties.load(inStream);
            properties.setProperty(key, value);
            FileOutputStream outStream = new FileOutputStream(propertyFile);
            properties.store(outStream, comment);
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setAndSave(String key, Boolean value, String comment) {
        if (!set(key, value? "true": "false"))
            return  false;

        if (!save(key, value? "true": "false", comment))
            return false;

        return true;
    }

    public static boolean setAndSave(String key, String value, String comment) {
        if (!set(key, value))
            return  false;

        if (!save(key, value, comment))
            return false;

        return true;
    }
}
