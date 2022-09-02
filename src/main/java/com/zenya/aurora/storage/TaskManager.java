package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.scheduler.AuroraTask;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackLocationTask;
import com.zenya.aurora.scheduler.TrackTPSTask;
import java.util.HashMap;
import java.util.Iterator;
import org.bukkit.scheduler.BukkitTask;

public class TaskManager {

    private static final HashMap<TaskKey, AuroraTask> TASK_MAP = new HashMap<>();

    public TaskManager(Aurora plugin) {
        registerTask(TaskKey.TRACK_TPS_TASK, new TrackTPSTask(plugin));
        registerTask(TaskKey.TRACK_LOCATION_TASK, new TrackLocationTask(plugin));
    }

    public <T extends AuroraTask> T getTask(TaskKey key, Class<T> taskClass) {
        return (T) TASK_MAP.get(key);
    }

    public <T extends AuroraTask> T getTask(TaskKey key) {
        return (T) TASK_MAP.get(key);
    }

    public void registerTask(TaskKey key, AuroraTask task) {
        TASK_MAP.put(key, task);
    }

    public void unregisterTasks() {
        for (Iterator<AuroraTask> iterator = TASK_MAP.values().iterator(); iterator.hasNext();) {
            AuroraTask task = iterator.next();
            for (BukkitTask t : task.getTasks()) {
                t.cancel();
            }
            iterator.remove();
        }
    }
}
