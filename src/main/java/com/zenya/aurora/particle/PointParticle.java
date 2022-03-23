package com.zenya.aurora.particle;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PointParticle extends SimpleParticleTask {

    public PointParticle(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.POINT_PARTICLE;
    }

    @Override
    public BukkitTask generate() {
        return AuroraAPI.getAPI().getParticleFactory().createPoint(locs[locIndex], update.generateInt(), duration.generateLong(), display);
    }
}
