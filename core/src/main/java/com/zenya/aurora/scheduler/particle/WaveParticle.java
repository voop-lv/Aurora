package com.zenya.aurora.scheduler.particle;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.ext.LightAPI;
import com.zenya.aurora.util.RandomNumber;
import com.zenya.aurora.ext.ZParticle;
import com.zenya.aurora.ext.ZParticleDisplay;
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

public class WaveParticle implements ParticleTask {
    private BukkitTask runnables[];
    private Location locs[];
    private Player player;
    private Particle particle;
    private ZParticleDisplay display;
    private int maxCount;
    private boolean lighting;
    private RandomNumber<Double> rate;
    private RandomNumber<Integer> update;
    private RandomNumber<Long> duration;
    private RandomNumber<Double> waveCycles;
    private RandomNumber<Double> waveAmplitude;
    private RandomNumber<Double> rotationAngle;
    private char rotationAxis;

    public WaveParticle(Player player, Location[] locs, ParticleFile particleFile) {
        this.player = player;
        this.locs = locs;
        this.particle = Particle.valueOf(particleFile.getParticle().getParticleName());
        this.display = ZParticleDisplay.simple(player.getLocation(), particle, player);
        this.maxCount = particleFile.getParticle().getMaxCount();
        this.lighting = (LightAPI.isEnabled() && particleFile.getParticle().isEnableLighting());
        this.rate = particleFile.getProperties().getRate();
        this.update = particleFile.getProperties().getUpdate();
        this.duration = particleFile.getProperties().getDuration();
        this.waveCycles = particleFile.getProperties().getWaveCycles();
        this.waveAmplitude = particleFile.getProperties().getWaveAmplitude();
        this.rotationAngle = particleFile.getProperties().getRotationAngle();
        this.rotationAxis = particleFile.getProperties().getRotationAxis();
        runTasks();
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.WAVE_PARTICLE;
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
                    drawTasks.add(ZParticle.wave(locs[locIndex], locs[locIndex+1], rate.generateDouble(), update.generateInt(), duration.generateLong(), waveCycles.generateDouble(), waveAmplitude.generateDouble(), rotationAngle.generateDouble(), rotationAxis, display));

                    //Set lighting if enabled
                    if(lighting) {
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                LightAPI.setLight(locs[locIndex], LightType.BLOCK, 15, true);
                                LightAPI.setLight(locs[locIndex+1], LightType.BLOCK, 15, true);
                            }
                        }.runTask(Aurora.getInstance());
                    }

                    //Go to next index
                    if(locIndex < (locs.length-2)) {
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


