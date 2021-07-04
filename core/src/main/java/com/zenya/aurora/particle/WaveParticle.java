package com.zenya.aurora.particle;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class WaveParticle extends ComplexParticleTask {

    public WaveParticle(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.WAVE_PARTICLE;
    }

    @Override
    public BukkitTask generate() {
        return AuroraAPI.getAPI().getParticleFactory().createWave(locs[locIndex], locs[locIndex+1], rate.generateDouble(), update.generateInt(), duration.generateLong(), waveCycles.generateDouble(), waveAmplitude.generateDouble(), rotationAngle.generateDouble(), rotationAxis, display);
    }
}