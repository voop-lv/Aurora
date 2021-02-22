package com.zenya.aurora.storage;

import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;

import java.util.HashMap;

public class TaskManager {
    public static final TaskManager INSTANCE = new TaskManager();
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager() {
        registerTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.INSTANCE);
        registerTask(TaskKey.TRACK_LOCATION_TASK, TrackLocationTask.INSTANCE);
    }

    public AuroraTask getTask(TaskKey key) {
        return taskMap.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTask(TaskKey key) {
        taskMap.remove(key);
    }
}


