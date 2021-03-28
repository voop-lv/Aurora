package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {
    public static final ToggleManager INSTANCE = new ToggleManager();
    private ConcurrentMap<String, Boolean> toggleMap = new ConcurrentHashMap<>();

    public Boolean isToggled(String playerName) {
        if(!toggleMap.containsKey(playerName)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean status = StorageFileManager.getDatabase().getToggleStatus(playerName);
                    toggleMap.put(playerName, status);
                }
            }.runTask(Aurora.getInstance());
        }
        return toggleMap.getOrDefault(playerName, false);
    }

    public void registerToggle(String playerName, boolean status) {
        toggleMap.put(playerName, status);
        new BukkitRunnable() {
            @Override
            public void run() {
                StorageFileManager.getDatabase().setToggleStatus(playerName, status);
            }
        }.runTask(Aurora.getInstance());
    }

    public void cacheToggle(String playerName, boolean enabled) {
        toggleMap.put(playerName, enabled);
    }

    public void uncacheToggle(String playerName) {
        toggleMap.remove(playerName);
    }
}