package com.zenya.aurora.scheduler;

import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public interface AuroraTask {
    TaskKey getKey();
    void runTasks();
    BukkitTask[] getTasks();
}
