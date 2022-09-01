package com.zenya.aurora.command;

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
import org.bukkit.block.Biome;
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
        this.storageFileManager = this.plugin.getStorageFileManager();
        this.toggleManager = this.plugin.getToggleManager();
        this.lightAPI = this.plugin.getLightAPI();
        this.particleFileManager = this.plugin.getParticleFileManager();
        this.particleFileCache = this.plugin.getParticleFileCache();
        this.wgManager = plugin.getWorldGuardManager();
        this.ambientParticlesFlag = this.plugin.getAmbientParticlesFlag();
    }

    private void sendUsage(CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        chat.sendMessages("command.help");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);

        //No command arguments
        if (args.length < 1) {
            sendUsage(sender);
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
                sendUsage(sender);
                return true;
            }

            if (args[0].toLowerCase().equals("toggle")) {
                if (!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;
                chat.withPlayer(player);
                if (this.toggleManager.isToggled(player.getName())) {
                    this.toggleManager.registerToggle(player.getName(), false);
                    chat.sendMessages("command.toggle.disable");
                } else {
                    this.toggleManager.registerToggle(player.getName(), true);
                    chat.sendMessages("command.toggle.enable");
                }
                Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(player));
                return true;
            }

            if (args[0].toLowerCase().equals("reload")) {
                this.storageFileManager.reloadFiles();
                if (!this.storageFileManager.getConfig().getBool("enable-lighting")) {
                    try {
                        this.lightAPI.disable();
                    } catch (NoClassDefFoundError exc) {
                        // Already disabled, do nothing
                    }
                }

                this.particleFileCache.reload();
                chat.withArgs(this.particleFileManager.getParticles().size()).sendMessages("command.reload");

                for (Player p : Bukkit.getOnlinePlayers()) {
                    Bukkit.getPluginManager().callEvent(new ParticleUpdateEvent(p));
                }
                return true;
            }

            if (args[0].toLowerCase().equals("status")) {
                String globalFiles = "";
                if (this.particleFileManager.getParticles() == null || this.particleFileManager.getParticles().size() == 0) {
                    //No particle files
                    globalFiles += "None";
                } else {
                    for (ParticleFile particleFile : this.particleFileManager.getParticles()) {
                        //Check if enabled or disabled
                        String particleName = particleFile.isEnabled() ? Colorize.colorize("&a") : Colorize.colorize("&c");
                        //If enabled, check if active in region/biome
                        if (particleFile.isEnabled() && sender instanceof Player) {
                            Player player = (Player) sender;
                            Biome biome = player.getLocation().getBlock().getBiome();
                            String biomeName = biome.toString();

                            //WG support
                            if (this.wgManager.getWorldGuard() != null) {
                                if (this.ambientParticlesFlag.getParticles(player).contains(particleFile)) {
                                    //Set if particle is in region
                                    particleName = Colorize.colorize("&b");
                                } else if (this.ambientParticlesFlag.getParticles(player).isEmpty()
                                        && this.particleFileCache.getClass(biomeName).contains(particleFile)) {
                                    //If region has no particles, fallback to biome
                                    particleName = Colorize.colorize("&b");
                                }
                                //No WG
                            } else if (this.particleFileCache.getClass(biomeName).contains(particleFile)) {
                                //Only set if particle is in biome
                                particleName = Colorize.colorize("&b");
                            }

                            //Check if disabled in world
                            if (this.storageFileManager.getConfig().listContains("disabled-worlds", player.getWorld().getName())) {
                                particleName = Colorize.colorize("&c");
                            }
                        }
                        particleName += (particleFile.getName() + Colorize.colorize("&f, "));
                        globalFiles += particleName;
                    }
                }
                chat.withArgs(this.particleFileManager.getParticles().size(), globalFiles).sendMessages("command.status");
                return true;
            }

            //Wrong arg1
            sendUsage(sender);
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
                        this.toggleManager.registerToggle(player.getName(), true);
                        chat.sendMessages("command.toggle.enable");
                    }
                    case "off" -> {
                        this.toggleManager.registerToggle(player.getName(), false);
                        chat.sendMessages("command.toggle.disable");
                    }
                    default -> {
                        //Wrong arg2 for toggle
                        sendUsage(sender);
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
                    }.runTask(this.plugin);
                } catch (NumberFormatException exc) {
                    //Wrong arg2
                    sendUsage(sender);
                }
                return true;
            }

            //Wrong arg1
            sendUsage(sender);
            return true;
        }
        //Incorrect number of args
        sendUsage(sender);
        return true;
    }
}
