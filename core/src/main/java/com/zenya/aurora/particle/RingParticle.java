package com.zenya.aurora.particle;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.scheduler.TaskKey;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class RingParticle extends SimpleParticleTask {

    public RingParticle(Player player, Location[] locs, ParticleFile particleFile) {
        super(player, locs, particleFile);
        BukkitTask drawShape = AuroraAPI.getAPI().getParticleFactory().createRing(locs[locIndex], radius.generateDouble(), rate.generateDouble(), update.generateInt(), duration.generateLong(), rotationAngle.generateDouble(), rotationAxis, display);
        super.runTasks(drawShape);
    }

    @Override
    public TaskKey getKey() {
        return TaskKey.RING_PARTICLE;
    }
}