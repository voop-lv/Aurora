package com.zenya.aurora;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.api.ParticleFactory;
import com.zenya.aurora.command.AuroraTab;
import com.zenya.aurora.util.ext.LightAPI;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.event.Listeners;
import com.zenya.aurora.storage.ParticleFileCache;
import com.zenya.aurora.storage.ParticleFileManager;
import com.zenya.aurora.storage.ParticleManager;
import com.zenya.aurora.storage.StorageFileManager;
import com.zenya.aurora.storage.TaskManager;
import com.zenya.aurora.storage.ToggleManager;
import com.zenya.aurora.util.ext.ZParticle;
import com.zenya.aurora.util.Logger;
import com.zenya.aurora.worldguard.AmbientParticlesFlag;
import com.zenya.aurora.worldguard.WGManager;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Aurora extends JavaPlugin {

    private StorageFileManager storageFileManager;
    private ToggleManager toggleManager;
    private LightAPI lightAPI;
    private ParticleFileManager particleFileManager;
    private ParticleFileCache particleFileCache;
    private ParticleManager particleManager;
    private TaskManager taskManager;
    private WGManager wgManager;
    private AmbientParticlesFlag ambientParticlesFlag;

    @Override
    public void onLoad() {
        //WorldGuard dependency
        wgManager = new WGManager();
        if (wgManager.getWorldGuard() != null) {
            try {
                ambientParticlesFlag = new AmbientParticlesFlag(this);
            } catch (Exception exc) {
                Logger.logError("PlugMan or /reload is not supported by Aurora");
                Logger.logError("If you're updating your particle configs, use /aurora reload");
                Logger.logError("If you're updating the plugin version, restart your server");
                getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Override
    public void onEnable() {

        //Register API
        AuroraAPI.setAPI(new AuroraAPIImpl(this));

        //Init config, messages, biomes and db
        storageFileManager = new StorageFileManager(this);

        toggleManager = new ToggleManager(this);

        //Init LightAPI
        if (storageFileManager.getConfig().getBool("enable-lighting")) {
            lightAPI = new LightAPI();
        }

        //Init particle files
        particleFileManager = new ParticleFileManager(this);
        particleFileCache = new ParticleFileCache(this);
        particleManager = new ParticleManager();

        //Register all runnables
        taskManager = new TaskManager(this);

        //Register events
        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        //Register commands
        getCommand("aurora").setExecutor(new AuroraCommand(this));
        getCommand("aurora").setTabCompleter(new AuroraTab());
    }
    
    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        taskManager.unregisterTasks();
        for (Player player : particleManager.getPlayers()) {
            particleManager.unregisterTasks(player, true);
        }

        try {
            lightAPI.disable();
        } catch (NoClassDefFoundError exc) {
            //Silence errors
        }
    }

    public StorageFileManager getStorageFileManager() {
        return storageFileManager;
    }

    public ToggleManager getToggleManager() {
        return toggleManager;
    }

    public LightAPI getLightAPI() {
        return lightAPI;
    }

    public ParticleFileManager getParticleFileManager() {
        return particleFileManager;
    }

    public void reloadParticleFileManager() {
        particleFileManager = new ParticleFileManager(this);
    }

    public ParticleFileCache getParticleFileCache() {
        return particleFileCache;
    }

    public void reloadParticleFileCache() {
        particleFileCache = new ParticleFileCache(this);
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public WGManager getWorldGuardManager() {
        return wgManager;
    }

    public AmbientParticlesFlag getAmbientParticlesFlag() {
        return ambientParticlesFlag;
    }

    private class AuroraAPIImpl extends AuroraAPI {

        private ParticleFactory factory;

        public AuroraAPIImpl(Aurora plugin) {
            factory = new ZParticle(plugin);
        }

        @Override
        public ParticleFactory getParticleFactory() {
            return factory;
        }
    }
}
