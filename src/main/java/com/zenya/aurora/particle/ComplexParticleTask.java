package com.zenya.aurora.particle;

import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.util.RandomNumber;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import ru.beykerykt.minecraft.lightapi.common.api.engine.LightFlag;

//For particles tasks which require setting lighting at multiple locations
public abstract class ComplexParticleTask extends ParticleTask {

    public RandomNumber<Double> waveCycles;
    public RandomNumber<Double> waveAmplitude;

    public ComplexParticleTask(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
        waveCycles = particleFile.getProperties().getWaveCycles();
        waveAmplitude = particleFile.getProperties().getWaveAmplitude();
        runTasks();
    }

    @Override
    public void runTasks() {
        //List to track existing particle groups
        List<BukkitTask> drawTasks = new ArrayList<>();

        //Task to set particle groups
        BukkitTask task1 = new BukkitRunnable() {
            @Override
            public void run() {
                //Remove expired tasks
                if (!drawTasks.isEmpty()) {
                    drawTasks.removeIf(drawTask -> !Bukkit.getScheduler().isQueued(drawTask.getTaskId()) && !Bukkit.getScheduler().isCurrentlyRunning(drawTask.getTaskId()));
                }

                //Manage total displayed particle groups
                if (drawTasks.size() < maxCount) {
                    //Create particles
                    drawTasks.add(generate());

                    //Set lighting if enabled
                    if (lighting) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                getLightAPI().setLight(locs[locIndex], LightFlag.BLOCK_LIGHTING, 15, true, false);
                                getLightAPI().setLight(locs[locIndex + 1], LightFlag.BLOCK_LIGHTING, 15, true, false);
                            }
                        }.runTask(getPlugin());
                    }

                    //Go to next index
                    if (locIndex < (locs.length - 2)) {
                        locIndex++;
                    } else {
                        locIndex = 0;
                    }
                }
            }
        }.runTaskTimerAsynchronously(getPlugin(), 0, update.generateInt());

        //Add to runnables[]
        runnables = new BukkitTask[]{task1};
    }
}
