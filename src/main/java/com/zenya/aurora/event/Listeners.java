package com.zenya.aurora.event;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.particle.*;
import com.zenya.aurora.util.LocationTools;
import com.zenya.aurora.util.TimeCheck;
import com.zenya.aurora.worldguard.AmbientParticlesFlag;
import com.zenya.aurora.worldguard.WGManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class Listeners implements Listener {
    private final Aurora plugin;
    private final ToggleManager toggleManager;
    private final StorageFileManager storageFileManager;
    private final ParticleFileCache particleFileCache;
    private final ParticleManager particleManager;
    private final WGManager wgManager;
    private final AmbientParticlesFlag ambientParticlesFlag;

    public Listeners(Aurora plugin) {
        this.plugin = plugin;
        this.toggleManager = this.plugin.getToggleManager();
        this.storageFileManager = this.plugin.getStorageFileManager();
        this.particleFileCache = this.plugin.getParticleFileCache();
        this.particleManager = this.plugin.getParticleManager();
        this.wgManager = this.plugin.getWorldGuardManager();
        this.ambientParticlesFlag = this.plugin.getAmbientParticlesFlag();
    }

    private void spawnParticles(ParticleUpdateEvent e, List<ParticleFile> particleFiles) {
        Player player = e.getPlayer();

        for (ParticleFile particleFile : particleFiles) {
            if (!particleFile.isEnabled()) {
                continue;
            }

            LocationTools.getParticleLocations(
                    e.getNearbyChunks(this.storageFileManager.getConfig().getInt("particle-spawn-radius")),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMinY() + player.getLocation().getY() : particleFile.getSpawning().getMinY(),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMaxY() + player.getLocation().getY() : particleFile.getSpawning().getMaxY(),
                    particleFile.getSpawning().getSpawnDistance(),
                    particleFile.getSpawning().getRandMultiplier(),
                    particleFile.getSpawning().isShuffleLocations()).thenAcceptAsync(locs -> {

                switch (particleFile.getParticle().getParticleType().toUpperCase()) {
                    case "LINE" ->
                        this.particleManager.registerTask(player, new LineParticle(player, locs, particleFile));
                    case "CUBE" ->
                        this.particleManager.registerTask(player, new CubeParticle(player, locs, particleFile));
                    case "RING" ->
                        this.particleManager.registerTask(player, new RingParticle(player, locs, particleFile));
                    case "CIRCLE" ->
                        this.particleManager.registerTask(player, new CircleParticle(player, locs, particleFile));
                    case "SPHERE" ->
                        this.particleManager.registerTask(player, new SphereParticle(player, locs, particleFile));
                    case "WAVE" ->
                        this.particleManager.registerTask(player, new WaveParticle(player, locs, particleFile));
                    default -> //Default to point particle
                        this.particleManager.registerTask(player, new PointParticle(player, locs, particleFile));
                }
            });
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean status = Listeners.this.storageFileManager.getDatabase().getToggleStatus(player.getName());
                Listeners.this.toggleManager.cacheToggle(player.getName(), status);
                Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
            }
        }.runTask(this.plugin);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        this.toggleManager.uncacheToggle(player.getName());
        Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
    }

    @EventHandler
    public void onParticleUpdateEvent(ParticleUpdateEvent e) {
        //Init variables
        Player player = e.getPlayer();
        Biome biome = player.getLocation().getBlock().getBiome();
        String biomeName = biome.toString();

        //Remove old tasks
        this.particleManager.unregisterTasks(player, false);

        //Ignore if spawn conditions are not met
        if (this.storageFileManager.getConfig().listContains("disabled-worlds", player.getWorld().getName())) {
            return;
        }
        if (!TimeCheck.isDuring(player.getPlayerTime())) {
            return;
        }

        //Ignore for disabled players
        if (!player.hasPermission("aurora.view")) {
            return;
        }
        if (!this.toggleManager.isToggled(player.getName())) {
            return;
        }

        //Register new tasks
        List<ParticleFile> biomeParticles = this.particleFileCache.getClass(biomeName);
        if (this.wgManager.getWorldGuard() != null) {
            List<ParticleFile> regionParticles = this.ambientParticlesFlag.getParticles(player);
            //WorldGuard support
            if (!regionParticles.isEmpty()) {
                spawnParticles(e, regionParticles);
            } else {
                if (biomeParticles != null && !biomeParticles.isEmpty()) {
                    spawnParticles(e, biomeParticles);
                }
            }
        } else {
            //No WorldGuard support
            if (biomeParticles != null && !biomeParticles.isEmpty()) {
                spawnParticles(e, biomeParticles);
            }
        }
    }
}
