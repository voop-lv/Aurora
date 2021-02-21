package com.zenya.aurora;

import com.zenya.aurora.api.LightAPI;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.event.Listeners;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.YAMLFileManager;
import com.zenya.aurora.storage.TaskManager;
import com.zenya.aurora.storage.ParticleManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Aurora extends JavaPlugin {
    private static Aurora instance;
    private static LightAPI lightAPI;

    @Override
    public void onEnable() {
        instance = this;

        //Init LightAPI
        lightAPI = LightAPI.getLightAPI();

        //Register all runnables
        TaskManager.getInstance();

        //Init all configs and particle files
        YAMLFileManager.getInstance();
        ParticleFileManager.getInstance();
        ParticleFileCache.getInstance();

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands TODO
        this.getCommand("aurora").setExecutor(new AuroraCommand());
    }

    @Override
    public void onDisable() {
        ParticleManager pm = ParticleManager.getInstance();
        for(Player player : pm.getPlayers()) {
            pm.unregisterTasks(player);
        }
        lightAPI.disable();
    }

    public static Aurora getInstance() {
        return instance;
    }
}
