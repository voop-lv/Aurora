package com.zenya.aurora.particle;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class CubeParticle extends SimpleParticleTask {

    public CubeParticle(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.CUBE_PARTICLE;
    }

    @Override
    public BukkitTask generate() {
        return AuroraAPI.getAPI().getParticleFactory().createCube(locs[locIndex], length.generateDouble(), rate.generateDouble(), update.generateInt(), duration.generateLong(), rotationAngle.generateDouble(), rotationAxis, display);
    }
}