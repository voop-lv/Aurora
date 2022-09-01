package com.zenya.aurora.util;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.YAMLFile;
import com.zenya.aurora.scheduler.TaskKey;
import com.zenya.aurora.scheduler.TrackTPSTask;
import com.zenya.aurora.storage.StorageFileManager;
import optic_fusion1.aurora.util.Colorize;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatBuilder {

    private String text;
    private Player player;
    private CommandSender sender;
    private List<String> args;

    public ChatBuilder() {
        this(null);
    }

    public ChatBuilder(String text) {
        this.text = text;
    }

    public ChatBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public ChatBuilder withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ChatBuilder withPlayer(String player) {
        this.player = Bukkit.getPlayer(player);
        return this;
    }

    public ChatBuilder withSender(CommandSender sender) {
        this.sender = sender;
        try {
            this.player = (Player) sender;
        } catch (ClassCastException exc) {
            player = null;
        }
        return this;
    }

    public <T extends Serializable> ChatBuilder withArgs(T... args) {
        this.args = new ArrayList<>();

        for (T arg : args) {
            this.args.add(arg.toString());
        }
        return this;
    }

    public String build() {
        //Placeholders
        text = text == null ? "" : Colorize.colorize(text);
        text = text.replaceAll("%tps%", Float.toString(Aurora.getPlugin(Aurora.class).getTaskManager().getTask(TaskKey.TRACK_TPS_TASK, TrackTPSTask.class).getAverageTps()));
        text = player == null ? text : text.replaceAll("%world%", player.getWorld().getName());
        text = player == null ? text : text.replaceAll("%player%", player.getName());

        if (args != null && !args.isEmpty()) {
            for (int i = 0; i < args.size(); i++) {
                text = text.replaceAll("%arg" + (i + 1) + "%", args.get(i));
            }
        }

        return text;
    }

    private void sendMessage(CommandSender sender) {
        this.sender = sender;
        sender.sendMessage(build());
    }

    private void sendMessage(Player player) {
        this.player = player;
        player.sendMessage(build());
    }

    public void sendMessage() {
        if (text == null || text.isEmpty()) {
            return;
        }

        if (player != null) {
            sendMessage(player);
        } else if (sender != null) {
            sendMessage(sender);
        }
    }

    public void sendMessages(String node) {
        final YAMLFile messages = Aurora.getPlugin(Aurora.class).getStorageFileManager().getMessages();
        if (messages.isList(node)) {
            for (String item : messages.getList(node)) {
                withText(item).sendMessage();
            }
            return;
        }

        withText(messages.getString(node)).sendMessage();
    }

}
