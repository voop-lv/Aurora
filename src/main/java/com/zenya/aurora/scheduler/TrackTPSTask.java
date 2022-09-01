package com.zenya.aurora.scheduler;

import com.zenya.aurora.Aurora;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class TrackTPSTask implements AuroraTask {
    private final Aurora plugin;
    private BukkitTask runnables[];
    private float instTps = 0;
    private float avgTps = 0;

    public TrackTPSTask(Aurora plugin) {
        this.plugin = plugin;
        runTasks();
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.TRACK_TPS_TASK;
    }

    @Override
    public void runTasks() {
        //Task to get instant TPS
        BukkitTask task1 = new BukkitRunnable() {
            long start = 0;
            long now = 0;

            @Override
            public void run() {
                start = now;
                now = System.currentTimeMillis();
                long tdiff = now - start;

                if (tdiff > 0) {
                    instTps = (float) (1000 / tdiff);
                }
            }
        }.runTaskTimer(this.plugin, 0, 1);

        //Task to populate avgTps
        BukkitTask task2 = new BukkitRunnable() {
            ArrayList<Float> tpsList = new ArrayList<>();

            @Override
            public void run() {
                Float totalTps = 0f;

                tpsList.add(instTps);
                //Remove old tps after 15s
                if (tpsList.size() >= 15) {
                    tpsList.remove(0);
                }
                for (Float f : tpsList) {
                    totalTps += f;
                }
                avgTps = totalTps / tpsList.size();
            }
        }.runTaskTimerAsynchronously(this.plugin, 20, 20);

        //Add to runnables[]
        runnables = new BukkitTask[]{task1, task2};
    }

    @Override
    public BukkitTask[] getTasks() {
        return runnables;
    }

    public float getInstantTps() {
        if (instTps > 20.0f) {
            instTps = 20.0f;
        }
        return instTps;
    }

    public float getAverageTps() {
        if (avgTps > 20.0f) {
            avgTps = 20.0f;
        }
        return avgTps;
    }
}
