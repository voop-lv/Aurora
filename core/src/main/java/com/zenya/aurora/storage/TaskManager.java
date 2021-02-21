package com.zenya.aurora.storage;

import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackPlayerTask;
import com.zenya.aurora.scheduler.TrackTPSTask;

import java.util.HashMap;

public class TaskManager {
    private static TaskManager taskManager;
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager() {
        registerTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.getInstance());
        registerTask(TaskKey.TRACK_PLAYER_TASK, TrackPlayerTask.getInstance());
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

    public static TaskManager getInstance() {
        if(taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }
}


