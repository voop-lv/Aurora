package com.zenya.aurora.storage;

import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;
import com.zenya.aurora.util.Logger;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Iterator;

public class TaskManager {
    private static final String UUID = "%%__USER__%%";
    public static final TaskManager INSTANCE = new TaskManager();
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager() {
        if(UUID.startsWith("%")) {
            Logger.logInfo("Debug mode active :)");
        }
        registerTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.INSTANCE);
        registerTask(TaskKey.TRACK_LOCATION_TASK, TrackLocationTask.INSTANCE);
    }

    public AuroraTask getTask(TaskKey key) {
        return taskMap.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTasks() {
        for (Iterator<AuroraTask> iterator = taskMap.values().iterator(); iterator.hasNext(); ) {
            AuroraTask task = iterator.next();
            for(BukkitTask t : task.getTasks()) {
                t.cancel();
            }
            iterator.remove();
        }
    }
}


