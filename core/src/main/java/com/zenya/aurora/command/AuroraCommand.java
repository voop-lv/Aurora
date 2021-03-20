package com.zenya.aurora.command;

import com.cryptomorin.xseries.XBiome;
import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.ParticleFile;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.util.ChatUtils;
import com.zenya.aurora.util.LocationUtils;
import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AuroraCommand implements CommandExecutor {

    private void sendUsage(CommandSender sender) {
        ChatUtils.sendMessage(sender, "&8&m*]----------[*&r &5Aurora &8&m*]----------[*&r");
        ChatUtils.sendMessage(sender, "&5/aurora help&f -&d Shows this help page");
        ChatUtils.sendMessage(sender, "&5/aurora toggle <on/off>&f -&d Toggle client-side ambient particle effects");
        ChatUtils.sendMessage(sender, "&5/aurora reload&f -&d Reload all plugin configs and particle files");
        ChatUtils.sendMessage(sender, "&5/aurora status&f -&d View information on enabled particles in the server");
        ChatUtils.sendMessage(sender, "&5/aurora fixlighting [radius]&f -&d Update and refresh lighting on [radius] nearby chunks");
        ChatUtils.sendMessage(sender, "&8&m*]------------------------------[*&r");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        //No command arguments
        if(args.length < 1) {
            sendUsage(sender);
            return true;
        }

        //No permission
        if(!sender.hasPermission("aurora.command." + args[0])) {
            ChatUtils.sendMessage(sender, "&4You do not have permission to use this command");
            return true;
        }

        //help, toggle, reload, status
        if(args.length == 1) {
            if(args[0].toLowerCase().equals("help")) {
                sendUsage(sender);
                return true;
            }

            if(args[0].toLowerCase().equals("toggle")) {
                if(!(sender instanceof Player)) {
                    ChatUtils.sendMessage(sender, "&cYou must be a player to use this command");
                    return true;
                }
                Player player = (Player) sender;
                if(ToggleManager.INSTANCE.isToggled(player.getName())) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                    ChatUtils.sendMessage(player, "&cAurora ambient particles have been disabled");
                } else {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                    ChatUtils.sendMessage(player, "&aAurora ambient particles have been enabled");
                }
                return true;
            }

            if(args[0].toLowerCase().equals("reload")) {
                StorageFileManager.INSTANCE.reloadFiles();
                ParticleFileCache.reload();
                ChatUtils.sendMessage(sender, String.format("&5Successfully reloaded config and &d%s &5particle files", ParticleFileManager.INSTANCE.getFiles().size()));
                return true;
            }

            if(args[0].toLowerCase().equals("status")) {
                String globalFiles = String.format("Particle Files (%s): ", ParticleFileManager.INSTANCE.getFiles().size());
                if(ParticleFileManager.INSTANCE.getFiles() == null || ParticleFileManager.INSTANCE.getFiles().size() == 0) {
                    //No particle files
                    globalFiles += "None";
                } else {
                    for(String fileName : ParticleFileManager.INSTANCE.getFiles()) {
                        ParticleFile particleFile = ParticleFileManager.INSTANCE.getClass(fileName);
                        //Check if enabled or disabled
                        String particleName = ParticleFileManager.INSTANCE.getClass(fileName).isEnabled() ? ChatUtils.translateColor("&a") : ChatUtils.translateColor("&c");
                        //If enabled, check if active in biome
                        if(particleFile.isEnabled() && sender instanceof Player) {
                            Player player = (Player) sender;
                            for(ParticleFile biomeParticleFile : ParticleFileCache.INSTANCE.getClass(XBiome.matchXBiome(player.getLocation().getBlock().getBiome()))) {
                                if(particleFile.getName().equals(biomeParticleFile.getName())) particleName = ChatUtils.translateColor("&b");
                            }
                        }
                        particleName += (particleFile.getName() + ChatUtils.translateColor("&f, "));
                        globalFiles += particleName;
                    }
                }
                ChatUtils.sendMessage(sender, "&bActive | &aEnabled &f| &cDisabled &f");
                ChatUtils.sendMessage(sender, globalFiles);
                return true;
            }

            //Wrong arg1
            sendUsage(sender);
            return true;
        }

        //toggle, fixlighting
        if(args.length == 2) {
            if(args[0].toLowerCase().equals("toggle")) {
                if(!(sender instanceof Player)) {
                    ChatUtils.sendMessage(sender, "&cYou must be a player to use this command");
                    return true;
                }
                Player player = (Player) sender;

                if (args[1].toLowerCase().equals("on")) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                    ChatUtils.sendMessage(player, "&aAurora ambient particles have been enabled");
                    return true;
                }

                if (args[1].toLowerCase().equals("off")) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                    ChatUtils.sendMessage(player, "&cAurora ambient particles have been disabled");
                    return true;
                }

                //Wrong arg2 for toggle
                sendUsage(sender);
                return true;
            }

            if(args[0].toLowerCase().equals("fixlighting")) {
                if(!(sender instanceof Player)) {
                    ChatUtils.sendMessage(sender, "&cYou must be a player to use this command");
                    return true;
                }
                Player player = (Player) sender;

                try {
                    int chunks = (Integer.parseInt(args[1]) > 0 && Integer.parseInt(args[1]) < 10) ? Integer.parseInt(args[1]) : 10;
                    ChatUtils.sendMessage(sender, "&5Attempting to fix lighting. This action may take a few seconds...");
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for(Chunk c : LocationUtils.getSurroundingChunks(player.getLocation().getChunk(), chunks)) {
                                c.getWorld().refreshChunk(c.getX(), c.getZ());
                            }
                            ChatUtils.sendMessage(sender, String.format("&5Successfully reloaded lighting in a &d%sx%s &5chunk radius", chunks, chunks));
                        }
                    }.runTask(Aurora.getInstance());
                } catch(NumberFormatException exc) {
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

