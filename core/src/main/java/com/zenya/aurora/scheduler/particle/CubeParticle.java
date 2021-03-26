package com.zenya.aurora.scheduler.particle;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.util.LightAPI;
import com.zenya.aurora.util.object.RandomNumber;
import com.zenya.aurora.util.ZParticle;
import com.zenya.aurora.util.ZParticleDisplay;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.beykerykt.lightapi.LightType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CubeParticle implements ParticleTask {
    private BukkitTask runnables[];
    private Location locs[];
    private Player player;
    private Particle particle;
    private ZParticleDisplay display;
    private int maxCount;
    private boolean lighting;
    private RandomNumber<Double> length;
    private RandomNumber<Double> rate;
    private RandomNumber<Integer> update;
    private RandomNumber<Long> duration;
    private RandomNumber<Double> rotationAngle;
    private char rotationAxis;

    public CubeParticle(Player player, Location[] locs, ParticleFile particleFile) {
        this.player = player;
        this.locs = locs;
        this.particle = Particle.valueOf(particleFile.getParticle().getParticleName());
        this.display = ZParticleDisplay.simple(player.getLocation(), particle, player);
        this.maxCount = particleFile.getParticle().getMaxCount();
        this.lighting = (LightAPI.isEnabled() && particleFile.getParticle().isEnableLighting());
        this.length = particleFile.getProperties().getLength();
        this.rate = particleFile.getProperties().getRate();
        this.update = particleFile.getProperties().getUpdate();
        this.duration = particleFile.getProperties().getDuration();
        this.rotationAngle = particleFile.getProperties().getRotationAngle();
        this.rotationAxis = particleFile.getProperties().getRotationAxis();
        runTasks();
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.CUBE_PARTICLE;
    }

    @Override
    public void runTasks() {
        //List to track existing particle groups
        List<BukkitTask> drawTasks = new ArrayList<>();

        //Task to set particle groups
        BukkitTask task1 = new BukkitRunnable() {
            int locIndex = 0;
            @Override
            public void run() {
                if(drawTasks.size() != 0) {
                    for (Iterator<BukkitTask> iterator = drawTasks.iterator(); iterator.hasNext(); ) {
                        BukkitTask drawTask = iterator.next();
                        if (!Bukkit.getScheduler().isQueued(drawTask.getTaskId()) && !Bukkit.getScheduler().isCurrentlyRunning(drawTask.getTaskId()))
                            iterator.remove();
                    }
                }

                //Manage total displayed particle groups
                if(drawTasks.size() < maxCount) {
                    //Create particles
                    drawTasks.add(ZParticle.cube(locs[locIndex], length.generateDouble(), rate.generateDouble(), update.generateInt(), duration.generateLong(), rotationAngle.generateDouble(), rotationAxis, display));

                    //Set lighting if enabled
                    if(lighting) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                LightAPI.setLight(locs[locIndex], LightType.BLOCK, 15, true);
                            }
                        }.runTask(Aurora.getInstance());
                    }

                    //Go to next index
                    if(locIndex < (locs.length-1)) {
                        locIndex++;
                    } else {
                        locIndex = 0;
                    }
                }
            }
        }.runTaskTimerAsynchronously(Aurora.getInstance(), 0, update.generateInt());

        //Add to runnables[]
        runnables = new BukkitTask[]{task1};
    }

    @Override
    public BukkitTask[] getTasks() {
        return runnables;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void killTasks() {
        for(BukkitTask t : runnables) {
            t.cancel();
        }

        if(lighting) {
            for(Location loc : locs) {
                LightAPI.clearLight(loc, LightType.BLOCK, true);
            }
        }
    }
}


