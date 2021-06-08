package com.zenya.aurora.scheduler;

import org.bukkit.scheduler.BukkitTask;

public interface AuroraTask {
    TaskKey getKey();
    void runTasks();
    BukkitTask[] getTasks();
}
