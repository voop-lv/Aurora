package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import com.zenya.aurora.file.DBFile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ToggleManager {
    public static final ToggleManager INSTANCE = new ToggleManager();
    private static final DBFile DB_FILE = StorageFileManager.INSTANCE.getDBFile("database.db");
    private Map<String, Boolean> toggleMap = Collections.synchronizedMap(new HashMap<>());

    public Boolean isToggled(String playerName) {
        boolean containsKey;
        synchronized (toggleMap) {
            containsKey = toggleMap.containsKey(playerName);
        }

        if (!containsKey) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> DB_FILE.getToggleStatus(playerName));
            future.thenAcceptAsync(status -> {
                synchronized (toggleMap) {
                    toggleMap.put(playerName, status);
                }
            });
        }

        synchronized (toggleMap) {
            return toggleMap.getOrDefault(playerName, false);
        }
    }

    public void registerToggle(String playerName, boolean enabled) {
        synchronized (toggleMap) {
            toggleMap.put(playerName, enabled);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                DB_FILE.setToggleStatus(playerName, enabled);
            }
        }.runTaskAsynchronously(Aurora.getInstance());
    }

    public void unregisterToggle(String playerName) {
        synchronized (toggleMap) {
            toggleMap.remove(playerName);
        }
    }
}