package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {
    private final Aurora plugin;
    private final StorageFileManager storageFileManager;

    private final ConcurrentMap<String, Boolean> toggleMap;

    public ToggleManager(Aurora plugin) {
        this.plugin = plugin;
        this.storageFileManager = this.plugin.getStorageFileManager();
        this.toggleMap = new ConcurrentHashMap<>();
    }

    public boolean isToggled(String playerName) {
        if (!this.toggleMap.containsKey(playerName)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    final boolean status = ToggleManager.this.storageFileManager.getDatabase().getToggleStatus(playerName);
                    ToggleManager.this.toggleMap.put(playerName, status);
                }
            }.runTaskAsynchronously(this.plugin);
        }

        return this.toggleMap.getOrDefault(playerName, false);
    }

    public void registerToggle(String playerName, boolean status) {
        this.toggleMap.put(playerName, status);
        new BukkitRunnable() {
            @Override
            public void run() {
                ToggleManager.this.storageFileManager.getDatabase().setToggleStatus(playerName, status);
            }
        }.runTaskAsynchronously(this.plugin);
    }

    public void cacheToggle(String playerName, boolean enabled) {
        this.toggleMap.put(playerName, enabled);
    }

    public void uncacheToggle(String playerName) {
        this.toggleMap.remove(playerName);
    }
}
