package com.zenya.aurora.event;

import com.cryptomorin.xseries.XBiome;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.file.YAMLFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.scheduler.particle.*;
import com.zenya.aurora.util.LocationTools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean status = StorageFileManager.INSTANCE.getDBFile("database.db").getToggleStatus(player.getName());
                    ToggleManager.INSTANCE.cacheToggle(player.getName(), status);
                    Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
                }
            }.runTask(Aurora.getInstance());
        }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        ToggleManager.INSTANCE.uncacheToggle(player.getName());
        Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
    }

    @EventHandler
    public void onParticleUpdateEvent(ParticleUpdateEvent e) {
        //Init variables
        Player player = e.getPlayer();
        XBiome biome;

        try {
            biome = XBiome.matchXBiome(player.getLocation().getBlock().getBiome());
        } catch(NullPointerException exc) {
            biome = XBiome.THE_VOID;
        }

        //Remove old tasks
        ParticleManager.INSTANCE.unregisterTasks(player);

        //Ignore if spawn conditions are not met
        YAMLFile config = StorageFileManager.INSTANCE.getYAMLFile("config.yml");
        ArrayList<String> disabledWorlds = config.getList("disabled-worlds");
        if(disabledWorlds != null && disabledWorlds.size() != 0 && disabledWorlds.contains(player.getWorld().getName())) return;
        if(player.getWorld().getTime() < config.getInt("start-spawning-at") || player.getWorld().getTime() > config.getInt("stop-spawning-at")) return;

        //Ignore for disabled players
        if(!player.hasPermission("aurora.view")) return;
        if(!ToggleManager.INSTANCE.isToggled(player.getName())) return;


        //Register new tasks
        if(ParticleFileCache.INSTANCE.getClass(biome) == null || ParticleFileCache.INSTANCE.getClass(biome).size() == 0) return;
        for(ParticleFile particleFile : ParticleFileCache.INSTANCE.getClass(biome)) {
            if(!particleFile.isEnabled()) continue;

            LocationTools.getParticleLocations(
                    e.getNearbyChunks(StorageFileManager.INSTANCE.getYAMLFile("config.yml").getInt("particle-spawn-radius")),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMinY() + player.getLocation().getY() : particleFile.getSpawning().getMinY(),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMaxY() + player.getLocation().getY() : particleFile.getSpawning().getMaxY(),
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
