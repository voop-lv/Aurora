package com.zenya.aurora.util.object;

import com.zenya.aurora.storage.StorageFileManager;
import lombok.Getter;

public class TimeCheck {
    private static final long MIN_TIME = StorageFileManager.getConfig().getInt("start-spawning-at");
    private static final long MAX_TIME = StorageFileManager.getConfig().getInt("stop-spawning-at");
    @Getter private long relTime;

    public TimeCheck(Long time) {
        this.relTime = time % 23000;
    }

    public boolean isDuring() {
        if(MAX_TIME > MIN_TIME) {
            return (relTime > MIN_TIME && relTime < MAX_TIME);
        } else if(MAX_TIME < MIN_TIME){
            return (relTime > MIN_TIME || relTime < MAX_TIME);
        } else {
            return relTime == MIN_TIME;
        }
    }
}
