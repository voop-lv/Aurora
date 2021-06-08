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
        BukkitTask drawShape = AuroraAPI.getAPI().getParticleFactory().createWave(locs[locIndex], locs[locIndex+1], rate.generateDouble(), update.generateInt(), duration.generateLong(), waveCycles.generateDouble(), waveAmplitude.generateDouble(), rotationAngle.generateDouble(), rotationAxis, display);
        super.runTasks(drawShape);
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.WAVE_PARTICLE;
    }
}