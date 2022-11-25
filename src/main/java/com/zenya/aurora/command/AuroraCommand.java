package com.zenya.aurora.command;

import com.github.ipecter.rtu.biomelib.RTUBiomeLib;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.event.ParticleUpdateEvent;
import com.zenya.aurora.util.ext.LightAPI;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.LocationTools;
import com.zenya.aurora.util.ChatBuilder;
import com.zenya.aurora.util.ChunkContainer;
import com.zenya.aurora.worldguard.AmbientParticlesFlag;
import com.zenya.aurora.worldguard.WGManager;
import optic_fusion1.aurora.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AuroraCommand implements CommandExecutor {

    private final Aurora plugin;
    private final StorageFileManager storageFileManager;
    private final ToggleManager toggleManager;
    private final LightAPI lightAPI;
    private final ParticleFileManager particleFileManager;
    private final ParticleFileCache particleFileCache;
    private final WGManager wgManager;
    private final AmbientParticlesFlag ambientParticlesFlag;

    public AuroraCommand(Aurora plugin) {
        this.plugin = plugin;
        storageFileManager = plugin.getStorageFileManager();
        toggleManager = plugin.getToggleManager();
        lightAPI = plugin.getLightAPI();
        particleFileManager = plugin.getParticleFileManager();
        particleFileCache = plugin.getParticleFileCache();
        wgManager = plugin.getWorldGuardManager();
        ambientParticlesFlag = plugin.getAmbientParticlesFlag();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);

        //No command arguments
        if (args.length < 1) {
            chat.sendMessages("command.help");
            return true;
        }

        //No permission
        if (!sender.hasPermission("aurora.command." + args[0])) {
            chat.sendMessages("no-permission");
            return true;
        }

        //help, toggle, reload, status
        if (args.length == 1) {
            if (args[0].toLowerCase().equals("help")) {
                chat.sendMessages("command.help");
                return true;
            }

            if (args[0].toLowerCase().equals("toggle")) {
                if (!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;
                chat.withPlayer(player);
                if (toggleManager.isToggled(player.getName())) {
                    toggleManager.registerToggle(player.getName(), false);
                    chat.sendMessages("command.toggle.disable");
                } else {
                    toggleManager.registerToggle(player.getName(), true);
                    chat.sendMessages("command.toggle.enable");
                }
                Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
                return true;
            }

            if (args[0].toLowerCase().equals("reload")) {
                storageFileManager.reloadFiles();
                if (!storageFileManager.getConfig().getBool("enable-lighting")) {
                    try {
                        lightAPI.disable();
                    } catch (NoClassDefFoundError exc) {
                        // Already disabled, do nothing
                    }
                }

                particleFileCache.reload();
                chat.withArgs(particleFileManager.getParticles().size()).sendMessages("command.reload");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(p));
                }
                return true;
            }

            if (args[0].toLowerCase().equals("status")) {
                String globalFiles = "";
                if (particleFileManager.getParticles() == null || particleFileManager.getParticles().size() == 0) {
                    //No particle files
                    globalFiles += "None";
                } else {
                    for (ParticleFile particleFile : particleFileManager.getParticles()) {
                        //Check if enabled or disabled
                        String particleName = particleFile.isEnabled() ? Colorize.colorize("&a") : Colorize.colorize("&c");
                        //If enabled, check if active in region/biome
                        if (particleFile.isEnabled() && sender instanceof Player) {
                            Player player = (Player) sender;
                            String biomeName = RTUBiomeLib.getInterface().getBiomeName(player.getLocation()).toUpperCase();

                            //WG support
                            if (wgManager.getWorldGuard() != null) {
                                if (ambientParticlesFlag.getParticles(player).contains(particleFile)) {
                                    //Set if particle is in region
                                    particleName = Colorize.colorize("&b");
                                } else if (ambientParticlesFlag.getParticles(player).isEmpty()
                                        && particleFileCache.getClass(biomeName).contains(particleFile)) {
                                    //If region has no particles, fallback to biome
                                    particleName = Colorize.colorize("&b");
                                }
                                //No WG
                            } else if (particleFileCache.getClass(biomeName).contains(particleFile)) {
                                //Only set if particle is in biome
                                particleName = Colorize.colorize("&b");
                            }

                            //Check if disabled in world
                            if (storageFileManager.getConfig().listContains("disabled-worlds", player.getWorld().getName())) {
                                particleName = Colorize.colorize("&c");
                            }
                        }
                        particleName += (particleFile.getName() + Colorize.colorize("&f, "));
                        globalFiles += particleName;
                    }
                }
                chat.withArgs(particleFileManager.getParticles().size(), globalFiles).sendMessages("command.status");
                return true;
            }

            //Wrong arg1
            chat.sendMessages("command.help");
            return true;
        }

        //toggle, fixlighting
        if (args.length == 2) {
            if (args[0].toLowerCase().equals("toggle")) {
                if (!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;

                switch (args[1].toLowerCase()) {
                    case "on" -> {
                        toggleManager.registerToggle(player.getName(), true);
                        chat.sendMessages("command.toggle.enable");
                    }
                    case "off" -> {
                        toggleManager.registerToggle(player.getName(), false);
                        chat.sendMessages("command.toggle.disable");
                    }
                    default -> {
                        //Wrong arg2 for toggle
                        chat.sendMessages("command.help");
                        return true;
                    }
                }
                Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
                return true;
            }

            if (args[0].toLowerCase().equals("fixlighting")) {
                if (!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;

                try {
                    int chunks = (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 10) ? Integer.parseInt(args[1]) : 10;
                    chat.sendMessages("command.fixlighting.start");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (ChunkContainer container : LocationTools.getSurroundingChunks(player.getLocation().getChunk(), chunks)) {
                                container.getWorld().refreshChunk(container.getX(), container.getZ());
                            }
                            chat.withArgs(chunks, chunks).sendMessages("command.fixlighting.done");
                        }
                    }.runTask(plugin);
                } catch (NumberFormatException exc) {
                    //Wrong arg2
                    chat.sendMessages("command.help");
                }
                return true;
            }

            //Wrong arg1
            chat.sendMessages("command.help");
            return true;
        }
        //Incorrect number of args
        chat.sendMessages("command.help");
        return true;
    }
}
