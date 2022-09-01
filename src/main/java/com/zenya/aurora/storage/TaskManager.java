package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class TaskManager {
    private HashMap<TaskKey, AuroraTask> taskMap = new HashMap<>();

    public TaskManager(Aurora plugin) {
        registerTask(TaskKey.TRACK_TPS_TASK, new TrackTPSTask(plugin));
        registerTask(TaskKey.TRACK_LOCATION_TASK, new TrackLocationTask(plugin));
    }

    public <T extends AuroraTask> T getTask(TaskKey key, Class<T> taskClass) {
        return (T) taskMap.get(key);
    }

    public <T extends AuroraTask> T getTask(TaskKey key) {
        return (T) taskMap.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        taskMap.put(key, task);
    }

    public void unregisterTasks() {
        for (Iterator<AuroraTask> iterator = taskMap.values().iterator(); iterator.hasNext();) {
            AuroraTask task = iterator.next();
            for (BukkitTask t : task.getTasks()) {
                t.cancel();
            }
            iterator.remove();
        }
    }
}
