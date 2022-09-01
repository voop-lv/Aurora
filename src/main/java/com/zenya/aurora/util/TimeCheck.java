package com.zenya.aurora.util;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.YAMLFile;
import com.zenya.aurora.storage.StorageFileManager;

public class TimeCheck {

    private static final long MIN_TIME;
    private static final long MAX_TIME;
    static {
        final YAMLFile yamlFile = Aurora.getPlugin(Aurora.class).getStorageFileManager().getConfig();
        MIN_TIME = yamlFile.getInt("start-spawning-at");
        MAX_TIME = yamlFile.getInt("stop-spawning-at");
    }

    public static long getRelTime(Long time) {
        return time % 24000;
    }

    public static boolean isDuring(Long time) {
        long relTime = getRelTime(time);

        if (MAX_TIME > MIN_TIME) {
            return (relTime > MIN_TIME && relTime < MAX_TIME);
        } else if (MAX_TIME < MIN_TIME) {
            return (relTime > MIN_TIME || relTime < MAX_TIME);
        } else {
            return relTime == MIN_TIME;
        }
    }
}
