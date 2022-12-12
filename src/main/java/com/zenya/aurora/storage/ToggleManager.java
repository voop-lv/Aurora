package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.DBFile;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {

    private final Aurora plugin;
    private final StorageFileManager storageFileManager;

    private final ConcurrentMap<String, Boolean> toggleMap;

    public ToggleManager(Aurora plugin) {
        this.plugin = plugin;
        storageFileManager = plugin.getStorageFileManager();
        toggleMap = new ConcurrentHashMap<>();
    }

    public boolean isToggled(String playerName) {
        if (!toggleMap.containsKey(playerName)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    DBFile database = storageFileManager.getDatabase();
                    if (database == null) {
                        plugin.getLogger().warning("StorageFileManager DBFile is null");
                        return;
                    }
                    boolean status = database.getToggleStatus(playerName);
                    toggleMap.put(playerName, status);
                }
            }.runTaskAsynchronously(plugin);
        }

        return toggleMap.getOrDefault(playerName, false);
    }

    public void registerToggle(String playerName, boolean status) {
        toggleMap.put(playerName, status);
        new BukkitRunnable() {
            @Override
            public void run() {
                storageFileManager.getDatabase().setToggleStatus(playerName, status);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void cacheToggle(String playerName, boolean enabled) {
        toggleMap.put(playerName, enabled);
    }

    public void uncacheToggle(String playerName) {
        toggleMap.remove(playerName);
    }
}
