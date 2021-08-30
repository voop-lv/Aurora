package com.zenya.aurora.storage;

import com.zenya.aurora.Aurora;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {

  public static final ToggleManager INSTANCE = new ToggleManager();
  private static final ConcurrentMap<String, Boolean> TOGGLE_MAP = new ConcurrentHashMap<>();

  public Boolean isToggled(String playerName) {
    if (!TOGGLE_MAP.containsKey(playerName)) {
      new BukkitRunnable() {
        @Override
        public void run() {
          boolean status = StorageFileManager.getDatabase().getToggleStatus(playerName);
          TOGGLE_MAP.put(playerName, status);
        }
      }.runTask(Aurora.getInstance());
    }
    return TOGGLE_MAP.getOrDefault(playerName, false);
  }

  public void registerToggle(String playerName, boolean status) {
    TOGGLE_MAP.put(playerName, status);
    new BukkitRunnable() {
      @Override
      public void run() {
        StorageFileManager.getDatabase().setToggleStatus(playerName, status);
      }
    }.runTask(Aurora.getInstance());
  }

  public void cacheToggle(String playerName, boolean enabled) {
    TOGGLE_MAP.put(playerName, enabled);
  }

  public void uncacheToggle(String playerName) {
    TOGGLE_MAP.remove(playerName);
  }
}
