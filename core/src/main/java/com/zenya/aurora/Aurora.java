package com.zenya.aurora;

import com.zenya.aurora.api.LightAPI;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.event.Listeners;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.storage.TaskManager;
import com.zenya.aurora.storage.ParticleManager;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class Aurora extends JavaPlugin {
    @Getter private static Aurora instance;
    private LightAPI lightAPI;
    private TaskManager taskManager;
    private StorageFileManager storageFileManager;
    private ParticleFileManager particleFileManager;
    private ParticleFileCache particleFileCache;

    @Override
    public void onEnable() {
        instance = this;

        //Init LightAPI
        lightAPI = LightAPI.INSTANCE;

        //Register all runnables
        taskManager = TaskManager.INSTANCE;

        //Init all configs and particle files
        storageFileManager = StorageFileManager.INSTANCE;
        particleFileManager = ParticleFileManager.INSTANCE;
        particleFileCache = ParticleFileCache.INSTANCE;

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands
        this.getCommand("aurora").setExecutor(new AuroraCommand());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(instance);
        taskManager.unregisterTasks();
        ParticleManager pm = ParticleManager.INSTANCE;
        final Set<Player> players = pm.getPlayers();
        for(Player player : players) {
            pm.unregisterTasks(player);
        }
        lightAPI.disable();
    }
}
