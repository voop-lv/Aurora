package com.zenya.aurora.event;

import com.cryptomorin.xseries.XBiome;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.YAMLFileManager;
import com.zenya.aurora.scheduler.particle.*;
import com.zenya.aurora.util.LocationUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerChunkChangeEvent(PlayerChunkChangeEvent e) {
        //Init variables
        Player player = e.getPlayer();
        XBiome biome = XBiome.matchXBiome(player.getLocation().getBlock().getBiome());

        //Remove old tasks
        ParticleManager.INSTANCE.unregisterTasks(player);

        //Ignore for disabled players
        if(!player.hasPermission("aurora.view")) return;
        if(!ToggleManager.INSTANCE.isToggled(player.getName())) return;

        //Register new tasks
        if(ParticleFileCache.INSTANCE.getClass(biome) == null || ParticleFileCache.INSTANCE.getClass(biome).size() == 0) return;
        for(ParticleFile particleFile : ParticleFileCache.INSTANCE.getClass(biome)) {
            if(!particleFile.isEnabled()) continue;

            LocationUtils.getParticleLocations(
                    e.getNearbyChunks(YAMLFileManager.INSTANCE.getFile("config.yml").getInt("particle-spawn-radius")),
                    particleFile.getSpawning().getMinY(),
                    particleFile.getSpawning().getMaxY(),
                    particleFile.getSpawning().getSpawnDistance(),
                    particleFile.getSpawning().getRandMultiplier(),
                    particleFile.getSpawning().isShuffleLocations()).thenAcceptAsync(locs -> {

                switch(particleFile.getParticle().getParticleType().toUpperCase()) {
                    case "LINE":
                        ParticleManager.INSTANCE.registerTask(player, new LineParticle(player, locs, particleFile));
                        break;
                    case "CUBE":
                        ParticleManager.INSTANCE.registerTask(player, new CubeParticle(player, locs, particleFile));
                        break;
                    case "RING":
                        ParticleManager.INSTANCE.registerTask(player, new RingParticle(player, locs, particleFile));
                        break;
                    case "CIRCLE":
                        ParticleManager.INSTANCE.registerTask(player, new CircleParticle(player, locs, particleFile));
                        break;
                    case "SPHERE":
                        ParticleManager.INSTANCE.registerTask(player, new SphereParticle(player, locs, particleFile));
                        break;
                    case "WAVE":
                        ParticleManager.INSTANCE.registerTask(player, new WaveParticle(player, locs, particleFile));
                        break;
                    default:
                        //Default to point particle
                        ParticleManager.INSTANCE.registerTask(player, new PointParticle(player, locs, particleFile));
                }
            });
        }
    }
}
