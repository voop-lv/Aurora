package com.zenya.aurora;

import com.zenya.aurora.api.AuroraAPI;
import com.zenya.aurora.api.ParticleFactory;
import com.zenya.aurora.command.AuroraTab;
import com.zenya.aurora.storage.*;
import com.zenya.aurora.util.ext.LightAPI;
import com.zenya.aurora.command.AuroraCommand;
import com.zenya.aurora.event.Listeners;
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
        this.wgManager = new WGManager();
        if (this.wgManager.getWorldGuard() != null) {
            try {
                this.ambientParticlesFlag = new AmbientParticlesFlag(this);
            } catch (Exception exc) {
                Logger.logError("PlugMan or /reload is not supported by Aurora");
                Logger.logError("If you're updating your particle configs, use /aurora reload");
                Logger.logError("If you're updating the plugin version, restart your server");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    @Override
    public void onEnable() {
        // Enables Metrics
        new MetricsLite(this, 12646);

        //Register API
        AuroraAPI.setAPI(new AuroraAPIImpl(this));

        this.toggleManager = new ToggleManager(this);

        //Init config, messages, biomes and db
        this.storageFileManager = new StorageFileManager(this);

        //Init LightAPI
        if (this.storageFileManager.getConfig().getBool("enable-lighting")) {
            this.lightAPI = new LightAPI();
        }

        //Init particle files
        this.particleFileManager = new ParticleFileManager(this);
        this.particleFileCache = new ParticleFileCache(this);
        this.particleManager = new ParticleManager();

        //Register all runnables
        //Spigot buyer ID check in here
        this.taskManager = new TaskManager(this);

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(this), this);

        //Register commands
        this.getCommand("aurora").setExecutor(new AuroraCommand(this));
        this.getCommand("aurora").setTabCompleter(new AuroraTab());
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.taskManager.unregisterTasks();
        for (Player player : this.particleManager.getPlayers()) {
            this.particleManager.unregisterTasks(player, true);
        }

        try {
            this.lightAPI.disable();
        } catch (NoClassDefFoundError exc) {
            //Silence errors
        }
    }

    public StorageFileManager getStorageFileManager() {
        return this.storageFileManager;
    }

    public ToggleManager getToggleManager() {
        return this.toggleManager;
    }

    public LightAPI getLightAPI() {
        return this.lightAPI;
    }

    public ParticleFileManager getParticleFileManager() {
        return this.particleFileManager;
    }

    public void reloadParticleFileManager() {
        this.particleFileManager = new ParticleFileManager(this);
    }

    public ParticleFileCache getParticleFileCache() {
        return this.particleFileCache;
    }

    public void reloadParticleFileCache() {
        this.particleFileCache = new ParticleFileCache(this);
    }

    public ParticleManager getParticleManager() {
        return this.particleManager;
    }

    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    public WGManager getWorldGuardManager() {
        return this.wgManager;
    }

    public AmbientParticlesFlag getAmbientParticlesFlag() {
        return this.ambientParticlesFlag;
    }

    private class AuroraAPIImpl extends AuroraAPI {

        private ParticleFactory factory;

        public AuroraAPIImpl(Aurora plugin) {
            this.factory = new ZParticle(plugin);
        }

        @Override
        public ParticleFactory getParticleFactory() {
            return factory;
        }
    }
}
