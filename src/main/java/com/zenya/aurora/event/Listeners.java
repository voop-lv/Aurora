package com.zenya.aurora.event;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.particle.CircleParticle;
import com.zenya.aurora.particle.CubeParticle;
import com.zenya.aurora.particle.LineParticle;
import com.zenya.aurora.particle.PointParticle;
import com.zenya.aurora.particle.RingParticle;
import com.zenya.aurora.particle.SphereParticle;
import com.zenya.aurora.particle.WaveParticle;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.StorageFileManager;
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
        toggleManager = plugin.getToggleManager();
        storageFileManager = plugin.getStorageFileManager();
        particleFileCache = plugin.getParticleFileCache();
        particleManager = plugin.getParticleManager();
        wgManager = plugin.getWorldGuardManager();
        ambientParticlesFlag = plugin.getAmbientParticlesFlag();
    }

    private void spawnParticles(ParticleUpdateEvent e, List<ParticleFile> particleFiles) {
        Player player = e.getPlayer();

        for (ParticleFile particleFile : particleFiles) {
            if (!particleFile.isEnabled()) {
                continue;
            }

            LocationTools.getParticleLocations(
                    e.getNearbyChunks(storageFileManager.getConfig().getInt("particle-spawn-radius")),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMinY() + player.getLocation().getY() : particleFile.getSpawning().getMinY(),
                    particleFile.getSpawning().isRelativePlayerPosition() ? particleFile.getSpawning().getMaxY() + player.getLocation().getY() : particleFile.getSpawning().getMaxY(),
                    particleFile.getSpawning().getSpawnDistance(),
                    particleFile.getSpawning().getRandMultiplier(),
                    particleFile.getSpawning().isShuffleLocations()).thenAcceptAsync(locs -> {

                switch (particleFile.getParticle().getParticleType().toUpperCase()) {
                    case "LINE" ->
                        particleManager.registerTask(player, new LineParticle(player, locs, particleFile));
                    case "CUBE" ->
                        particleManager.registerTask(player, new CubeParticle(player, locs, particleFile));
                    case "RING" ->
                        particleManager.registerTask(player, new RingParticle(player, locs, particleFile));
                    case "CIRCLE" ->
                        particleManager.registerTask(player, new CircleParticle(player, locs, particleFile));
                    case "SPHERE" ->
                        particleManager.registerTask(player, new SphereParticle(player, locs, particleFile));
                    case "WAVE" ->
                        particleManager.registerTask(player, new WaveParticle(player, locs, particleFile));
                    default -> //Default to point particle
                        particleManager.registerTask(player, new PointParticle(player, locs, particleFile));
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
                boolean status = storageFileManager.getDatabase().getToggleStatus(player.getName());
                toggleManager.cacheToggle(player.getName(), status);
                Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
            }
        }.runTask(plugin);
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        toggleManager.uncacheToggle(player.getName());
        Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
    }

    @EventHandler
    public void onParticleUpdateEvent(ParticleUpdateEvent e) {
        //Init variables
        Player player = e.getPlayer();
        Biome biome = player.getLocation().getBlock().getBiome();
        String biomeName = biome.toString();

        //Remove old tasks
        particleManager.unregisterTasks(player, false);

        //Ignore if spawn conditions are not met
        if (storageFileManager.getConfig().listContains("disabled-worlds", player.getWorld().getName())) {
            return;
        }
        if (!TimeCheck.isDuring(player.getPlayerTime())) {
            return;
        }

        //Ignore for disabled players
        if (!player.hasPermission("aurora.view")) {
            return;
        }
        if (!toggleManager.isToggled(player.getName())) {
            return;
        }

        //Register new tasks
        List<ParticleFile> biomeParticles = particleFileCache.getClass(biomeName);
        if (wgManager.getWorldGuard() != null) {
            List<ParticleFile> regionParticles = ambientParticlesFlag.getParticles(player);
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
